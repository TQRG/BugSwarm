package sic.vista.swing;

import sic.modelo.PreciosProducto;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.text.ParseException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.EmpresaActiva;
import sic.modelo.Medida;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rubro;
import sic.service.IMedidaService;
import sic.service.IProductoService;
import sic.service.IProveedorService;
import sic.service.IRubroService;
import sic.service.BusinessServiceException;

public class GUI_ModificacionProductosBulk extends JDialog {

    private final List<Producto> productosParaModificar;
    private ModeloTabla modeloTablaProductos;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IMedidaService medidaService = appContext.getBean(IMedidaService.class);    
    private final IRubroService rubroService = appContext.getBean(IRubroService.class);
    private final IProveedorService proveedorService = appContext.getBean(IProveedorService.class);
    private final IProductoService productoService = appContext.getBean(IProductoService.class);
    private static final Logger LOGGER = Logger.getLogger(GUI_ModificacionProductosBulk.class.getPackage().getName());

    public GUI_ModificacionProductosBulk(List<Producto> productosParaModificar) {
        this.initComponents();
        this.setIcon();        
        this.productosParaModificar = productosParaModificar;
        this.cargarResultadosAlTable();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void validarComponentesDePrecios() {
        try {
            txt_PrecioCosto.commitEdit();
            txt_PVP.commitEdit();
            txt_IVA_Neto.commitEdit();
            txt_ImpuestoInterno.commitEdit();
            txt_ImpuestoInterno_Neto.commitEdit();
            txt_Ganancia.commitEdit();
            txt_Ganancia_Neto.commitEdit();
            txt_PrecioLista.commitEdit();

        } catch (ParseException ex) {
            String msjError = "Se produjo un error analizando los campos.";
            LOGGER.error(msjError + " - " + ex.getMessage());
        }
    }

    private void cargarComboBoxMedidas() {
        cmb_Medida.removeAllItems();
        List<Medida> medidas = medidaService.getUnidadMedidas(EmpresaActiva.getInstance().getEmpresa());
        for (Medida medida : medidas) {
            cmb_Medida.addItem(medida);
        }
    }

    private void cargarComboBoxRubros() {
        cmb_Rubro.removeAllItems();
        List<Rubro> rubros = rubroService.getRubros(EmpresaActiva.getInstance().getEmpresa());
        for (Rubro rubro : rubros) {
            cmb_Rubro.addItem(rubro);
        }
    }

    private void cargarComboBoxProveedores() {
        cmb_Proveedor.removeAllItems();
        List<Proveedor> proveedores;
        proveedores = proveedorService.getProveedores(EmpresaActiva.getInstance().getEmpresa());
        for (Proveedor proveedor : proveedores) {
            cmb_Proveedor.addItem(proveedor);
        }
    }

    private void cargarComboBoxIVA() {
        cmb_IVA.removeAllItems();
        cmb_IVA.addItem((double) 0);
        cmb_IVA.addItem(10.5);
        cmb_IVA.addItem((double) 21);
    }

    private void prepararComponentes() {
        txt_PrecioCosto.setValue(0.0);
        txt_PVP.setValue(0.0);
        txt_IVA_Neto.setValue(0.0);
        txt_ImpuestoInterno.setValue(0.0);
        txt_ImpuestoInterno_Neto.setValue(0.0);
        txt_Ganancia.setValue(0.0);
        txt_Ganancia_Neto.setValue(0.0);
        txt_PrecioLista.setValue(0.0);
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[2];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripcion";

        modeloTablaProductos.setColumnIdentifiers(encabezados);
        tbl_ProductosAModifcar.setModel(modeloTablaProductos);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaProductos.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;

        modeloTablaProductos.setClaseColumnas(tipos);
        tbl_ProductosAModifcar.getTableHeader().setReorderingAllowed(false);
        tbl_ProductosAModifcar.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_ProductosAModifcar.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbl_ProductosAModifcar.getColumnModel().getColumn(1).setPreferredWidth(400);
    }

    private void limpiarJTable() {
        modeloTablaProductos = new ModeloTabla();
        tbl_ProductosAModifcar.setModel(modeloTablaProductos);
        this.setColumnas();
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        for (Producto producto : productosParaModificar) {
            Object[] fila = new Object[23];
            fila[0] = producto.getCodigo();
            fila[1] = producto.getDescripcion();

            modeloTablaProductos.addRow(fila);
        }
        tbl_ProductosAModifcar.setModel(modeloTablaProductos);
    }

    private void habilitarBotonGuardar() {
        if (chk_Precios.isSelected() == true
                || chk_Proveedor.isSelected() == true
                || chk_Rubro.isSelected() == true
                || chk_UnidadDeMedida.isSelected() == true) {

            btn_Guardar.setEnabled(true);
        } else {
            btn_Guardar.setEnabled(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp_ProductosAModificar = new javax.swing.JScrollPane();
        tbl_ProductosAModifcar = new javax.swing.JTable();
        panel1 = new javax.swing.JPanel();
        cmb_Rubro = new javax.swing.JComboBox();
        btn_Rubros = new javax.swing.JButton();
        cmb_Proveedor = new javax.swing.JComboBox();
        btn_NuevoProveedor = new javax.swing.JButton();
        btn_Medidas = new javax.swing.JButton();
        cmb_Medida = new javax.swing.JComboBox();
        chk_Rubro = new javax.swing.JCheckBox();
        chk_Proveedor = new javax.swing.JCheckBox();
        chk_UnidadDeMedida = new javax.swing.JCheckBox();
        panel2 = new javax.swing.JPanel();
        lbl_PrecioCosto = new javax.swing.JLabel();
        lbl_Ganancia = new javax.swing.JLabel();
        lbl_PrecioLista = new javax.swing.JLabel();
        lbl_ImpuestoInterno = new javax.swing.JLabel();
        txt_PrecioCosto = new javax.swing.JFormattedTextField();
        txt_Ganancia = new javax.swing.JFormattedTextField();
        txt_PrecioLista = new javax.swing.JFormattedTextField();
        txt_ImpuestoInterno = new javax.swing.JFormattedTextField();
        lbl_IVA = new javax.swing.JLabel();
        txt_IVA_Neto = new javax.swing.JFormattedTextField();
        txt_Ganancia_Neto = new javax.swing.JFormattedTextField();
        txt_ImpuestoInterno_Neto = new javax.swing.JFormattedTextField();
        lbl_PVP = new javax.swing.JLabel();
        txt_PVP = new javax.swing.JFormattedTextField();
        chk_Precios = new javax.swing.JCheckBox();
        cmb_IVA = new javax.swing.JComboBox();
        btn_Guardar = new javax.swing.JButton();
        lbl_Indicaciones = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modificar varios Productos");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tbl_ProductosAModifcar.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_ProductosAModifcar.setFocusable(false);
        sp_ProductosAModificar.setViewportView(tbl_ProductosAModifcar);

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        cmb_Rubro.setEnabled(false);

        btn_Rubros.setForeground(java.awt.Color.blue);
        btn_Rubros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddBlock.png"))); // NOI18N
        btn_Rubros.setText("Nuevo");
        btn_Rubros.setEnabled(false);
        btn_Rubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RubrosActionPerformed(evt);
            }
        });

        cmb_Proveedor.setEnabled(false);

        btn_NuevoProveedor.setForeground(java.awt.Color.blue);
        btn_NuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_NuevoProveedor.setText("Nuevo");
        btn_NuevoProveedor.setEnabled(false);
        btn_NuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoProveedorActionPerformed(evt);
            }
        });

        btn_Medidas.setForeground(java.awt.Color.blue);
        btn_Medidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddRuler_16x16.png"))); // NOI18N
        btn_Medidas.setText("Nueva");
        btn_Medidas.setEnabled(false);
        btn_Medidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_MedidasActionPerformed(evt);
            }
        });

        cmb_Medida.setEnabled(false);

        chk_Rubro.setText("Rubro:");
        chk_Rubro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_RubroItemStateChanged(evt);
            }
        });

        chk_Proveedor.setText("Proveedor:");
        chk_Proveedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProveedorItemStateChanged(evt);
            }
        });

        chk_UnidadDeMedida.setText("U. de Medida:");
        chk_UnidadDeMedida.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UnidadDeMedidaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chk_UnidadDeMedida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Proveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Rubro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_Medida, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_NuevoProveedor)
                    .addComponent(btn_Medidas)
                    .addComponent(btn_Rubros))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Rubro)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Rubros, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Proveedor)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_UnidadDeMedida)
                    .addComponent(cmb_Medida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Medidas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Rubros, cmb_Rubro});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoProveedor, cmb_Proveedor});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Medidas, cmb_Medida});

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_PrecioCosto.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PrecioCosto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioCosto.setText("Precio de Costo:");

        lbl_Ganancia.setForeground(new java.awt.Color(192, 192, 192));
        lbl_Ganancia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Ganancia.setText("Ganancia (%):");

        lbl_PrecioLista.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PrecioLista.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioLista.setText("Precio de Lista:");

        lbl_ImpuestoInterno.setForeground(new java.awt.Color(192, 192, 192));
        lbl_ImpuestoInterno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_ImpuestoInterno.setText("Impuesto Interno (%):");

        txt_PrecioCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        txt_PrecioCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_PrecioCosto.setEnabled(false);
        txt_PrecioCosto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_PrecioCostoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_PrecioCostoFocusLost(evt);
            }
        });
        txt_PrecioCosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_PrecioCostoActionPerformed(evt);
            }
        });

        txt_Ganancia.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        txt_Ganancia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Ganancia.setEnabled(false);
        txt_Ganancia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_GananciaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_GananciaFocusLost(evt);
            }
        });
        txt_Ganancia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_GananciaActionPerformed(evt);
            }
        });

        txt_PrecioLista.setEditable(false);
        txt_PrecioLista.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        txt_PrecioLista.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_PrecioLista.setEnabled(false);
        txt_PrecioLista.setFocusable(false);

        txt_ImpuestoInterno.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        txt_ImpuestoInterno.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_ImpuestoInterno.setEnabled(false);
        txt_ImpuestoInterno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_ImpuestoInternoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_ImpuestoInternoFocusLost(evt);
            }
        });
        txt_ImpuestoInterno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_ImpuestoInternoActionPerformed(evt);
            }
        });

        lbl_IVA.setForeground(new java.awt.Color(192, 192, 192));
        lbl_IVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_IVA.setText("I.V.A. (%):");

        txt_IVA_Neto.setEditable(false);
        txt_IVA_Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        txt_IVA_Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_IVA_Neto.setEnabled(false);
        txt_IVA_Neto.setFocusable(false);

        txt_Ganancia_Neto.setEditable(false);
        txt_Ganancia_Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        txt_Ganancia_Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Ganancia_Neto.setEnabled(false);
        txt_Ganancia_Neto.setFocusable(false);

        txt_ImpuestoInterno_Neto.setEditable(false);
        txt_ImpuestoInterno_Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        txt_ImpuestoInterno_Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_ImpuestoInterno_Neto.setEnabled(false);
        txt_ImpuestoInterno_Neto.setFocusable(false);

        lbl_PVP.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PVP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PVP.setText("Precio Venta Público:");

        txt_PVP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        txt_PVP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_PVP.setEnabled(false);
        txt_PVP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_PVPFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_PVPFocusLost(evt);
            }
        });
        txt_PVP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_PVPActionPerformed(evt);
            }
        });

        chk_Precios.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_PreciosItemStateChanged(evt);
            }
        });

        cmb_IVA.setEnabled(false);
        cmb_IVA.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_IVAItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(chk_Precios)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lbl_PrecioCosto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lbl_Ganancia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(12, 12, 12)))
                        .addComponent(txt_Ganancia, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbl_PVP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_IVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_ImpuestoInterno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_PrecioLista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_IVA, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_ImpuestoInterno, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txt_ImpuestoInterno_Neto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                    .addComponent(txt_IVA_Neto, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_PVP, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Ganancia_Neto, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_PrecioCosto, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_PrecioLista))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chk_Precios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PrecioCosto)
                    .addComponent(txt_PrecioCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Ganancia)
                    .addComponent(txt_Ganancia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Ganancia_Neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PVP)
                    .addComponent(txt_PVP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_IVA)
                    .addComponent(cmb_IVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_IVA_Neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_ImpuestoInterno)
                    .addComponent(txt_ImpuestoInterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ImpuestoInterno_Neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PrecioLista)
                    .addComponent(txt_PrecioLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.setEnabled(false);
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        lbl_Indicaciones.setText("Productos que se van a modificar:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_ProductosAModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Indicaciones)
                            .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_Indicaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_ProductosAModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_RubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RubrosActionPerformed
        GUI_DetalleRubro gui_DetalleRubro = new GUI_DetalleRubro();
        gui_DetalleRubro.setModal(true);
        gui_DetalleRubro.setLocationRelativeTo(this);
        gui_DetalleRubro.setVisible(true);

        try {
            this.cargarComboBoxRubros();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_RubrosActionPerformed

    private void btn_NuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoProveedorActionPerformed
        GUI_DetalleProveedor gui_DetalleProveedor = new GUI_DetalleProveedor();
        gui_DetalleProveedor.setModal(true);
        gui_DetalleProveedor.setLocationRelativeTo(this);
        gui_DetalleProveedor.setVisible(true);

        try {
            this.cargarComboBoxProveedores();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevoProveedorActionPerformed

    private void btn_MedidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_MedidasActionPerformed
        GUI_DetalleMedida gui_DetalleMedida = new GUI_DetalleMedida();
        gui_DetalleMedida.setModal(true);
        gui_DetalleMedida.setLocationRelativeTo(this);
        gui_DetalleMedida.setVisible(true);

        try {
            this.cargarComboBoxMedidas();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_MedidasActionPerformed

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        boolean checkPrecios = false;
        boolean checkMedida = false;
        boolean checkRubro = false;
        boolean checkProveedor = false;
        PreciosProducto preciosProducto = new PreciosProducto();
        Medida medida = new Medida();
        Rubro rubro = new Rubro();
        Proveedor proveedor = new Proveedor();

        if (chk_Precios.isSelected() == true) {
            checkPrecios = true;
            preciosProducto.setPrecioCosto(Double.parseDouble(txt_PrecioCosto.getValue().toString()));
            preciosProducto.setGanancia_porcentaje(Double.parseDouble(txt_Ganancia.getValue().toString()));
            preciosProducto.setGanancia_neto(Double.parseDouble(txt_Ganancia_Neto.getValue().toString()));
            preciosProducto.setPrecioVentaPublico(Double.parseDouble(txt_PVP.getValue().toString()));
            preciosProducto.setIva_porcentaje(Double.parseDouble(cmb_IVA.getSelectedItem().toString()));
            preciosProducto.setIva_neto(Double.parseDouble(txt_IVA_Neto.getValue().toString()));
            preciosProducto.setImpuestoInterno_porcentaje(Double.parseDouble(txt_ImpuestoInterno.getValue().toString()));
            preciosProducto.setImpuestoInterno_neto(Double.parseDouble(txt_ImpuestoInterno_Neto.getValue().toString()));
            preciosProducto.setPrecioLista(Double.parseDouble(txt_PrecioLista.getValue().toString()));
        }

        if (chk_UnidadDeMedida.isSelected() == true) {
            checkMedida = true;
            medida = (Medida) cmb_Medida.getSelectedItem();
        }

        if (chk_Rubro.isSelected() == true) {
            checkRubro = true;
            rubro = (Rubro) cmb_Rubro.getSelectedItem();
        }

        if (chk_Proveedor.isSelected() == true) {
            checkProveedor = true;
            proveedor = (Proveedor) cmb_Proveedor.getSelectedItem();
        }

        try {
            productoService.modificarMultiplesProductos(productosParaModificar,
                    checkPrecios, preciosProducto, checkMedida, medida,
                    checkRubro, rubro, checkProveedor, proveedor);

            JOptionPane.showMessageDialog(this, "Los productos se modificaron correctamente.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void chk_RubroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_RubroItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cmb_Rubro.setEnabled(true);
            btn_Rubros.setEnabled(true);
        } else {
            cmb_Rubro.setEnabled(false);
            btn_Rubros.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_RubroItemStateChanged

    private void chk_ProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProveedorItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cmb_Proveedor.setEnabled(true);
            btn_NuevoProveedor.setEnabled(true);
        } else {
            cmb_Proveedor.setEnabled(false);
            btn_NuevoProveedor.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_ProveedorItemStateChanged

    private void chk_UnidadDeMedidaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UnidadDeMedidaItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cmb_Medida.setEnabled(true);
            btn_Medidas.setEnabled(true);
        } else {
            cmb_Medida.setEnabled(false);
            btn_Medidas.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_UnidadDeMedidaItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            this.prepararComponentes();
            this.cargarComboBoxRubros();
            this.cargarComboBoxProveedores();
            this.cargarComboBoxMedidas();
            this.cargarComboBoxIVA();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formWindowOpened

    private void cmb_IVAItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_IVAItemStateChanged
        this.validarComponentesDePrecios();
        txt_IVA_Neto.setValue(productoService.calcularIVA_Neto(
                Double.parseDouble(txt_PVP.getValue().toString()),
                Double.parseDouble(cmb_IVA.getSelectedItem().toString())));
        txt_PrecioLista.setValue(productoService.calcularPrecioLista(
                Double.parseDouble(txt_PVP.getValue().toString()),
                Double.parseDouble(cmb_IVA.getSelectedItem().toString()),
                Double.parseDouble(txt_ImpuestoInterno.getValue().toString())));
    }//GEN-LAST:event_cmb_IVAItemStateChanged

    private void chk_PreciosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_PreciosItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            lbl_PrecioCosto.setForeground(Color.BLACK);
            txt_PrecioCosto.setEnabled(true);
            lbl_Ganancia.setForeground(Color.BLACK);
            txt_Ganancia.setEnabled(true);
            txt_Ganancia_Neto.setEnabled(true);
            lbl_PVP.setForeground(Color.BLACK);
            txt_PVP.setEnabled(true);
            lbl_IVA.setForeground(Color.BLACK);
            cmb_IVA.setEnabled(true);
            txt_IVA_Neto.setEnabled(true);
            lbl_ImpuestoInterno.setForeground(Color.BLACK);
            txt_ImpuestoInterno.setEnabled(true);
            txt_ImpuestoInterno_Neto.setEnabled(true);
            lbl_PrecioLista.setForeground(Color.BLACK);
            txt_PrecioLista.setEnabled(true);
        } else {
            lbl_PrecioCosto.setForeground(Color.LIGHT_GRAY);
            txt_PrecioCosto.setEnabled(false);
            lbl_Ganancia.setForeground(Color.LIGHT_GRAY);
            txt_Ganancia.setEnabled(false);
            txt_Ganancia_Neto.setEnabled(false);
            lbl_PVP.setForeground(Color.LIGHT_GRAY);
            txt_PVP.setEnabled(false);
            lbl_IVA.setForeground(Color.LIGHT_GRAY);
            cmb_IVA.setEnabled(false);
            txt_IVA_Neto.setEnabled(false);
            lbl_ImpuestoInterno.setForeground(Color.LIGHT_GRAY);
            txt_ImpuestoInterno.setEnabled(false);
            txt_ImpuestoInterno_Neto.setEnabled(false);
            lbl_PrecioLista.setForeground(Color.LIGHT_GRAY);
            txt_PrecioLista.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_PreciosItemStateChanged

    private void txt_PVPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PVPFocusLost
        this.txt_PVPActionPerformed(null);
    }//GEN-LAST:event_txt_PVPFocusLost

    private void txt_PVPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PVPFocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_PVP.selectAll();
            }
        });
    }//GEN-LAST:event_txt_PVPFocusGained

    private void txt_PVPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_PVPActionPerformed
        this.validarComponentesDePrecios();
        txt_Ganancia.setValue(productoService.calcularGanancia_Porcentaje(
                Double.parseDouble(txt_PrecioCosto.getValue().toString()),
                Double.parseDouble(txt_PVP.getValue().toString())));
        txt_GananciaActionPerformed(null);
        cmb_IVAItemStateChanged(null);
        txt_ImpuestoInternoActionPerformed(null);
    }//GEN-LAST:event_txt_PVPActionPerformed

    private void txt_ImpuestoInternoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ImpuestoInternoFocusLost
        this.txt_ImpuestoInternoActionPerformed(null);
    }//GEN-LAST:event_txt_ImpuestoInternoFocusLost

    private void txt_ImpuestoInternoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ImpuestoInternoFocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_ImpuestoInterno.selectAll();
            }
        });
    }//GEN-LAST:event_txt_ImpuestoInternoFocusGained

    private void txt_ImpuestoInternoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_ImpuestoInternoActionPerformed
        this.validarComponentesDePrecios();
        txt_ImpuestoInterno_Neto.setValue(productoService.calcularImpInterno_Neto(
                Double.parseDouble(txt_PVP.getValue().toString()),
                Double.parseDouble(txt_ImpuestoInterno.getValue().toString())));
        txt_PrecioLista.setValue(productoService.calcularPrecioLista(
                Double.parseDouble(txt_PVP.getValue().toString()),
                Double.parseDouble(cmb_IVA.getSelectedItem().toString()),
                Double.parseDouble(txt_ImpuestoInterno.getValue().toString())));
    }//GEN-LAST:event_txt_ImpuestoInternoActionPerformed

    private void txt_GananciaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_GananciaFocusLost
        this.txt_GananciaActionPerformed(null);
    }//GEN-LAST:event_txt_GananciaFocusLost

    private void txt_GananciaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_GananciaFocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_Ganancia.selectAll();
            }
        });
    }//GEN-LAST:event_txt_GananciaFocusGained

    private void txt_GananciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_GananciaActionPerformed
        this.validarComponentesDePrecios();
        txt_Ganancia_Neto.setValue(productoService.calcularGanancia_Neto(
                Double.parseDouble(txt_PrecioCosto.getValue().toString()),
                Double.parseDouble(txt_Ganancia.getValue().toString())));
        txt_PVP.setValue(productoService.calcularPVP(
                Double.parseDouble(txt_PrecioCosto.getValue().toString()),
                Double.parseDouble(txt_Ganancia.getValue().toString())));
        txt_PrecioLista.setValue(productoService.calcularPrecioLista(
                Double.parseDouble(txt_PVP.getValue().toString()),
                Double.parseDouble(cmb_IVA.getSelectedItem().toString()),
                Double.parseDouble(txt_ImpuestoInterno.getValue().toString())));
        cmb_IVAItemStateChanged(null);
    }//GEN-LAST:event_txt_GananciaActionPerformed

    private void txt_PrecioCostoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PrecioCostoFocusLost
        this.txt_PrecioCostoActionPerformed(null);
    }//GEN-LAST:event_txt_PrecioCostoFocusLost

    private void txt_PrecioCostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PrecioCostoFocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_PrecioCosto.selectAll();
            }
        });
    }//GEN-LAST:event_txt_PrecioCostoFocusGained

    private void txt_PrecioCostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_PrecioCostoActionPerformed
        this.txt_GananciaActionPerformed(null);
        this.cmb_IVAItemStateChanged(null);
        this.txt_ImpuestoInternoActionPerformed(null);
    }//GEN-LAST:event_txt_PrecioCostoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_Medidas;
    private javax.swing.JButton btn_NuevoProveedor;
    private javax.swing.JButton btn_Rubros;
    private javax.swing.JCheckBox chk_Precios;
    private javax.swing.JCheckBox chk_Proveedor;
    private javax.swing.JCheckBox chk_Rubro;
    private javax.swing.JCheckBox chk_UnidadDeMedida;
    private javax.swing.JComboBox cmb_IVA;
    private javax.swing.JComboBox cmb_Medida;
    private javax.swing.JComboBox cmb_Proveedor;
    private javax.swing.JComboBox cmb_Rubro;
    private javax.swing.JLabel lbl_Ganancia;
    private javax.swing.JLabel lbl_IVA;
    private javax.swing.JLabel lbl_ImpuestoInterno;
    private javax.swing.JLabel lbl_Indicaciones;
    private javax.swing.JLabel lbl_PVP;
    private javax.swing.JLabel lbl_PrecioCosto;
    private javax.swing.JLabel lbl_PrecioLista;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JScrollPane sp_ProductosAModificar;
    private javax.swing.JTable tbl_ProductosAModifcar;
    private javax.swing.JFormattedTextField txt_Ganancia;
    private javax.swing.JFormattedTextField txt_Ganancia_Neto;
    private javax.swing.JFormattedTextField txt_IVA_Neto;
    private javax.swing.JFormattedTextField txt_ImpuestoInterno;
    private javax.swing.JFormattedTextField txt_ImpuestoInterno_Neto;
    private javax.swing.JFormattedTextField txt_PVP;
    private javax.swing.JFormattedTextField txt_PrecioCosto;
    private javax.swing.JFormattedTextField txt_PrecioLista;
    // End of variables declaration//GEN-END:variables
}
