package sic.vista.swing;

import java.beans.PropertyVetoException;
import java.text.ParseException;
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
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.Cliente;
import sic.modelo.Factura;
import sic.modelo.FacturaVenta;
import sic.modelo.Pedido;
import sic.modelo.Usuario;
import sic.service.EstadoPedido;
import sic.service.IClienteService;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.IPedidoService;
import sic.service.IUsuarioService;
import sic.service.Movimiento;
import sic.service.BusinessServiceException;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class GUI_FacturasVenta extends JInternalFrame {

    private ModeloTabla modeloTablaFacturas;
    private List<FacturaVenta> facturas;
    private final int cantidadResultadosParaMostrar = 0; //deshabilitada momentaneamente
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IFacturaService facturaService = appContext.getBean(IFacturaService.class);
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);
    private final IClienteService clienteService = appContext.getBean(IClienteService.class);
    private final IUsuarioService usuarioService = appContext.getBean(IUsuarioService.class);
    private final IPedidoService pedidoService = appContext.getBean(IPedidoService.class);
    private static final Logger LOGGER = Logger.getLogger(GUI_FacturasVenta.class.getPackage().getName());

    public GUI_FacturasVenta() {
        this.initComponents();
        modeloTablaFacturas = new ModeloTabla();
        this.setSize(940, 600);
        rb_soloImpagas.setSelected(true);
    }

    public void buscarConCriteria(BusquedaFacturaVentaCriteria criteria) {
        this.setEstadoDeComponentes(criteria);
        this.buscar(criteria);
    }

    public void actualizarEstadoPedido(Pedido pedido) {
        if (pedidoService.getFacturasDelPedido(pedido.getNroPedido()).isEmpty()) {
            pedido.setEstado(EstadoPedido.ABIERTO);
        } else {
            pedido.setEstado(EstadoPedido.ACTIVO);
        }
        pedidoService.actualizar(pedido);
    }

    private void setEstadoDeComponentes(BusquedaFacturaVentaCriteria criteria) {
        if (criteria.isBuscaCliente() && criteria.getCliente() != null) {
            chk_Cliente.setSelected(true);
            cmb_Cliente.setEnabled(true);
            cmb_Cliente.setSelectedItem(criteria.getCliente());
        }
        if (criteria.isBuscarPorPedido() && criteria.getNroPedido() != 0) {
            chk_NumeroPedido.setSelected(true);
            txt_NumeroPedido.setEnabled(true);
            txt_NumeroPedido.setText(String.valueOf(criteria.getNroPedido()));
        }
        if (criteria.isBuscaPorFecha() && criteria.getFechaDesde() != null && criteria.getFechaHasta() != null) {
            chk_Fecha.setSelected(true);
            dc_FechaDesde.setEnabled(true);
            dc_FechaDesde.setDate(criteria.getFechaDesde());
            dc_FechaHasta.setEnabled(false);
            dc_FechaHasta.setDate(criteria.getFechaHasta());
        }
        if (criteria.isBuscaPorNumeroFactura() && criteria.getNumFactura() != 0) {
            chk_NumFactura.setSelected(true);
            txt_NroFactura.setEnabled(true);
            txt_NroFactura.setText(String.valueOf(criteria.getNumFactura()));
        }
        if (criteria.isBuscaPorTipoFactura() && criteria.getTipoFactura() != '\u0000') {
            chk_NumFactura.setEnabled(true);
            cmb_TipoFactura.setEnabled(true);
            cmb_TipoFactura.setSelectedItem(criteria.getTipoFactura());
        }
        if (criteria.isBuscaSoloImpagas()) {
            chk_EstadoFactura.setSelected(true);
            rb_soloImpagas.setSelected(true);
        }
        if (criteria.isBuscaSoloPagadas()) {
            chk_EstadoFactura.setSelected(true);
            rb_soloPagadas.setSelected(true);
        }
        if (criteria.isBuscaUsuario() && criteria.getUsuario() != null) {
            chk_Cliente.setEnabled(true);
            cmb_Cliente.setEnabled(true);
            cmb_Cliente.setSelectedItem(criteria.getUsuario());
        }
    }

    private BusquedaFacturaVentaCriteria getCriteriaDeComponentes() {
        BusquedaFacturaVentaCriteria criteria = new BusquedaFacturaVentaCriteria();
        try {
            criteria.setBuscaPorFecha(chk_Fecha.isSelected());
            criteria.setFechaDesde(dc_FechaDesde.getDate());
            criteria.setFechaHasta(dc_FechaHasta.getDate());
            criteria.setBuscaCliente(chk_Cliente.isSelected());
            criteria.setCliente((Cliente) cmb_Cliente.getSelectedItem());
            criteria.setBuscaPorTipoFactura(chk_TipoFactura.isSelected());
            criteria.setTipoFactura(cmb_TipoFactura.getSelectedItem().toString().charAt(0));
            criteria.setBuscaUsuario(chk_Usuario.isSelected());
            criteria.setUsuario((Usuario) cmb_Usuario.getSelectedItem());
            criteria.setBuscaPorNumeroFactura(chk_NumFactura.isSelected());
            txt_SerieFactura.commitEdit();
            txt_NroFactura.commitEdit();
            criteria.setNumSerie(Integer.valueOf(txt_SerieFactura.getValue().toString()));
            criteria.setNumFactura(Integer.valueOf(txt_NroFactura.getValue().toString()));
            criteria.setBuscaSoloImpagas(chk_EstadoFactura.isSelected() && rb_soloImpagas.isSelected());
            criteria.setBuscaSoloPagadas(chk_EstadoFactura.isSelected() && rb_soloPagadas.isSelected());
            criteria.setEmpresa(empresaService.getEmpresaActiva().getEmpresa());
            criteria.setCantRegistros(cantidadResultadosParaMostrar);
            criteria.setBuscarPorPedido(chk_NumeroPedido.isSelected());
            if (chk_NumeroPedido.isSelected()) {
                criteria.setNroPedido(Long.parseLong(txt_NumeroPedido.getText()));
            }

        } catch (ParseException | BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return criteria;
    }

    private void setColumnas() {
        //sorting
        tbl_Resultados.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[16];
        encabezados[0] = "Fecha Factura";
        encabezados[1] = "Tipo";
        encabezados[2] = "Nº Factura";
        encabezados[3] = "Fecha Vencimiento";
        encabezados[4] = "Cliente";
        encabezados[5] = "Usuario (Vendedor)";
        encabezados[6] = "Transportista";
        encabezados[7] = "Pagada";
        encabezados[8] = "SubTotal";
        encabezados[9] = "% Recargo";
        encabezados[10] = "Recargo neto";
        encabezados[11] = "SubTotal neto";
        encabezados[12] = "IVA 10.5% neto";
        encabezados[13] = "IVA 21% neto";
        encabezados[14] = "Imp. Interno neto";
        encabezados[15] = "Total";
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
        tipos[6] = String.class;
        tipos[7] = Boolean.class;
        tipos[8] = Double.class;
        tipos[9] = Double.class;
        tipos[10] = Double.class;
        tipos[11] = Double.class;
        tipos[12] = Double.class;
        tipos[13] = Double.class;
        tipos[14] = Double.class;
        tipos[15] = Double.class;
        modeloTablaFacturas.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(60);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(190);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(140);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(80);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(15).setPreferredWidth(120);
    }

    private void calcularResultados() {
        txt_ResultTotalFacturado.setValue(facturaService.calcularTotalFacturadoVenta(facturas));
        txt_ResultGananciaTotal.setValue(facturaService.calcularGananciaTotal(facturas));
        txt_ResultTotalIVAVenta.setValue(facturaService.calcularIVA_Venta(facturas));

    }

    private void buscar(final BusquedaFacturaVentaCriteria criteria) {
        this.limpiarJTable();
        cambiarEstadoEnabled(false);
        pb_Filtro.setIndeterminate(true);
        SwingWorker<List<FacturaVenta>, Void> worker = new SwingWorker<List<FacturaVenta>, Void>() {

            @Override
            protected List<FacturaVenta> doInBackground() throws Exception {
                try {
                    facturas = facturaService.buscarFacturaVenta(criteria);
                    cargarResultadosAlTable();
                    calcularResultados();

                } catch (BusinessServiceException ex) {
                    JOptionPane.showInternalMessageDialog(getParent(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                } 

                cambiarEstadoEnabled(true);
                return facturas;
            }

            @Override
            protected void done() {
                pb_Filtro.setIndeterminate(false);
                try {
                    if (get().isEmpty()) {
                        JOptionPane.showInternalMessageDialog(getParent(), ResourceBundle.getBundle("Mensajes").getString("mensaje_busqueda_sin_resultados"),
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

    private void cambiarEstadoEnabled(boolean status) {
        chk_Fecha.setEnabled(status);
        if (status == true && chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
        chk_Cliente.setEnabled(status);
        if (status == true && chk_Cliente.isSelected() == true) {
            cmb_Cliente.setEnabled(true);
        } else {
            cmb_Cliente.setEnabled(false);
        }
        chk_Usuario.setEnabled(status);
        if (status == true && chk_Usuario.isSelected() == true) {
            cmb_Usuario.setEnabled(true);
        } else {
            cmb_Usuario.setEnabled(false);
        }
        chk_NumFactura.setEnabled(status);
        if (status == true && chk_NumFactura.isSelected() == true) {
            txt_SerieFactura.setEnabled(true);
            txt_NroFactura.setEnabled(true);
        } else {
            txt_SerieFactura.setEnabled(false);
            txt_NroFactura.setEnabled(false);
        }
        chk_TipoFactura.setEnabled(status);
        if (status == true && chk_TipoFactura.isSelected() == true) {
            cmb_TipoFactura.setEnabled(true);
        } else {
            cmb_TipoFactura.setEnabled(false);
        }
        chk_NumeroPedido.setEnabled(status);
        if (status == true && chk_NumeroPedido.isSelected() == true) {
            txt_NumeroPedido.setEnabled(true);
        } else {
            txt_NumeroPedido.setEnabled(false);
        }

        chk_EstadoFactura.setEnabled(status);
        if (status == true && chk_EstadoFactura.isSelected() == true) {
            rb_soloImpagas.setEnabled(true);
            rb_soloPagadas.setEnabled(true);
        } else {
            rb_soloImpagas.setEnabled(false);
            rb_soloPagadas.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        btn_Nueva.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        btn_VerDetalle.setEnabled(status);
        btn_VerPagos.setEnabled(status);
    }

    private void cargarResultadosAlTable() {
        for (FacturaVenta factura : facturas) {
            Object[] fila = new Object[16];
            fila[0] = factura.getFecha();
            fila[1] = String.valueOf(factura.getTipoFactura());
            fila[2] = factura.getNumSerie() + " - " + factura.getNumFactura();
            fila[3] = factura.getFechaVencimiento();
            fila[4] = factura.getCliente().getRazonSocial();
            fila[5] = factura.getUsuario().getNombre();
            fila[6] = factura.getTransportista().getNombre();
            fila[7] = factura.isPagada();
            fila[8] = factura.getSubTotal();
            fila[9] = factura.getRecargo_porcentaje();
            fila[10] = factura.getRecargo_neto();
            fila[11] = factura.getSubTotal_neto();
            fila[12] = factura.getIva_105_neto();
            fila[13] = factura.getIva_21_neto();
            fila[14] = factura.getImpuestoInterno_neto();
            fila[15] = factura.getTotal();
            modeloTablaFacturas.addRow(fila);
        }
        tbl_Resultados.setModel(modeloTablaFacturas);
        lbl_cantResultados.setText(facturas.size() + " facturas encontradas");
    }

    private void limpiarJTable() {
        modeloTablaFacturas = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaFacturas);
        this.setColumnas();
    }

    private void cargarComboBoxClientes() {
        cmb_Cliente.removeAllItems();
        List<Cliente> clientes;
        clientes = clienteService.getClientes(empresaService.getEmpresaActiva().getEmpresa());
        for (Cliente cliente : clientes) {
            cmb_Cliente.addItem(cliente);
        }
    }

    private void cargarComboBoxUsuarios() {
        cmb_Usuario.removeAllItems();
        List<Usuario> usuarios;
        usuarios = usuarioService.getUsuarios();
        for (Usuario usuario : usuarios) {
            cmb_Usuario.addItem(usuario);
        }
    }

    private void cargarComboBoxTipoFactura() {
        char[] tiposFactura = facturaService.getTiposFacturaSegunEmpresa(empresaService.getEmpresaActiva().getEmpresa());
        cmb_TipoFactura.removeAllItems();
        for (int i = 0; tiposFactura.length > i; i++) {
            cmb_TipoFactura.addItem(tiposFactura[i]);
        }
    }

    private void lanzarReporteFactura() throws JRException {
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            JasperPrint report = facturaService.getReporteFacturaVenta(facturas.get(indexFilaSeleccionada));

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

    private boolean existeClienteDisponible() {
        return !clienteService.getClientes(empresaService.getEmpresaActiva().getEmpresa()).isEmpty();
    }

    private void controlarEntradaSoloNumerico(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg_estadoFactura = new javax.swing.ButtonGroup();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_VerDetalle = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        panelNumeros = new javax.swing.JPanel();
        lbl_TotalFacturado = new javax.swing.JLabel();
        lbl_GananciaTotal = new javax.swing.JLabel();
        lbl_TotalIVAVenta = new javax.swing.JLabel();
        txt_ResultTotalFacturado = new javax.swing.JFormattedTextField();
        txt_ResultGananciaTotal = new javax.swing.JFormattedTextField();
        txt_ResultTotalIVAVenta = new javax.swing.JFormattedTextField();
        btn_Nueva = new javax.swing.JButton();
        btn_VerPagos = new javax.swing.JButton();
        panelFiltros = new javax.swing.JPanel();
        subPanelFiltros1 = new javax.swing.JPanel();
        chk_Fecha = new javax.swing.JCheckBox();
        chk_Cliente = new javax.swing.JCheckBox();
        cmb_Cliente = new javax.swing.JComboBox();
        chk_NumFactura = new javax.swing.JCheckBox();
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        separador = new javax.swing.JLabel();
        chk_Usuario = new javax.swing.JCheckBox();
        cmb_Usuario = new javax.swing.JComboBox();
        txt_SerieFactura = new javax.swing.JFormattedTextField();
        txt_NroFactura = new javax.swing.JFormattedTextField();
        subPanelFiltros2 = new javax.swing.JPanel();
        chk_TipoFactura = new javax.swing.JCheckBox();
        cmb_TipoFactura = new javax.swing.JComboBox();
        chk_NumeroPedido = new javax.swing.JCheckBox();
        txt_NumeroPedido = new javax.swing.JFormattedTextField();
        chk_EstadoFactura = new javax.swing.JCheckBox();
        rb_soloImpagas = new javax.swing.JRadioButton();
        rb_soloPagadas = new javax.swing.JRadioButton();
        btn_Buscar = new javax.swing.JButton();
        pb_Filtro = new javax.swing.JProgressBar();
        lbl_cantResultados = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Facturas de Venta");
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

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar ");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        lbl_TotalFacturado.setText("Total Facturado:");

        lbl_GananciaTotal.setText("Ganancia Total:");

        lbl_TotalIVAVenta.setText("Total IVA Venta:");

        txt_ResultTotalFacturado.setEditable(false);
        txt_ResultTotalFacturado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        txt_ResultTotalFacturado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_ResultTotalFacturado.setText("$0.0");

        txt_ResultGananciaTotal.setEditable(false);
        txt_ResultGananciaTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        txt_ResultGananciaTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_ResultGananciaTotal.setText("$0.0");

        txt_ResultTotalIVAVenta.setEditable(false);
        txt_ResultTotalIVAVenta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        txt_ResultTotalIVAVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_ResultTotalIVAVenta.setText("$0.0");

        javax.swing.GroupLayout panelNumerosLayout = new javax.swing.GroupLayout(panelNumeros);
        panelNumeros.setLayout(panelNumerosLayout);
        panelNumerosLayout.setHorizontalGroup(
            panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumerosLayout.createSequentialGroup()
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_TotalIVAVenta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_GananciaTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_TotalFacturado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_ResultTotalIVAVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                    .addComponent(txt_ResultGananciaTotal)
                    .addComponent(txt_ResultTotalFacturado, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        panelNumerosLayout.setVerticalGroup(
            panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumerosLayout.createSequentialGroup()
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TotalFacturado)
                    .addComponent(txt_ResultTotalFacturado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_GananciaTotal)
                    .addComponent(txt_ResultGananciaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TotalIVAVenta)
                    .addComponent(txt_ResultTotalIVAVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        btn_Nueva.setForeground(java.awt.Color.blue);
        btn_Nueva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Add_16x16.png"))); // NOI18N
        btn_Nueva.setText("Nueva");
        btn_Nueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaActionPerformed(evt);
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

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Nueva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addGap(0, 0, 0)
                .addComponent(btn_VerDetalle)
                .addGap(0, 0, 0)
                .addComponent(btn_VerPagos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(sp_Resultados)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Nueva, btn_VerDetalle, btn_VerPagos});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_Eliminar)
                        .addComponent(btn_VerDetalle)
                        .addComponent(btn_Nueva)
                        .addComponent(btn_VerPagos))))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_Nueva, btn_VerDetalle, btn_VerPagos});

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Fecha.setText("Fecha Factura:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        chk_Cliente.setText("Cliente:");
        chk_Cliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ClienteItemStateChanged(evt);
            }
        });

        cmb_Cliente.setEnabled(false);

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

        separador.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        separador.setText("-");

        chk_Usuario.setText("Usuario:");
        chk_Usuario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsuarioItemStateChanged(evt);
            }
        });

        cmb_Usuario.setEnabled(false);

        txt_SerieFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_SerieFactura.setText("0");
        txt_SerieFactura.setEnabled(false);
        txt_SerieFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_SerieFacturaKeyTyped(evt);
            }
        });

        txt_NroFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NroFactura.setText("0");
        txt_NroFactura.setEnabled(false);
        txt_NroFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NroFacturaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout subPanelFiltros1Layout = new javax.swing.GroupLayout(subPanelFiltros1);
        subPanelFiltros1.setLayout(subPanelFiltros1Layout);
        subPanelFiltros1Layout.setHorizontalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chk_Usuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Fecha, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Cliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_NumFactura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(txt_SerieFactura)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(separador)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_NroFactura))
                    .addComponent(cmb_Usuario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Cliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(lbl_Desde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_Hasta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaHasta, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))))
        );
        subPanelFiltros1Layout.setVerticalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(lbl_Desde)
                    .addComponent(chk_Fecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Cliente)
                    .addComponent(cmb_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Usuario)
                    .addComponent(cmb_Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separador)
                    .addComponent(txt_NroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_NumFactura)))
        );

        chk_TipoFactura.setText("Tipo de Factura:");
        chk_TipoFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_TipoFacturaItemStateChanged(evt);
            }
        });

        cmb_TipoFactura.setEnabled(false);

        chk_NumeroPedido.setText("Nº de Pedido:");
        chk_NumeroPedido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumeroPedidoItemStateChanged(evt);
            }
        });

        txt_NumeroPedido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NumeroPedido.setText("0");
        txt_NumeroPedido.setEnabled(false);
        txt_NumeroPedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NumeroPedidoKeyTyped(evt);
            }
        });

        chk_EstadoFactura.setText("Estado Factura:");
        chk_EstadoFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_EstadoFacturaItemStateChanged(evt);
            }
        });

        bg_estadoFactura.add(rb_soloImpagas);
        rb_soloImpagas.setText("Solo Impagas");
        rb_soloImpagas.setEnabled(false);

        bg_estadoFactura.add(rb_soloPagadas);
        rb_soloPagadas.setText("Solo Pagadas");
        rb_soloPagadas.setEnabled(false);

        javax.swing.GroupLayout subPanelFiltros2Layout = new javax.swing.GroupLayout(subPanelFiltros2);
        subPanelFiltros2.setLayout(subPanelFiltros2Layout);
        subPanelFiltros2Layout.setHorizontalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(chk_TipoFactura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chk_EstadoFactura))
                .addGap(12, 12, 12)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rb_soloPagadas)
                    .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cmb_TipoFactura, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(rb_soloImpagas, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        subPanelFiltros2Layout.setVerticalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_TipoFactura)
                    .addComponent(cmb_TipoFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_EstadoFactura)
                    .addComponent(rb_soloImpagas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rb_soloPagadas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addComponent(subPanelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subPanelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_Buscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pb_Filtro, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(subPanelFiltros1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subPanelFiltros2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Buscar)
                    .addComponent(pb_Filtro, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Buscar, pb_Filtro});

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
    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
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

    private void chk_ClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ClienteItemStateChanged
        if (chk_Cliente.isSelected() == true) {
            cmb_Cliente.setEnabled(true);
            cmb_Cliente.requestFocus();
        } else {
            cmb_Cliente.setEnabled(false);
        }
}//GEN-LAST:event_chk_ClienteItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.buscar(this.getCriteriaDeComponentes());
}//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        try {
            this.lanzarReporteFactura();

        } catch (JRException | BusinessServiceException ex ) {
            String msjError = "Se produjo un error procesando el reporte.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);

        } 
}//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar / anular la factura seleccionada?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    facturaService.eliminar(facturas.get(indexFilaSeleccionada));
                    if (facturas.get(indexFilaSeleccionada).getPedido() != null) {
                        Pedido pedidoDeFactura = pedidoService.getPedidoPorNumero(facturas.get(indexFilaSeleccionada).getPedido().getNroPedido(), empresaService.getEmpresaActiva().getEmpresa().getId_Empresa());
                        this.actualizarEstadoPedido(pedidoDeFactura);
                    }
                    this.buscar(this.getCriteriaDeComponentes());

                } catch (BusinessServiceException ex) {
                    JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
}//GEN-LAST:event_btn_EliminarActionPerformed

    private void chk_NumFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumFacturaItemStateChanged
        if (chk_NumFactura.isSelected() == true) {
            txt_NroFactura.setEnabled(true);
            txt_SerieFactura.setEnabled(true);
            txt_SerieFactura.requestFocus();
        } else {
            txt_NroFactura.setEnabled(false);
            txt_SerieFactura.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumFacturaItemStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.cargarComboBoxClientes();
            this.cargarComboBoxUsuarios();
            this.cargarComboBoxTipoFactura();
            this.setColumnas();
            this.setMaximum(true);

        } catch (PropertyVetoException | BusinessServiceException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void chk_UsuarioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UsuarioItemStateChanged
        if (chk_Usuario.isSelected() == true) {
            cmb_Usuario.setEnabled(true);
            cmb_Usuario.requestFocus();
        } else {
            cmb_Usuario.setEnabled(false);
        }
    }//GEN-LAST:event_chk_UsuarioItemStateChanged

    private void chk_TipoFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_TipoFacturaItemStateChanged
        if (chk_TipoFactura.isSelected() == true) {
            cmb_TipoFactura.setEnabled(true);
            cmb_TipoFactura.requestFocus();
        } else {
            cmb_TipoFactura.setEnabled(false);
        }
    }//GEN-LAST:event_chk_TipoFacturaItemStateChanged

    private void btn_NuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaActionPerformed
        try {
            if (this.existeClienteDisponible()) {
                GUI_PuntoDeVenta gui_puntoDeVenta = new GUI_PuntoDeVenta();
                gui_puntoDeVenta.setModal(true);
                gui_puntoDeVenta.setLocationRelativeTo(this);
                gui_puntoDeVenta.setVisible(true);
                this.cargarComboBoxClientes();
                this.buscar(this.getCriteriaDeComponentes());
            } else {
                String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente");
                JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevaActionPerformed

    private void txt_SerieFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_SerieFacturaKeyTyped
        this.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_SerieFacturaKeyTyped

    private void txt_NroFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NroFacturaKeyTyped
        this.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NroFacturaKeyTyped

    private void txt_NumeroPedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NumeroPedidoKeyTyped
        this.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NumeroPedidoKeyTyped

    private void chk_NumeroPedidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumeroPedidoItemStateChanged
        txt_NumeroPedido.setEnabled(chk_NumeroPedido.isSelected());
    }//GEN-LAST:event_chk_NumeroPedidoItemStateChanged

    private void chk_EstadoFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_EstadoFacturaItemStateChanged
        if (chk_EstadoFactura.isSelected() == true) {
            rb_soloImpagas.setEnabled(true);
            rb_soloPagadas.setEnabled(true);
        } else {
            rb_soloImpagas.setEnabled(false);
            rb_soloPagadas.setEnabled(false);
        }
    }//GEN-LAST:event_chk_EstadoFacturaItemStateChanged

    private void btn_VerPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerPagosActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            if (tbl_Resultados.getSelectedRowCount() == 1) {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                GUI_Pagos gui_Pagos = new GUI_Pagos(facturas.get(indexFilaSeleccionada));
                gui_Pagos.setModal(true);
                gui_Pagos.setLocationRelativeTo(this);
                gui_Pagos.setVisible(true);
                this.buscar(this.getCriteriaDeComponentes());
            }
            if (tbl_Resultados.getSelectedRowCount() > 1) {
                int[] indiceFacturas = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
                List<Factura> facturasVenta = new ArrayList<>();
                for (int i = 0; i < indiceFacturas.length; i++) {
                    facturasVenta.add(this.facturas.get(indiceFacturas[i]));
                }
                if (facturaService.validarFacturasParaPagoMultiple(facturasVenta, Movimiento.VENTA)) {
                    GUI_PagoMultiplesFacturas nuevoPagoMultiple = new GUI_PagoMultiplesFacturas(this, true, facturasVenta, Movimiento.VENTA);
                    nuevoPagoMultiple.setLocationRelativeTo(this);
                    nuevoPagoMultiple.setVisible(true);
                    this.buscar(this.getCriteriaDeComponentes());
                } else if (!facturaService.validarClienteProveedorParaPagosMultiples(facturasVenta, Movimiento.VENTA)) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle(
                            "Mensajes").getString("mensaje_facturas_distintos_clientes"), "Aviso", JOptionPane.ERROR_MESSAGE);
                } else if (!facturaService.validarFacturasImpagasParaPagoMultiple(facturasVenta)) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle(
                            "Mensajes").getString("mensaje_facturas_seEncuentran_pagadas"), "Aviso", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btn_VerPagosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_estadoFactura;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Nueva;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JButton btn_VerPagos;
    private javax.swing.JCheckBox chk_Cliente;
    private javax.swing.JCheckBox chk_EstadoFactura;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumFactura;
    private javax.swing.JCheckBox chk_NumeroPedido;
    private javax.swing.JCheckBox chk_TipoFactura;
    private javax.swing.JCheckBox chk_Usuario;
    private javax.swing.JComboBox cmb_Cliente;
    private javax.swing.JComboBox cmb_TipoFactura;
    private javax.swing.JComboBox cmb_Usuario;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_GananciaTotal;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_TotalFacturado;
    private javax.swing.JLabel lbl_TotalIVAVenta;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelNumeros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JProgressBar pb_Filtro;
    private javax.swing.JRadioButton rb_soloImpagas;
    private javax.swing.JRadioButton rb_soloPagadas;
    private javax.swing.JLabel separador;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JPanel subPanelFiltros1;
    private javax.swing.JPanel subPanelFiltros2;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txt_NroFactura;
    private javax.swing.JFormattedTextField txt_NumeroPedido;
    private javax.swing.JFormattedTextField txt_ResultGananciaTotal;
    private javax.swing.JFormattedTextField txt_ResultTotalFacturado;
    private javax.swing.JFormattedTextField txt_ResultTotalIVAVenta;
    private javax.swing.JFormattedTextField txt_SerieFactura;
    // End of variables declaration//GEN-END:variables
}
