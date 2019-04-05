package sic.vista.swing;

import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.Proveedor;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.IProveedorService;
import sic.service.Movimiento;
import sic.service.BusinessServiceException;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class GUI_FacturasCompra extends JInternalFrame {

    private ModeloTabla modeloTablaFacturas = new ModeloTabla();
    private List<FacturaCompra> facturas;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IFacturaService facturaService = appContext.getBean(IFacturaService.class);
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);
    private final IProveedorService proveedorService = appContext.getBean(IProveedorService.class);
    private static final Logger LOGGER = Logger.getLogger(GUI_FacturasCompra.class.getPackage().getName());

    public GUI_FacturasCompra() {
        this.initComponents();
        this.setSize(940, 600);
    }

    private void setColumnas() {
        //sorting
        tbl_Resultados.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[15];
        encabezados[0] = "Fecha Factura";
        encabezados[1] = "Tipo";
        encabezados[2] = "Nº Factura";
        encabezados[3] = "Fecha Vencimiento";
        encabezados[4] = "Proveedor";
        encabezados[5] = "Transportista";
        encabezados[6] = "Pagada";
        encabezados[7] = "SubTotal";
        encabezados[8] = "% Descuento";
        encabezados[9] = "Descuento neto";
        encabezados[10] = "SubTotal neto";
        encabezados[11] = "IVA 10.5% neto";
        encabezados[12] = "IVA 21% neto";
        encabezados[13] = "Imp. Interno neto";
        encabezados[14] = "Total";
        modeloTablaFacturas.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaFacturas);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaFacturas.getColumnCount()];
        tipos[0] = Date.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = Date.class;
        tipos[4] = String.class;
        tipos[5] = String.class;
        tipos[6] = Boolean.class;
        tipos[7] = Double.class;
        tipos[8] = Double.class;
        tipos[9] = Double.class;
        tipos[10] = Double.class;
        tipos[11] = Double.class;
        tipos[12] = Double.class;
        tipos[13] = Double.class;
        tipos[14] = Double.class;
        modeloTablaFacturas.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(60);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(80);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(120);
    }

    private void buscar() {
        try {
            BusquedaFacturaCompraCriteria criteria = new BusquedaFacturaCompraCriteria();
            criteria.setBuscaPorFecha(chk_Fecha.isSelected());
            criteria.setFechaDesde(dc_FechaDesde.getDate());
            criteria.setFechaHasta(dc_FechaHasta.getDate());
            criteria.setBuscaPorProveedor(chk_Proveedor.isSelected());
            criteria.setProveedor((Proveedor) cmb_Proveedor.getSelectedItem());
            criteria.setBuscaPorNumeroFactura(chk_NumFactura.isSelected());
            txt_SerieFactura.commitEdit();
            txt_NroFactura.commitEdit();
            criteria.setNumSerie(Integer.valueOf(txt_SerieFactura.getValue().toString()));
            criteria.setNumFactura(Integer.valueOf(txt_NroFactura.getValue().toString()));
            if (rb_soloImpagas.isEnabled() && rb_soloImpagas.isSelected()) {
                criteria.setBuscarSoloInpagas(true);
            }
            if (rb_soloPagadas.isEnabled() && rb_soloPagadas.isSelected()) {
                criteria.setBuscaSoloPagadas(true);
            }
            criteria.setEmpresa(empresaService.getEmpresaActiva().getEmpresa());
            criteria.setCantRegistros(0);

            facturas = facturaService.buscarFacturaCompra(criteria);
            this.cargarResultadosAlTable();
            calcularResultados();

            if (facturas.isEmpty()) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle(
                        "Mensajes").getString("mensaje_busqueda_sin_resultados"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (ParseException | BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        } 
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        for (FacturaCompra factura : facturas) {
            Object[] fila = new Object[15];
            fila[0] = factura.getFecha();
            fila[1] = String.valueOf(factura.getTipoFactura());
            fila[2] = factura.getNumSerie() + " - " + factura.getNumFactura();
            fila[3] = factura.getFechaVencimiento();
            fila[4] = factura.getProveedor().getRazonSocial();
            fila[5] = factura.getTransportista().getNombre();
            fila[6] = factura.isPagada();
            fila[7] = factura.getSubTotal();
            fila[8] = factura.getDescuento_porcentaje();
            fila[9] = factura.getDescuento_neto();
            fila[10] = factura.getSubTotal_neto();
            fila[11] = factura.getIva_105_neto();
            fila[12] = factura.getIva_21_neto();
            fila[13] = factura.getImpuestoInterno_neto();
            fila[14] = factura.getTotal();
            modeloTablaFacturas.addRow(fila);
        }
        tbl_Resultados.setModel(modeloTablaFacturas);
        String mensaje = facturas.size() + " facturas encontradas";
        lbl_CantRegistrosEncontrados.setText(mensaje);
    }

    private void calcularResultados() {
        txt_ResultGastoTotal.setValue(facturaService.calcularTotalFacturadoCompra(facturas));
        txt_ResultTotalIVACompra.setValue(facturaService.calcularIVA_Compra(facturas));
    }

    private void limpiarJTable() {
        modeloTablaFacturas = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaFacturas);
        this.setColumnas();
    }

    private void cargarComboBoxProveedores() {
        cmb_Proveedor.removeAllItems();
        List<Proveedor> proveedores;
        proveedores = proveedorService.getProveedores(empresaService.getEmpresaActiva().getEmpresa());
        for (Proveedor proveedor : proveedores) {
            cmb_Proveedor.addItem(proveedor);
        }
    }

    private boolean existeProveedorDisponible() {
        if (proveedorService.getProveedores(empresaService.getEmpresaActiva().getEmpresa()).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        chk_Fecha = new javax.swing.JCheckBox();
        chk_Proveedor = new javax.swing.JCheckBox();
        cmb_Proveedor = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        chk_estadoFactura = new javax.swing.JCheckBox();
        chk_NumFactura = new javax.swing.JCheckBox();
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        txt_SerieFactura = new javax.swing.JFormattedTextField();
        txt_NroFactura = new javax.swing.JFormattedTextField();
        lbl_CantRegistrosEncontrados = new javax.swing.JLabel();
        rb_soloImpagas = new javax.swing.JRadioButton();
        rb_soloPagadas = new javax.swing.JRadioButton();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_VerDetalle = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btn_VerPagos = new javax.swing.JButton();
        lbl_TotalIVACompra = new javax.swing.JLabel();
        txt_ResultTotalIVACompra = new javax.swing.JFormattedTextField();
        txt_ResultGastoTotal = new javax.swing.JFormattedTextField();
        lbl_TotalFacturado = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Facturas de Compra");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/SIC_16_square.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Fecha.setText("Fecha Factura:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

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

        chk_estadoFactura.setText("Estado Factura:");
        chk_estadoFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk_estadoFacturaActionPerformed(evt);
            }
        });

        chk_NumFactura.setText("Nº de Factura:");
        chk_NumFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumFacturaItemStateChanged(evt);
            }
        });

        lbl_Hasta.setText("Hasta:");
        lbl_Hasta.setEnabled(false);

        lbl_Desde.setText("Desde:");
        lbl_Desde.setEnabled(false);

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("-");

        txt_SerieFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_SerieFactura.setText("0");
        txt_SerieFactura.setEnabled(false);

        txt_NroFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NroFactura.setText("0");
        txt_NroFactura.setEnabled(false);

        lbl_CantRegistrosEncontrados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        rb_soloImpagas.setText("Solo Impagas");
        rb_soloImpagas.setEnabled(false);
        rb_soloImpagas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_soloImpagasActionPerformed(evt);
            }
        });

        rb_soloPagadas.setText("Solo Pagadas");
        rb_soloPagadas.setEnabled(false);
        rb_soloPagadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_soloPagadasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_CantRegistrosEncontrados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(chk_Proveedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_NumFactura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_estadoFactura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Fecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cmb_Proveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltrosLayout.createSequentialGroup()
                                    .addComponent(lbl_Desde)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lbl_Hasta)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltrosLayout.createSequentialGroup()
                                    .addComponent(txt_SerieFactura)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txt_NroFactura)))
                            .addComponent(rb_soloImpagas)
                            .addComponent(rb_soloPagadas))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dc_FechaDesde, dc_FechaHasta});

        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Fecha)
                    .addComponent(lbl_Desde)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Proveedor)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_NumFactura)
                    .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txt_NroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_estadoFactura)
                    .addComponent(rb_soloImpagas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rb_soloPagadas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Buscar)
                    .addComponent(lbl_CantRegistrosEncontrados, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Add_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nueva");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        btn_VerPagos.setForeground(java.awt.Color.blue);
        btn_VerPagos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/StampArrow_16x16.png"))); // NOI18N
        btn_VerPagos.setText("Pagos");
        btn_VerPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerPagosActionPerformed(evt);
            }
        });

        lbl_TotalIVACompra.setText("Total IVA Compra:");

        txt_ResultTotalIVACompra.setEditable(false);
        txt_ResultTotalIVACompra.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        txt_ResultTotalIVACompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_ResultTotalIVACompra.setText("$0.0");

        txt_ResultGastoTotal.setEditable(false);
        txt_ResultGastoTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        txt_ResultGastoTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_ResultGastoTotal.setText("$0.0");

        lbl_TotalFacturado.setText("Total Facturado:");

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Nuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addComponent(btn_Eliminar)
                        .addGap(0, 0, 0)
                        .addComponent(btn_VerDetalle)
                        .addGap(0, 0, 0)
                        .addComponent(btn_VerPagos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_TotalIVACompra))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                        .addGap(455, 455, 455)
                        .addComponent(lbl_TotalFacturado)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_ResultGastoTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ResultTotalIVACompra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(sp_Resultados)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Nuevo, btn_VerDetalle, btn_VerPagos});

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_TotalFacturado, lbl_TotalIVACompra});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_ResultGastoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_TotalFacturado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_ResultTotalIVACompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_TotalIVACompra)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_Eliminar)
                        .addComponent(btn_VerPagos)
                        .addComponent(btn_VerDetalle))
                    .addComponent(btn_Nuevo, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_Nuevo, btn_VerDetalle, btn_VerPagos});

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
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        try {
            GUI_DetalleFacturaCompra gui_DetalleFacturaCompra = new GUI_DetalleFacturaCompra();
            if (this.existeProveedorDisponible()) {
                gui_DetalleFacturaCompra.setModal(true);
                gui_DetalleFacturaCompra.setLocationRelativeTo(this);
                gui_DetalleFacturaCompra.setVisible(true);
                this.cargarComboBoxProveedores();
                this.buscar();
            } else {
                String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_proveedor");
                JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            GUI_DetalleFacturaCompra gui_DetalleFacturaCompra = new GUI_DetalleFacturaCompra(facturas.get(indexFilaSeleccionada));
            gui_DetalleFacturaCompra.setModal(true);
            gui_DetalleFacturaCompra.setLocationRelativeTo(this);
            gui_DetalleFacturaCompra.setVisible(true);
        }
}//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la factura seleccionada?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    facturaService.eliminar(facturas.get(indexFilaSeleccionada));
                    this.buscar();

                } catch (BusinessServiceException ex) {
                    JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
}//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_VerPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerPagosActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            if (tbl_Resultados.getSelectedRowCount() == 1) {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                GUI_Pagos gui_Pagos = new GUI_Pagos(facturas.get(indexFilaSeleccionada));
                gui_Pagos.setModal(true);
                gui_Pagos.setLocationRelativeTo(this);
                gui_Pagos.setVisible(true);
                this.buscar();
            }
            if (tbl_Resultados.getSelectedRowCount() > 1) {
                int[] indiceFacturas = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
                List<Factura> facturasCompra = new ArrayList<>();
                for (int i = 0; i < indiceFacturas.length; i++) {
                    facturasCompra.add(this.facturas.get(indiceFacturas[i]));
                }
                if (facturaService.validarFacturasParaPagoMultiple(facturasCompra, Movimiento.COMPRA)) {
                    GUI_PagoMultiplesFacturas gui_pagoMultiplesFacturas = new GUI_PagoMultiplesFacturas(this, true, facturasCompra, Movimiento.COMPRA);
                    gui_pagoMultiplesFacturas.setLocationRelativeTo(this);
                    gui_pagoMultiplesFacturas.setVisible(true);
                    this.buscar();
                } else if (!facturaService.validarClienteProveedorParaPagosMultiples(facturasCompra, Movimiento.COMPRA)) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle(
                            "Mensajes").getString("mensaje_facturas_distintos_proveedores"), "Aviso", JOptionPane.ERROR_MESSAGE);
                } else if (!facturaService.validarFacturasImpagasParaPagoMultiple(facturasCompra)) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle(
                            "Mensajes").getString("mensaje_facturas_seEncuentran_pagadas"), "Aviso", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
}//GEN-LAST:event_btn_VerPagosActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.cargarComboBoxProveedores();
            this.setColumnas();
            this.setMaximum(true);
            rb_soloImpagas.setSelected(true);

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

    private void chk_NumFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumFacturaItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_NumFactura.isSelected() == true) {
            txt_NroFactura.setEnabled(true);
            txt_SerieFactura.setEnabled(true);
            txt_SerieFactura.requestFocus();
        } else {
            txt_NroFactura.setEnabled(false);
            txt_SerieFactura.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumFacturaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.buscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void chk_ProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProveedorItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Proveedor.isSelected() == true) {
            cmb_Proveedor.setEnabled(true);
            cmb_Proveedor.requestFocus();
        } else {
            cmb_Proveedor.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ProveedorItemStateChanged

    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
            lbl_Desde.setEnabled(true);
            lbl_Hasta.setEnabled(true);
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
            lbl_Desde.setEnabled(false);
            lbl_Hasta.setEnabled(false);
        }
    }//GEN-LAST:event_chk_FechaItemStateChanged

    private void chk_estadoFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk_estadoFacturaActionPerformed
        if (chk_estadoFactura.isSelected()) {
            rb_soloImpagas.setEnabled(true);
            rb_soloPagadas.setEnabled(true);
        } else {
            rb_soloImpagas.setEnabled(false);
            rb_soloPagadas.setEnabled(false);
        }
    }//GEN-LAST:event_chk_estadoFacturaActionPerformed

    private void rb_soloPagadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_soloPagadasActionPerformed
        rb_soloImpagas.setSelected(false);
    }//GEN-LAST:event_rb_soloPagadasActionPerformed

    private void rb_soloImpagasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_soloImpagasActionPerformed
        rb_soloPagadas.setSelected(false);
    }//GEN-LAST:event_rb_soloImpagasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JButton btn_VerPagos;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumFactura;
    private javax.swing.JCheckBox chk_Proveedor;
    private javax.swing.JCheckBox chk_estadoFactura;
    private javax.swing.JComboBox cmb_Proveedor;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbl_CantRegistrosEncontrados;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_TotalFacturado;
    private javax.swing.JLabel lbl_TotalIVACompra;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JRadioButton rb_soloImpagas;
    private javax.swing.JRadioButton rb_soloPagadas;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txt_NroFactura;
    private javax.swing.JFormattedTextField txt_ResultGastoTotal;
    private javax.swing.JFormattedTextField txt_ResultTotalIVACompra;
    private javax.swing.JFormattedTextField txt_SerieFactura;
    // End of variables declaration//GEN-END:variables
}
