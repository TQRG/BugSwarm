package sic.vista.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.Producto;
import sic.modelo.RenglonFactura;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.IProductoService;
import sic.service.Movimiento;
import sic.service.BusinessServiceException;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class GUI_BuscarProductos extends JDialog {

    private final String tipoComprobante;
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<Producto> productos;
    private final List<RenglonFactura> renglonesFactura;
    private Producto prodSeleccionado;
    private RenglonFactura renglon;
    private boolean debeCargarRenglon;    
    private final Movimiento tipoMovimiento;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IProductoService productoService = appContext.getBean(IProductoService.class);
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);
    private final IFacturaService facturaService = appContext.getBean(IFacturaService.class);
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private static final Logger LOGGER = Logger.getLogger(GUI_BuscarProductos.class.getPackage().getName());

    public GUI_BuscarProductos(JDialog parent, boolean modal, List<RenglonFactura> renglones, Movimiento movimiento, String tipoComprobante) {
        this.setModal(modal);
        this.initComponents();
        this.setIcon();
        renglonesFactura = renglones;
        tipoMovimiento = movimiento;
        this.tipoComprobante = tipoComprobante;
        this.setSize(parent.getWidth() - 100, parent.getHeight() - 200);
        this.setLocationRelativeTo(parent);
        this.setColumnas();

        //listeners        
        txt_CampoBusqueda.addKeyListener(keyHandler);
        btn_Buscar.addKeyListener(keyHandler);
        tbl_Resultado.addKeyListener(keyHandler);
        ta_ObservacionesProducto.addKeyListener(keyHandler);
        txt_Cantidad.addKeyListener(keyHandler);
        txt_UnidadMedida.addKeyListener(keyHandler);
        txt_PorcentajeDescuento.addKeyListener(keyHandler);
        btn_Aceptar.addKeyListener(keyHandler);
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    public boolean debeCargarRenglon() {
        return debeCargarRenglon;
    }

    public RenglonFactura getRenglon() {
        return renglon;
    }
    
    public Producto getProductoSeleccionado(){
        return  this.prodSeleccionado;
    }

    private void prepararComponentes() {
        txt_Cantidad.setValue(1.00);
        txt_PorcentajeDescuento.setValue(0.0);
    }

    private void buscar() throws BusinessServiceException {
        int cantidadResultados = 250;
        productos = productoService.getProductosPorDescripcionQueContenga(
                txt_CampoBusqueda.getText().trim(), cantidadResultados,
                empresaService.getEmpresaActiva().getEmpresa());
        prodSeleccionado = null;
        txt_UnidadMedida.setText("");
    }

    private void aceptarProducto() {
        this.actualizarEstadoSeleccion();
        if (prodSeleccionado != null) {
            boolean existeStock = productoService.existeStockDisponible(prodSeleccionado.getId_Producto(), renglon.getCantidad());
            if (existeStock || tipoMovimiento == Movimiento.PEDIDO || tipoMovimiento == Movimiento.COMPRA) {
                debeCargarRenglon = true;
                this.dispose();
            } else if (!existeStock) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_sin_stock_suficiente"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            debeCargarRenglon = false;
            this.dispose();
        }
    }

    private void actualizarProductosCargadosEnFactura() {
        if (!(tipoMovimiento == Movimiento.PEDIDO || tipoMovimiento == Movimiento.COMPRA)) {
            for (RenglonFactura renglonFactura : renglonesFactura) {
                for (Producto producto : productos) {
                    if (renglonFactura.getDescripcionItem().equals(producto.getDescripcion()) && producto.isIlimitado() == false) {
                        producto.setCantidad(producto.getCantidad() - renglonFactura.getCantidad());
                    }
                }
            }
        }
    }

    private void actualizarEstadoSeleccion() {
        if (txt_Cantidad.isEditValid() && txt_PorcentajeDescuento.isEditValid()) {
            try {
                txt_Cantidad.commitEdit();
                txt_PorcentajeDescuento.commitEdit();
            } catch (ParseException ex) {
                String msjError = "Se produjo un error analizando los campos.";
                LOGGER.error(msjError + " - " + ex.getMessage());
            }
        }
        if (prodSeleccionado != null) {
            renglon = facturaService.calcularRenglon(this.tipoComprobante, tipoMovimiento,
                    Double.parseDouble(txt_Cantidad.getValue().toString()), prodSeleccionado,
                    Double.parseDouble(txt_PorcentajeDescuento.getValue().toString()));
        }
    }

    private void seleccionarProductoEnTable() {
        int fila = tbl_Resultado.getSelectedRow();
        if (fila != -1) {
            prodSeleccionado = productos.get(fila);
            txt_UnidadMedida.setText(prodSeleccionado.getMedida().getNombre());
            ta_ObservacionesProducto.setText(prodSeleccionado.getNota());
            this.actualizarEstadoSeleccion();
        }
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        for (Producto producto : productos) {
            Object[] fila = new Object[6];
            fila[0] = producto.getCodigo();
            fila[1] = producto.getDescripcion();
            fila[2] = producto.getCantidad();
            fila[3] = producto.isIlimitado();
            fila[4] = producto.getMedida();
            double precio = (tipoMovimiento == Movimiento.VENTA) ? producto.getPrecioLista()
                   : (tipoMovimiento == Movimiento.PEDIDO) ? producto.getPrecioLista()
                   : (tipoMovimiento == Movimiento.COMPRA) ? producto.getPrecioCosto() : 0.0;
            fila[5] = precio;
            modeloTablaResultados.addRow(fila);
        }
        tbl_Resultado.setModel(modeloTablaResultados);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultado.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[6];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripción";
        encabezados[2] = "Cantidad";
        encabezados[3] = "Sin Límite";
        encabezados[4] = "Unidad";
        String encabezadoPrecio = (tipoMovimiento == Movimiento.VENTA) ? "P. Lista"
                : (tipoMovimiento == Movimiento.PEDIDO) ? "P. Lista"
                : (tipoMovimiento == Movimiento.COMPRA) ? "P.Costo" : "";
        encabezados[5] = encabezadoPrecio;
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultado.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = Double.class;
        tipos[3] = Boolean.class;
        tipos[4] = String.class;
        tipos[5] = Double.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultado.getTableHeader().setReorderingAllowed(false);
        tbl_Resultado.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultado.setDefaultRenderer(Double.class, new RenderTabla());

        //Size de columnas
        //Codigo
        tbl_Resultado.getColumnModel().getColumn(0).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(0).setMaxWidth(130);

        //Descripcion
        //tbl_Resultado.getColumnModel().getColumn(1).setPreferredWidth(380);
        //Cantidad
        tbl_Resultado.getColumnModel().getColumn(2).setPreferredWidth(67);
        tbl_Resultado.getColumnModel().getColumn(2).setMaxWidth(67);

        //Sin limite
        tbl_Resultado.getColumnModel().getColumn(3).setPreferredWidth(70);
        tbl_Resultado.getColumnModel().getColumn(3).setMaxWidth(70);

        //Medida
        tbl_Resultado.getColumnModel().getColumn(4).setPreferredWidth(70);
        tbl_Resultado.getColumnModel().getColumn(4).setMaxWidth(70);

        //Precio
        tbl_Resultado.getColumnModel().getColumn(5).setPreferredWidth(80);
        tbl_Resultado.getColumnModel().getColumn(5).setMaxWidth(80);
    }

    /**
     * Clase interna para manejar las hotkeys
     */
    class HotKeysHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFondo = new javax.swing.JPanel();
        txt_CampoBusqueda = new javax.swing.JTextField();
        btn_Buscar = new javax.swing.JButton();
        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultado = new javax.swing.JTable();
        btn_Aceptar = new javax.swing.JButton();
        txt_UnidadMedida = new javax.swing.JTextField();
        lbl_Cantidad = new javax.swing.JLabel();
        lbl_Descuento = new javax.swing.JLabel();
        txt_Cantidad = new javax.swing.JFormattedTextField();
        txt_PorcentajeDescuento = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_ObservacionesProducto = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Productos");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        txt_CampoBusqueda.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_CampoBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_CampoBusquedaActionPerformed(evt);
            }
        });
        txt_CampoBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_CampoBusquedaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_CampoBusquedaKeyTyped(evt);
            }
        });

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_LupaBuscar.png"))); // NOI18N
        btn_Buscar.setFocusable(false);
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        tbl_Resultado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_Resultado.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl_Resultado.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbl_Resultado.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resultado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResultadoMouseClicked(evt);
            }
        });
        tbl_Resultado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbl_ResultadoFocusGained(evt);
            }
        });
        tbl_Resultado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResultadoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbl_ResultadoKeyReleased(evt);
            }
        });
        sp_Resultado.setViewportView(tbl_Resultado);

        btn_Aceptar.setForeground(java.awt.Color.blue);
        btn_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btn_Aceptar.setFocusable(false);
        btn_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AceptarActionPerformed(evt);
            }
        });

        txt_UnidadMedida.setEditable(false);
        txt_UnidadMedida.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_UnidadMedida.setFocusable(false);

        lbl_Cantidad.setText("Cantidad:");

        lbl_Descuento.setText("Descuento (%):");

        txt_Cantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        txt_Cantidad.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_Cantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_CantidadFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_CantidadFocusLost(evt);
            }
        });
        txt_Cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_CantidadKeyPressed(evt);
            }
        });

        txt_PorcentajeDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        txt_PorcentajeDescuento.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_PorcentajeDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_PorcentajeDescuentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_PorcentajeDescuentoFocusLost(evt);
            }
        });
        txt_PorcentajeDescuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_PorcentajeDescuentoKeyReleased(evt);
            }
        });

        ta_ObservacionesProducto.setColumns(20);
        ta_ObservacionesProducto.setEditable(false);
        ta_ObservacionesProducto.setLineWrap(true);
        ta_ObservacionesProducto.setRows(4);
        ta_ObservacionesProducto.setFocusable(false);
        jScrollPane1.setViewportView(ta_ObservacionesProducto);

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_Resultado)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(txt_CampoBusqueda)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Buscar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_Descuento)
                            .addComponent(lbl_Cantidad))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txt_PorcentajeDescuento)
                            .addComponent(txt_Cantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_UnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Aceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Buscar)
                    .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_CampoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultado, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl_Cantidad)
                            .addComponent(txt_Cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_UnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl_Descuento)
                            .addComponent(txt_PorcentajeDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btn_Aceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Buscar, txt_CampoBusqueda});

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_Cantidad, txt_PorcentajeDescuento, txt_UnidadMedida});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AceptarActionPerformed
        this.aceptarProducto();
    }//GEN-LAST:event_btn_AceptarActionPerformed

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        try {
            this.buscar();
            this.actualizarProductosCargadosEnFactura();
            this.cargarResultadosAlTable();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void tbl_ResultadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadoMouseClicked
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadoMouseClicked

    private void txt_CantidadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantidadFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txt_CantidadFocusLost

    private void txt_PorcentajeDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PorcentajeDescuentoFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txt_PorcentajeDescuentoFocusLost

    private void txt_CampoBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_CampoBusquedaActionPerformed
        try {
            this.buscar();
            this.actualizarProductosCargadosEnFactura();
            this.cargarResultadosAlTable();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        } 
    }//GEN-LAST:event_txt_CampoBusquedaActionPerformed

    private void tbl_ResultadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadoKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        }
        if (evt.getKeyCode() == 9) {
            txt_Cantidad.requestFocus();
        }
    }//GEN-LAST:event_tbl_ResultadoKeyPressed

    private void tbl_ResultadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadoKeyReleased
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadoKeyReleased

    private void txt_PorcentajeDescuentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_PorcentajeDescuentoKeyReleased
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txt_PorcentajeDescuentoKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        prodSeleccionado = null;
    }//GEN-LAST:event_formWindowClosing

    private void txt_CampoBusquedaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_CampoBusquedaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txt_CampoBusquedaKeyTyped

    private void txt_CampoBusquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_CampoBusquedaKeyReleased
        try {
            this.buscar();
            this.actualizarProductosCargadosEnFactura();
            this.cargarResultadosAlTable();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        } 
    }//GEN-LAST:event_txt_CampoBusquedaKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            this.prepararComponentes();
            this.buscar();
            this.actualizarProductosCargadosEnFactura();
            this.cargarResultadosAlTable();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        } 
    }//GEN-LAST:event_formWindowOpened

    private void txt_CantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantidadFocusGained
        this.seleccionarProductoEnTable();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_Cantidad.selectAll();
            }
        });
    }//GEN-LAST:event_txt_CantidadFocusGained

    private void txt_CantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_CantidadKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txt_CantidadKeyPressed

    private void txt_PorcentajeDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PorcentajeDescuentoFocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_PorcentajeDescuento.selectAll();
            }
        });
    }//GEN-LAST:event_txt_PorcentajeDescuentoFocusGained

    private void tbl_ResultadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadoFocusGained
        //Si no hay nada seleccionado y NO esta vacio el table, selecciona la primer fila
        if ((tbl_Resultado.getSelectedRow() == -1) && (tbl_Resultado.getRowCount() != 0)) {
            tbl_Resultado.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadoFocusGained
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Aceptar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_Cantidad;
    private javax.swing.JLabel lbl_Descuento;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTextArea ta_ObservacionesProducto;
    private javax.swing.JTable tbl_Resultado;
    private javax.swing.JTextField txt_CampoBusqueda;
    private javax.swing.JFormattedTextField txt_Cantidad;
    private javax.swing.JFormattedTextField txt_PorcentajeDescuento;
    private javax.swing.JTextField txt_UnidadMedida;
    // End of variables declaration//GEN-END:variables
}
