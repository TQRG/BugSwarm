package sic.vista.swing;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.BusquedaProductoCriteria;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rubro;
import sic.service.IEmpresaService;
import sic.service.IProductoService;
import sic.service.IProveedorService;
import sic.service.IRubroService;
import sic.service.BusinessServiceException;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class GUI_Productos extends JInternalFrame {

    private ModeloTabla modeloTablaResultados;
    private List<Producto> productos;
    private boolean listarSoloFaltantes;
    private final int cantidadResultadosParaMostrar = 0; //deshabilitada momentaneamente
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IRubroService rubroService = appContext.getBean(IRubroService.class);
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);
    private final IProveedorService proveedorService = appContext.getBean(IProveedorService.class);
    private final IProductoService productoService = appContext.getBean(IProductoService.class);
    private static final Logger LOGGER = Logger.getLogger(GUI_Productos.class.getPackage().getName());

    public GUI_Productos() {
        this.initComponents();
        this.setSize(750, 450);
        modeloTablaResultados = new ModeloTabla();
        rb_Todos.setSelected(true);
        listarSoloFaltantes = false;
    }

    private void cargarComboBoxRubros() {
        Rubro rubroSeleccionado = (Rubro) cmb_Rubro.getSelectedItem();
        List<Rubro> rubros;
        cmb_Rubro.removeAllItems();
        rubros = rubroService.getRubros(empresaService.getEmpresaActiva().getEmpresa());
        for (Rubro r : rubros) {
            cmb_Rubro.addItem(r);
        }
        if (rubroSeleccionado != null && rubros.contains(rubroSeleccionado)) {
            cmb_Rubro.setSelectedItem(rubroSeleccionado);
        }
    }

    private void cargarComboBoxProveedores() {
        Proveedor proveedorSeleccionado = (Proveedor) cmb_Proveedor.getSelectedItem();
        List<Proveedor> proveedores;
        cmb_Proveedor.removeAllItems();
        proveedores = proveedorService.getProveedores(empresaService.getEmpresaActiva().getEmpresa());
        for (Proveedor prov : proveedores) {
            cmb_Proveedor.addItem(prov);
        }
        if (proveedorSeleccionado != null && proveedores.contains(proveedorSeleccionado)) {
            cmb_Proveedor.setSelectedItem(proveedorSeleccionado);
        }
    }

    private void setColumnas() {
        //sorting
        tbl_Resultados.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[23];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripcion";
        encabezados[2] = "Cantidad";
        encabezados[3] = "Cant. Minima";
        encabezados[4] = "Sin Límite";
        encabezados[5] = "Medida";
        encabezados[6] = "Precio Costo";
        encabezados[7] = "% Ganancia";
        encabezados[8] = "Ganancia";
        encabezados[9] = "PVP";
        encabezados[10] = "% IVA";
        encabezados[11] = "IVA";
        encabezados[12] = "% Imp.Interno";
        encabezados[13] = "Imp.Interno";
        encabezados[14] = "Precio Lista";
        encabezados[15] = "Rubro";
        encabezados[16] = "Fecha U. Modificacion";
        encabezados[17] = "Estanteria";
        encabezados[18] = "Estante";
        encabezados[19] = "Proveedor";
        encabezados[20] = "Fecha Alta";
        encabezados[21] = "Fecha Vencimiento";
        encabezados[22] = "Nota";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = Double.class;
        tipos[3] = Double.class;
        tipos[4] = Boolean.class;
        tipos[5] = String.class;
        tipos[6] = Double.class;
        tipos[7] = Double.class;
        tipos[8] = Double.class;
        tipos[9] = Double.class;
        tipos[10] = Double.class;
        tipos[11] = Double.class;
        tipos[12] = Double.class;
        tipos[13] = Double.class;
        tipos[14] = Double.class;
        tipos[15] = String.class;
        tipos[16] = Date.class;
        tipos[17] = String.class;
        tipos[18] = String.class;
        tipos[19] = String.class;
        tipos[20] = Date.class;
        tipos[21] = Date.class;
        tipos[22] = String.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(400);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(80);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(15).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(16).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(17).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(18).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(19).setPreferredWidth(450);
        tbl_Resultados.getColumnModel().getColumn(20).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(21).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(22).setPreferredWidth(1000);
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        for (Producto producto : productos) {
            Object[] fila = new Object[23];
            fila[0] = producto.getCodigo();
            fila[1] = producto.getDescripcion();
            fila[2] = producto.getCantidad();
            fila[3] = producto.getCantMinima();
            fila[4] = producto.isIlimitado();
            fila[5] = producto.getMedida().getNombre();
            fila[6] = producto.getPrecioCosto();
            fila[7] = producto.getGanancia_porcentaje();
            fila[8] = producto.getGanancia_neto();
            fila[9] = producto.getPrecioVentaPublico();
            fila[10] = producto.getIva_porcentaje();
            fila[11] = producto.getIva_neto();
            fila[12] = producto.getImpuestoInterno_porcentaje();
            fila[13] = producto.getImpuestoInterno_neto();
            fila[14] = producto.getPrecioLista();
            fila[15] = producto.getRubro().getNombre();
            fila[16] = producto.getFechaUltimaModificacion();
            fila[17] = producto.getEstanteria();
            fila[18] = producto.getEstante();
            fila[19] = producto.getProveedor().getRazonSocial();
            fila[20] = producto.getFechaAlta();
            fila[21] = producto.getFechaVencimiento();
            fila[22] = producto.getNota();
            modeloTablaResultados.addRow(fila);
        }
        tbl_Resultados.setModel(modeloTablaResultados);
        lbl_cantResultados.setText(productos.size() + " productos encontrados");
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void cambiarEstadoEnabled(boolean status) {
        chk_Codigo.setEnabled(status);
        if (status == true && chk_Codigo.isSelected() == true) {
            txt_Codigo.setEnabled(true);
        } else {
            txt_Codigo.setEnabled(false);
        }
        chk_Descripcion.setEnabled(status);
        if (status == true && chk_Descripcion.isSelected() == true) {
            txt_Descripcion.setEnabled(true);
        } else {
            txt_Descripcion.setEnabled(false);
        }
        chk_Rubro.setEnabled(status);
        if (status == true && chk_Rubro.isSelected() == true) {
            cmb_Rubro.setEnabled(true);
        } else {
            cmb_Rubro.setEnabled(false);
        }
        chk_Proveedor.setEnabled(status);
        if (status == true && chk_Proveedor.isSelected() == true) {
            cmb_Proveedor.setEnabled(true);
        } else {
            cmb_Proveedor.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        rb_Todos.setEnabled(status);
        rb_Faltantes.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        btn_Nuevo.setEnabled(status);
        btn_Modificar.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        btn_ReporteListaPrecios.setEnabled(status);
    }

    private void buscar() {
        cambiarEstadoEnabled(false);
        pg_progreso.setIndeterminate(true);

        SwingWorker<List<Producto>, Void> worker = new SwingWorker<List<Producto>, Void>() {
            @Override
            protected List<Producto> doInBackground() throws Exception {
                try {
                    BusquedaProductoCriteria criteria = new BusquedaProductoCriteria();
                    criteria.setBuscarPorCodigo(chk_Codigo.isSelected());
                    criteria.setCodigo(txt_Codigo.getText().trim());
                    criteria.setBuscarPorDescripcion(chk_Descripcion.isSelected());
                    criteria.setDescripcion(txt_Descripcion.getText().trim());
                    criteria.setBuscarPorRubro(chk_Rubro.isSelected());
                    criteria.setRubro((Rubro) cmb_Rubro.getSelectedItem());
                    criteria.setBuscarPorProveedor(chk_Proveedor.isSelected());
                    criteria.setProveedor((Proveedor) cmb_Proveedor.getSelectedItem());
                    criteria.setEmpresa(empresaService.getEmpresaActiva().getEmpresa());
                    criteria.setCantRegistros(cantidadResultadosParaMostrar);
                    criteria.setListarSoloFaltantes(listarSoloFaltantes);
                    productos = productoService.buscarProductos(criteria);
                    return productos;

                } catch (BusinessServiceException ex) {
                    JOptionPane.showInternalMessageDialog(getParent(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                }

                productos = new ArrayList<>();
                return productos;
            }

            @Override
            protected void done() {
                cargarResultadosAlTable();
                cambiarEstadoEnabled(true);
                pg_progreso.setIndeterminate(false);
                try {
                    if (get().isEmpty()) {
                        JOptionPane.showInternalMessageDialog(getParent(),
                                ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_busqueda_sin_resultados"),
                                "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (InterruptedException ex) {
                    String msjError = "La tarea que se estaba realizando fue interrumpida. Intente nuevamente.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);

                } catch (ExecutionException ex) {
                    String msjError = "Se produjo un error en la ejecución de la tarea solicitada. Intente nuevamente.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private List<Producto> getSeleccionMultipleDeProductos(int[] indices) {
        List<Producto> productosSeleccionados = new ArrayList<>();
        for (int i = 0; i < indices.length; i++) {
            productosSeleccionados.add(productos.get(indices[i]));
        }
        return productosSeleccionados;
    }

    private void lanzarReporteListaDePrecios() throws JRException {
        if (productos != null) {
            if (!productos.isEmpty()) {
                JasperPrint report = productoService.getReporteListaDePrecios(productos);
                JDialog viewer = new JDialog(new JFrame(), "Vista Previa", true);
                viewer.setSize(this.getWidth(), this.getHeight());
                ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/SIC_16_square.png"));
                viewer.setIconImage(iconoVentana.getImage());
                viewer.setLocationRelativeTo(null);
                JRViewer jrv = new JRViewer(report);
                viewer.getContentPane().add(jrv);
                viewer.setVisible(true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg_TiposDeListados = new javax.swing.ButtonGroup();
        panelFiltros = new javax.swing.JPanel();
        chk_Codigo = new javax.swing.JCheckBox();
        txt_Codigo = new javax.swing.JTextField();
        chk_Proveedor = new javax.swing.JCheckBox();
        cmb_Proveedor = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        txt_Descripcion = new javax.swing.JTextField();
        chk_Descripcion = new javax.swing.JCheckBox();
        chk_Rubro = new javax.swing.JCheckBox();
        cmb_Rubro = new javax.swing.JComboBox();
        pg_progreso = new javax.swing.JProgressBar();
        lbl_cantResultados = new javax.swing.JLabel();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        rb_Faltantes = new javax.swing.JRadioButton();
        rb_Todos = new javax.swing.JRadioButton();
        btn_ReporteListaPrecios = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Productos");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Product_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Codigo.setText("Código:");
        chk_Codigo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_CodigoItemStateChanged(evt);
            }
        });

        txt_Codigo.setEnabled(false);

        chk_Proveedor.setText("Proveedor:");
        chk_Proveedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProveedorItemStateChanged(evt);
            }
        });

        cmb_Proveedor.setEnabled(false);

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        txt_Descripcion.setEnabled(false);

        chk_Descripcion.setText("Descripción:");
        chk_Descripcion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_DescripcionItemStateChanged(evt);
            }
        });

        chk_Rubro.setText("Rubro:");
        chk_Rubro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_RubroItemStateChanged(evt);
            }
        });

        cmb_Rubro.setEnabled(false);

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_Rubro)
                            .addComponent(chk_Descripcion)
                            .addComponent(chk_Codigo)
                            .addComponent(chk_Proveedor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_Descripcion, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_Rubro, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_Codigo)
                            .addComponent(cmb_Proveedor, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pg_progreso, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Codigo)
                    .addComponent(txt_Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Descripcion)
                    .addComponent(txt_Descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Rubro)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Proveedor)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pg_progreso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_Resultados.setToolTipText("Mantener presionado Ctrl ó Shift para seleccionar varios productos");
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProduct_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditProduct_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteProduct_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        bg_TiposDeListados.add(rb_Faltantes);
        rb_Faltantes.setText("Faltantes");
        rb_Faltantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_FaltantesActionPerformed(evt);
            }
        });

        bg_TiposDeListados.add(rb_Todos);
        rb_Todos.setText("Todos");
        rb_Todos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_TodosActionPerformed(evt);
            }
        });

        btn_ReporteListaPrecios.setForeground(new java.awt.Color(0, 0, 255));
        btn_ReporteListaPrecios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Printer_16x16.png"))); // NOI18N
        btn_ReporteListaPrecios.setText("Imprimir");
        btn_ReporteListaPrecios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ReporteListaPreciosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rb_Todos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rb_Faltantes))
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addComponent(btn_Nuevo)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Modificar)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Eliminar)
                        .addGap(0, 0, 0)
                        .addComponent(btn_ReporteListaPrecios)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rb_Todos)
                    .addComponent(rb_Faltantes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Eliminar, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Modificar, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Nuevo, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_ReporteListaPrecios, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo, btn_ReporteListaPrecios});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chk_CodigoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_CodigoItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Codigo.isSelected() == true) {
            txt_Codigo.setEnabled(true);
            txt_Codigo.requestFocus();
        } else {
            txt_Codigo.setEnabled(false);
        }
    }//GEN-LAST:event_chk_CodigoItemStateChanged

    private void chk_ProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProveedorItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Proveedor.isSelected() == true) {
            cmb_Proveedor.setEnabled(true);
            cmb_Proveedor.requestFocus();
        } else {
            cmb_Proveedor.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ProveedorItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        buscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        GUI_DetalleProducto gui_DetalleProducto = new GUI_DetalleProducto();
        gui_DetalleProducto.setModal(true);
        gui_DetalleProducto.setLocationRelativeTo(this);
        gui_DetalleProducto.setVisible(true);

        try {
            this.cargarComboBoxProveedores();
            this.cargarComboBoxRubros();
            this.buscar();

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {

            int respuesta = JOptionPane.showInternalConfirmDialog(this,
                    ResourceBundle.getBundle(
                            "Mensajes").getString("mensaje_pregunta_eliminar_productos"),
                    "Eliminar", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    productoService.eliminarMultiplesProductos(
                            this.getSeleccionMultipleDeProductos(
                                    Utilidades.getSelectedRowsModelIndices(tbl_Resultados)));
                    buscar();

                } catch (BusinessServiceException ex) {
                    JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_ModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            if (tbl_Resultados.getSelectedRowCount() > 1) {
                //seleccion multiple
                GUI_ModificacionProductosBulk modificarProductosBulk = new GUI_ModificacionProductosBulk(
                        this.getSeleccionMultipleDeProductos(
                                Utilidades.getSelectedRowsModelIndices(tbl_Resultados)));
                modificarProductosBulk.setModal(true);
                modificarProductosBulk.setLocationRelativeTo(this);
                modificarProductosBulk.setVisible(true);
            } else {
                //seleccion unica
                GUI_DetalleProducto modificarProducto = new GUI_DetalleProducto(
                        productos.get(Utilidades.getSelectedRowModelIndice(tbl_Resultados)));
                modificarProducto.setModal(true);
                modificarProducto.setLocationRelativeTo(this);
                modificarProducto.setVisible(true);
            }

            try {
                this.cargarComboBoxProveedores();
                this.cargarComboBoxRubros();
                this.buscar();

            } catch (BusinessServiceException ex) {
                JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void chk_DescripcionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_DescripcionItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Descripcion.isSelected() == true) {
            txt_Descripcion.setEnabled(true);
            txt_Descripcion.requestFocus();
        } else {
            txt_Descripcion.setEnabled(false);
        }
    }//GEN-LAST:event_chk_DescripcionItemStateChanged

    private void chk_RubroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_RubroItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Rubro.isSelected() == true) {
            cmb_Rubro.setEnabled(true);
            cmb_Rubro.requestFocus();
        } else {
            cmb_Rubro.setEnabled(false);
        }
    }//GEN-LAST:event_chk_RubroItemStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.cargarComboBoxRubros();
            this.cargarComboBoxProveedores();
            this.setColumnas();
            this.setMaximum(true);

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();

        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void rb_TodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_TodosActionPerformed
        listarSoloFaltantes = false;
        buscar();
    }//GEN-LAST:event_rb_TodosActionPerformed

    private void rb_FaltantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_FaltantesActionPerformed
        listarSoloFaltantes = true;
        buscar();
    }//GEN-LAST:event_rb_FaltantesActionPerformed

    private void btn_ReporteListaPreciosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ReporteListaPreciosActionPerformed
        try {
            this.lanzarReporteListaDePrecios();

        } catch (JRException | BusinessServiceException ex) {
            String msjError = "Se produjo un error procesando el reporte.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);

        } 
    }//GEN-LAST:event_btn_ReporteListaPreciosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_TiposDeListados;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JButton btn_ReporteListaPrecios;
    private javax.swing.JCheckBox chk_Codigo;
    private javax.swing.JCheckBox chk_Descripcion;
    private javax.swing.JCheckBox chk_Proveedor;
    private javax.swing.JCheckBox chk_Rubro;
    private javax.swing.JComboBox cmb_Proveedor;
    private javax.swing.JComboBox cmb_Rubro;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JProgressBar pg_progreso;
    private javax.swing.JRadioButton rb_Faltantes;
    private javax.swing.JRadioButton rb_Todos;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txt_Codigo;
    private javax.swing.JTextField txt_Descripcion;
    // End of variables declaration//GEN-END:variables
}
