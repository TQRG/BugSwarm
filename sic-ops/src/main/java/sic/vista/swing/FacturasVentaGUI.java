package sic.vista.swing;

import java.awt.Desktop;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.Factura;
import sic.modelo.FacturaVenta;
import sic.modelo.Usuario;
import sic.modelo.Movimiento;
import sic.modelo.Rol;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class FacturasVentaGUI extends JInternalFrame {

    private ModeloTabla modeloTablaFacturas;
    private List<FacturaVenta> facturas;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public FacturasVentaGUI() {
        this.initComponents();
        modeloTablaFacturas = new ModeloTabla();        
        rb_soloImpagas.setSelected(true);
    }

    public void buscarPorNroPedido(long nroPedido) {
        chk_NumeroPedido.setSelected(true);
        txt_NumeroPedido.setEnabled(true);
        txt_NumeroPedido.setText(String.valueOf(nroPedido));       
        this.buscar();               
    }
    
    private String getUriCriteria() {
        String uriCriteria = "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                + "&soloPagas=" + (chk_EstadoFactura.isSelected() && rb_soloPagadas.isSelected())
                + "&soloImpagas=" + (chk_EstadoFactura.isSelected() && rb_soloImpagas.isSelected());
        if (chk_Cliente.isSelected()) {
            uriCriteria += "&idCliente=" + ((Cliente) cmb_Cliente.getSelectedItem()).getId_Cliente();
        }
        if (chk_Fecha.isSelected()) {
            uriCriteria += "&desde=" + dc_FechaDesde.getDate().getTime()
                    + "&hasta=" + dc_FechaHasta.getDate().getTime();
        }
        if (chk_NumFactura.isSelected()) {
            uriCriteria += "&nroFactura=" + Long.valueOf(txt_NroFactura.getText())
                    + "&nroSerie=" + Long.valueOf(txt_SerieFactura.getText());
        }
        if (chk_TipoFactura.isSelected()) {
            uriCriteria += "&tipoFactura=" + cmb_TipoFactura.getSelectedItem().toString().charAt(0);
        }
        if (chk_Viajante.isSelected()) {
            uriCriteria += "&idViajante=" + ((Usuario) cmb_Viajante.getSelectedItem()).getId_Usuario();
        }
        if (chk_Vendedor.isSelected()) {
            uriCriteria += "&idUsuario=" + ((Usuario) cmb_Vendedor.getSelectedItem()).getId_Usuario();
        }
        if (chk_NumeroPedido.isSelected()) { 
            uriCriteria += "&nroPedido=" + Long.parseLong(txt_NumeroPedido.getText());
        }
        return uriCriteria;
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

    private void calcularResultados(String uriCriteria) {
        try {
            txt_ResultTotalFacturado.setValue(RestClient.getRestTemplate()
                    .getForObject("/facturas/total-facturado-venta/criteria?" + uriCriteria,
                            double.class));
            txt_ResultGananciaTotal.setValue(RestClient.getRestTemplate()
                    .getForObject("/facturas/ganancia-total/criteria?" + uriCriteria,
                            double.class));
            txt_ResultTotalIVAVenta.setValue(RestClient.getRestTemplate()
                    .getForObject("/facturas/total-iva-venta/criteria?" + uriCriteria,
                            double.class));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        this.limpiarJTable();
        cambiarEstadoEnabled(false);
        pb_Filtro.setIndeterminate(true);
        SwingWorker<List<FacturaVenta>, Void> worker = new SwingWorker<List<FacturaVenta>, Void>() {
                    
            @Override
            protected List<FacturaVenta> doInBackground() throws Exception {
                String uriCriteria = getUriCriteria();
                facturas = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/facturas/venta/busqueda/criteria?" + uriCriteria, Factura[].class)));
                cargarResultadosAlTable();
                calcularResultados(uriCriteria);
                cambiarEstadoEnabled(true);
                return facturas;
            }

            @Override
            protected void done() {
                pb_Filtro.setIndeterminate(false);
                try {
                    if (get().isEmpty()) {
                        JOptionPane.showInternalMessageDialog(getParent(),
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_busqueda_sin_resultados"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException ex) {
                    String msjError = "La tarea que se estaba realizando fue interrumpida. Intente nuevamente.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                    cambiarEstadoEnabled(true);
                } catch (ExecutionException ex) {
                    if (ex.getCause() instanceof RestClientResponseException) {
                        JOptionPane.showMessageDialog(getParent(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (ex.getCause() instanceof ResourceAccessException) {
                        LOGGER.error(ex.getMessage());
                        JOptionPane.showMessageDialog(getParent(),
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String msjError = "Se produjo un error en la ejecución de la tarea solicitada. Intente nuevamente.";
                        LOGGER.error(msjError + " - " + ex.getMessage());
                        JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    cambiarEstadoEnabled(true);
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
        chk_Viajante.setEnabled(status);
        if (status == true && chk_Viajante.isSelected() == true) {
            cmb_Viajante.setEnabled(true);
        } else {
            cmb_Viajante.setEnabled(false);
        }
        chk_Vendedor.setEnabled(status);
        if (status == true && chk_Vendedor.isSelected() == true) {
            cmb_Vendedor.setEnabled(true);
        } else {
            cmb_Vendedor.setEnabled(false);
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
        facturas.stream().map((factura) -> {
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
            return fila;
        }).forEach((fila) -> {
            modeloTablaFacturas.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaFacturas);
        lbl_cantResultados.setText(facturas.size() + " facturas encontradas");
    }

    private void limpiarJTable() {
        modeloTablaFacturas = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaFacturas);
        this.setColumnas();
    }

    private void cargarClientes() {
        if (chk_Cliente.isSelected() == true) {
            cmb_Cliente.setEnabled(true);
            cmb_Cliente.removeAllItems();
            try {
                List<Cliente> clientes = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/clientes/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                                Cliente[].class)));
                clientes.stream().forEach((c) -> {
                    cmb_Cliente.addItem(c);
                });
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            cmb_Cliente.requestFocus();
        } else {
            cmb_Cliente.removeAllItems();
            cmb_Cliente.setEnabled(false);
        }  
    }

    private void cargarUsuarios() {
        if (chk_Vendedor.isSelected() == true) {
            cmb_Vendedor.setEnabled(true);
            cmb_Vendedor.removeAllItems();
            try {
                List<Usuario> usuarios = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                                .getForObject("/usuarios/rol?"
                                + "rol=" + Rol.VENDEDOR,
                                Usuario[].class)));
                usuarios.stream().forEach((u) -> {
                    cmb_Vendedor.addItem(u);
                });
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            cmb_Vendedor.requestFocus();
        } else {
            cmb_Vendedor.removeAllItems();
            cmb_Vendedor.setEnabled(false);
        }
    }
    
    private void cargarUsuariosViajante() {
        if (chk_Viajante.isSelected() == true) {
            cmb_Viajante.setEnabled(true);
            cmb_Viajante.removeAllItems();
            try {
                List<Usuario> usuarios = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/usuarios/rol?"
                                + "rol=" + Rol.VIAJANTE,
                                Usuario[].class)));
                usuarios.stream().forEach((u) -> {
                    cmb_Viajante.addItem(u);
                });
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            cmb_Viajante.requestFocus();
        } else {
            cmb_Viajante.removeAllItems();
            cmb_Viajante.setEnabled(false);
        }
    }

    private void cargarTiposDeFactura() {
        try {
            char[] tiposFactura = RestClient.getRestTemplate()
                .getForObject("/facturas/tipos/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                char[].class);
            for (int i = 0; tiposFactura.length > i; i++) {
                cmb_TipoFactura.addItem(tiposFactura[i]);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lanzarReporteFactura() {
        if (Desktop.isDesktopSupported()) {
            try {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/facturas/" + facturas.get(indexFilaSeleccionada).getId_Factura() + "/reporte",
                        byte[].class);
                File f = new File("Factura.pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean existeClienteDisponible() {
        return !Arrays.asList(RestClient.getRestTemplate()
            .getForObject("/clientes/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
            Cliente[].class)).isEmpty();
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
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_Viajante = new javax.swing.JCheckBox();
        cmb_Viajante = new javax.swing.JComboBox();
        chk_Vendedor = new javax.swing.JCheckBox();
        cmb_Vendedor = new javax.swing.JComboBox();
        subPanelFiltros2 = new javax.swing.JPanel();
        btn_Buscar = new javax.swing.JButton();
        pb_Filtro = new javax.swing.JProgressBar();
        lbl_cantResultados = new javax.swing.JLabel();
        chk_TipoFactura = new javax.swing.JCheckBox();
        chk_NumeroPedido = new javax.swing.JCheckBox();
        chk_EstadoFactura = new javax.swing.JCheckBox();
        txt_NumeroPedido = new javax.swing.JFormattedTextField();
        rb_soloImpagas = new javax.swing.JRadioButton();
        rb_soloPagadas = new javax.swing.JRadioButton();
        cmb_TipoFactura = new javax.swing.JComboBox();
        chk_NumFactura = new javax.swing.JCheckBox();
        txt_SerieFactura = new javax.swing.JFormattedTextField();
        separador = new javax.swing.JLabel();
        txt_NroFactura = new javax.swing.JFormattedTextField();

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
        txt_ResultTotalFacturado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalFacturado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txt_ResultGananciaTotal.setEditable(false);
        txt_ResultGananciaTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultGananciaTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txt_ResultTotalIVAVenta.setEditable(false);
        txt_ResultTotalIVAVenta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalIVAVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
                .addComponent(panelNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(sp_Resultados)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Nueva, btn_VerDetalle, btn_VerPagos});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
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

        lbl_Hasta.setText("Hasta:");
        lbl_Hasta.setEnabled(false);

        lbl_Desde.setText("Desde:");
        lbl_Desde.setEnabled(false);

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        chk_Viajante.setText("Viajante");
        chk_Viajante.setToolTipText("");
        chk_Viajante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ViajanteItemStateChanged(evt);
            }
        });

        cmb_Viajante.setEnabled(false);

        chk_Vendedor.setText("Vendedor:");
        chk_Vendedor.setToolTipText("");
        chk_Vendedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_VendedorItemStateChanged(evt);
            }
        });

        cmb_Vendedor.setEnabled(false);

        javax.swing.GroupLayout subPanelFiltros1Layout = new javax.swing.GroupLayout(subPanelFiltros1);
        subPanelFiltros1.setLayout(subPanelFiltros1Layout);
        subPanelFiltros1Layout.setHorizontalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chk_Viajante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Fecha, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Cliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Vendedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_Viajante, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(lbl_Desde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_Hasta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(cmb_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cmb_Vendedor, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(chk_Viajante)
                    .addComponent(cmb_Viajante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Vendedor)
                    .addComponent(cmb_Vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout subPanelFiltros2Layout = new javax.swing.GroupLayout(subPanelFiltros2);
        subPanelFiltros2.setLayout(subPanelFiltros2Layout);
        subPanelFiltros2Layout.setHorizontalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        subPanelFiltros2Layout.setVerticalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
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

        chk_TipoFactura.setText("Tipo de Factura:");
        chk_TipoFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_TipoFacturaItemStateChanged(evt);
            }
        });

        chk_NumeroPedido.setText("Nº de Pedido:");
        chk_NumeroPedido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumeroPedidoItemStateChanged(evt);
            }
        });

        chk_EstadoFactura.setText("Estado Factura:");
        chk_EstadoFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_EstadoFacturaItemStateChanged(evt);
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

        bg_estadoFactura.add(rb_soloImpagas);
        rb_soloImpagas.setText("Solo Impagas");
        rb_soloImpagas.setEnabled(false);

        bg_estadoFactura.add(rb_soloPagadas);
        rb_soloPagadas.setText("Solo Pagadas");
        rb_soloPagadas.setEnabled(false);

        cmb_TipoFactura.setEnabled(false);

        chk_NumFactura.setText("Nº de Factura:");
        chk_NumFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumFacturaItemStateChanged(evt);
            }
        });

        txt_SerieFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_SerieFactura.setText("0");
        txt_SerieFactura.setEnabled(false);
        txt_SerieFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_SerieFacturaKeyTyped(evt);
            }
        });

        separador.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        separador.setText("-");

        txt_NroFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NroFactura.setText("0");
        txt_NroFactura.setEnabled(false);
        txt_NroFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NroFacturaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chk_NumFactura)
                                .addGap(18, 18, 18)
                                .addComponent(txt_SerieFactura)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_NroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(subPanelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(subPanelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_TipoFactura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chk_EstadoFactura))
                        .addGap(12, 12, 12)))
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_NumeroPedido)
                            .addComponent(cmb_TipoFactura, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rb_soloPagadas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(rb_soloImpagas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltrosLayout.createSequentialGroup()
                        .addComponent(pb_Filtro, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rb_soloImpagas, rb_soloPagadas});

        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(subPanelFiltros1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(subPanelFiltros2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(chk_TipoFactura)
                            .addComponent(cmb_TipoFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(chk_EstadoFactura)
                            .addComponent(rb_soloImpagas))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rb_soloPagadas)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_NumFactura)
                    .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_NroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(separador)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pb_Filtro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                    .addComponent(btn_Buscar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Buscar, pb_Filtro});

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rb_soloImpagas, rb_soloPagadas});

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
        this.cargarClientes();
}//GEN-LAST:event_chk_ClienteItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.buscar();        
}//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        this.lanzarReporteFactura();
}//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        int respuesta = JOptionPane.showConfirmDialog(this, ResourceBundle.getBundle("Mensajes")
                .getString("mensaje_eliminar_multiples_facturas"),
                "Eliminar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            int[] indexFilasSeleccionadas = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
            long [] idsFacturas = new long[indexFilasSeleccionadas.length];
            int i = 0;
            for (int indice : indexFilasSeleccionadas) {
                idsFacturas[i] = facturas.get(indice).getId_Factura();
                i++;
            }
            try {
                RestClient.getRestTemplate().delete("/facturas?idFactura="
                        + Arrays.toString(idsFacturas).substring(1, Arrays.toString(idsFacturas).length() - 1));
                this.buscar();
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
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
            this.setSize(940, 600);
            this.setColumnas();
            this.setMaximum(true);
            dc_FechaDesde.setDate(new Date());
            dc_FechaHasta.setDate(new Date());
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void chk_ViajanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ViajanteItemStateChanged
        this.cargarUsuariosViajante();
    }//GEN-LAST:event_chk_ViajanteItemStateChanged

    private void chk_TipoFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_TipoFacturaItemStateChanged
        if (chk_TipoFactura.isSelected() == true) {
            cmb_TipoFactura.setEnabled(true);
            this.cargarTiposDeFactura();
            cmb_TipoFactura.requestFocus();
        } else {
            cmb_TipoFactura.setEnabled(false);
            cmb_TipoFactura.removeAllItems();
        }
    }//GEN-LAST:event_chk_TipoFacturaItemStateChanged

    private void btn_NuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaActionPerformed
        if (this.existeClienteDisponible()) {
            PuntoDeVentaGUI gui_puntoDeVenta = new PuntoDeVentaGUI();
            gui_puntoDeVenta.setModal(true);
            gui_puntoDeVenta.setLocationRelativeTo(this);
            gui_puntoDeVenta.setVisible(true);
            this.cargarClientes();
            this.buscar();
        } else {
            String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente");
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }       
    }//GEN-LAST:event_btn_NuevaActionPerformed

    private void txt_SerieFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_SerieFacturaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_SerieFacturaKeyTyped

    private void txt_NroFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NroFacturaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NroFacturaKeyTyped

    private void txt_NumeroPedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NumeroPedidoKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
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
        try {
            if (tbl_Resultados.getSelectedRow() != -1) {
                if (tbl_Resultados.getSelectedRowCount() == 1) {
                    int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                    PagosGUI gui_Pagos = new PagosGUI(facturas.get(indexFilaSeleccionada));
                    gui_Pagos.setModal(true);
                    gui_Pagos.setLocationRelativeTo(this);
                    gui_Pagos.setVisible(true);
                    this.buscar();
                }
                if (tbl_Resultados.getSelectedRowCount() > 1) {
                    int[] indicesTabla = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
                    long[] idsFacturas = new long[indicesTabla.length];
                    List<Factura> facturasVenta = new ArrayList<>();
                    for (int i = 0; i < indicesTabla.length; i++) {
                        facturasVenta.add(this.facturas.get(indicesTabla[i]));
                        idsFacturas[i] = this.facturas.get(indicesTabla[i]).getId_Factura();
                    }
                    String uri = "/facturas/validaciones-pago-multiple?"
                            + "idFactura=" + Arrays.toString(idsFacturas).substring(1, Arrays.toString(idsFacturas).length() - 1)
                            + "&movimiento=" + Movimiento.VENTA;
                    boolean esValido = RestClient.getRestTemplate().getForObject(uri, boolean.class);
                    if (esValido) {
                        PagoMultiplesFacturasGUI gui_PagoMultiples = new PagoMultiplesFacturasGUI(this, facturasVenta, Movimiento.VENTA);
                        gui_PagoMultiples.setModal(true);
                        gui_PagoMultiples.setLocationRelativeTo(this);
                        gui_PagoMultiples.setVisible(true);
                        this.buscar();
                    }
                }
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_VerPagosActionPerformed

    private void chk_VendedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_VendedorItemStateChanged
        this.cargarUsuarios();
    }//GEN-LAST:event_chk_VendedorItemStateChanged

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
    private javax.swing.JCheckBox chk_Vendedor;
    private javax.swing.JCheckBox chk_Viajante;
    private javax.swing.JComboBox cmb_Cliente;
    private javax.swing.JComboBox cmb_TipoFactura;
    private javax.swing.JComboBox cmb_Vendedor;
    private javax.swing.JComboBox cmb_Viajante;
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
