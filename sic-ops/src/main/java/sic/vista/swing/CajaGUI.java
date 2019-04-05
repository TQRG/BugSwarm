package sic.vista.swing;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Caja;
import sic.modelo.EmpresaActiva;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.Pago;
import sic.modelo.UsuarioActivo;
import sic.modelo.EstadoCaja;
import sic.util.ColoresNumerosTablaRenderer;
import sic.util.FormatoFechasEnTablasRenderer;
import sic.util.FormatterFechaHora;
import sic.util.Utilidades;

public class CajaGUI extends JInternalFrame {

    private final FormatterFechaHora formatter = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_HISPANO);
    private ModeloTabla modeloTablaBalance;
    private ModeloTabla modeloTablaResumen;
    private final List<Object> listaMovimientos = new ArrayList<>();
    private Caja caja;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public CajaGUI(Caja caja) {
        initComponents();
        this.setSize(850, 600);
        this.caja = caja;
        this.setTituloVentana();
    }

    private void cargarDatosBalance(java.awt.event.KeyEvent evt) {
        int row = tbl_Resumen.getSelectedRow();
        if (row != -1) {
            row = Utilidades.getSelectedRowModelIndice(tbl_Resumen);
            if (evt != null) {
                if ((evt.getKeyCode() == KeyEvent.VK_UP) && row > 0) {
                    row--;
                }
                if ((evt.getKeyCode() == KeyEvent.VK_DOWN) && (row + 1) < tbl_Resumen.getRowCount()) {
                    row++;
                }
            }
            if (row != 0) {
                FormaDePago fdp = (FormaDePago) tbl_Resumen.getModel().getValueAt(row, 0);
                if (this.caja != null) {
                    this.listaMovimientos.clear();
                    Date hasta = new Date();
                    if (this.caja.getEstado() == EstadoCaja.CERRADA) {
                        hasta = this.caja.getFechaCierre();
                    }
                    try {
                        String criteriaPagos = "/pagos/busqueda?"
                                + "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idFormaDePago=" + fdp.getId_FormaDePago()
                                + "&desde=" + this.caja.getFechaApertura().getTime()
                                + "&hasta=" + hasta.getTime();
                        List<Pago> pagos = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                                .getForObject(criteriaPagos,
                                        Pago[].class)));
                        this.listaMovimientos.addAll(pagos);
                        String criteriaGastos = "/gastos/busqueda?"
                                + "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idFormaDePago=" + fdp.getId_FormaDePago()
                                + "&desde=" + this.caja.getFechaApertura().getTime()
                                + "&hasta=" + hasta.getTime();
                        List<Gasto> gastos = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                                .getForObject(criteriaGastos,
                                        Gasto[].class)));
                        this.listaMovimientos.addAll(gastos);
                        this.cargarMovimientosEnLaTablaBalance(this.listaMovimientos);
                    } catch (RestClientResponseException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (ResourceAccessException ex) {
                        LOGGER.error(ex.getMessage());
                        JOptionPane.showMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                this.limpiarTablaBalance();
            }
        }
    }

    private void limpiarTablaBalance() {
        modeloTablaBalance = new ModeloTabla();
        tbl_Balance.setModel(modeloTablaBalance);
        this.setColumnasDeTablaBalance();
    }

    private void limpiarTablaResumen() {
        modeloTablaResumen = new ModeloTabla();
        tbl_Resumen.setModel(modeloTablaResumen);
        this.setColumnasDeTablaResumenGeneral();
    }

    private void setColumnasDeTablaBalance() {
        //sorting
        tbl_Balance.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[3];
        encabezados[0] = "Concepto";
        encabezados[1] = "Fecha";
        encabezados[2] = "Monto";
        modeloTablaBalance.setColumnIdentifiers(encabezados);
        tbl_Balance.setModel(modeloTablaBalance);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaBalance.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = Date.class;
        tipos[2] = Double.class;
        modeloTablaBalance.setClaseColumnas(tipos);
        tbl_Balance.getTableHeader().setReorderingAllowed(false);
        tbl_Balance.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Balance.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl_Balance.getColumnModel().getColumn(1).setPreferredWidth(5);
        tbl_Balance.getColumnModel().getColumn(2).setCellRenderer(new ColoresNumerosTablaRenderer());
        tbl_Balance.getColumnModel().getColumn(1).setCellRenderer(new FormatoFechasEnTablasRenderer());

    }

    private void cargarMovimientosEnLaTablaBalance(List<Object> movimientos) {
        this.limpiarTablaBalance();
        movimientos.stream().map((movimiento) -> {
            Object[] fila = new Object[5];
            if (movimiento instanceof Gasto) {
                fila[0] = "Gasto: " + ((Gasto) movimiento).getConcepto();
                fila[1] = ((Gasto) movimiento).getFecha();
                fila[2] = -((Gasto) movimiento).getMonto();
            }
            if (movimiento instanceof Pago) {
                String tipoFactura = "";
                fila[1] = ((Pago) movimiento).getFecha();
                if ((((Pago) movimiento).getFactura() instanceof FacturaCompra)) {
                    fila[2] = -((Pago) movimiento).getMonto();
                    tipoFactura = "Compra";
                }
                if ((((Pago) movimiento).getFactura() instanceof FacturaVenta)) {
                    fila[2] = ((Pago) movimiento).getMonto();
                    tipoFactura = "Venta";
                }
                fila[0] = "Pago por: Factura " + tipoFactura
                        + " \"" + ((Pago) movimiento).getFactura().getTipoFactura() + "\""
                        + " Nº " + ((Pago) movimiento).getFactura().getNumSerie()
                        + " - " + ((Pago) movimiento).getFactura().getNumFactura();
            }
            return fila;
        }).forEach((fila) -> {
            modeloTablaBalance.addRow(fila);
        });
        tbl_Balance.setModel(modeloTablaBalance);
        //Ordena la tabla segun la Fecha
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tbl_Balance.getModel());
        tbl_Balance.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        int columnIndexToSort = 1;
        sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private void setColumnasDeTablaResumenGeneral() {
        //sorting
        tbl_Resumen.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[3];
        encabezados[0] = "Forma de Pago";
        encabezados[1] = "Afecta la Caja";
        encabezados[2] = "Total";
        modeloTablaResumen.setColumnIdentifiers(encabezados);
        tbl_Resumen.setModel(modeloTablaResumen);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResumen.getColumnCount()];
        tipos[0] = FormaDePago.class;
        tipos[1] = Boolean.class;
        tipos[2] = Double.class;
        modeloTablaResumen.setClaseColumnas(tipos);
        tbl_Resumen.getTableHeader().setReorderingAllowed(false);
        tbl_Resumen.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Resumen.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl_Resumen.getColumnModel().getColumn(1).setPreferredWidth(5);

    }

    private void cargarTablaResumenGeneral() {
        if (this.caja != null) {
            double totalGeneral = this.caja.getSaldoInicial();
            Object[] saldoInicial = new Object[3];
            FormaDePago saldoApertura = new FormaDePago();
            saldoApertura.setNombre("Saldo Apertura");
            saldoInicial[0] = saldoApertura;
            saldoInicial[1] = true;
            saldoInicial[2] = totalGeneral;
            double totalCaja = this.caja.getSaldoInicial();
            modeloTablaResumen.addRow(saldoInicial);
            try {
                for (FormaDePago formaDePago : this.getFormasDePago()) {
                    Date hasta = new Date();
                    if (this.caja.getEstado() == EstadoCaja.CERRADA) {
                        hasta = this.caja.getFechaCierre();
                    }
                    List<Pago> pagosPorFormaDePago = this.getPagosPorFechaYFormaDePago(formaDePago.getId_FormaDePago(),
                            this.caja.getFechaApertura().getTime(), hasta.getTime());
                    List<Gasto> gastosPorFormaDePago = this.getGastosPorFechaYFormaDePago(formaDePago.getId_FormaDePago(),
                            this.caja.getFechaApertura().getTime(), hasta.getTime());
                    if (pagosPorFormaDePago.size() > 0 || gastosPorFormaDePago.size() > 0) {
                        Object[] fila = new Object[3];
                        fila[0] = formaDePago;
                        this.listaMovimientos.clear();
                        this.listaMovimientos.addAll(pagosPorFormaDePago);
                        this.listaMovimientos.addAll(gastosPorFormaDePago);

                        String uriPagos = "/cajas/total-pagos?idPago=";
                        long[] idsPagos = new long[pagosPorFormaDePago.size()];
                        int indice = 0;
                        for (Pago pago : pagosPorFormaDePago) {
                            idsPagos[indice] = pago.getId_Pago();
                            indice++;
                        }

                        String uriGastos = "/cajas/total-gastos?idGasto=";
                        indice = 0;
                        long[] idsGastos = new long[gastosPorFormaDePago.size()];
                        for (Gasto gasto : gastosPorFormaDePago) {
                            idsGastos[indice] = gasto.getId_Gasto();
                            indice++;
                        }
                        double totalPagos = 0;
                        if (!pagosPorFormaDePago.isEmpty()) {
                            totalPagos += RestClient.getRestTemplate()
                                    .getForObject(uriPagos
                                            + Arrays.toString(idsPagos).substring(1, Arrays.toString(idsPagos).length() - 1),
                                            double.class);
                        }
                        double totalGastos = 0;
                        if (!gastosPorFormaDePago.isEmpty()) {
                            totalGastos = RestClient.getRestTemplate()
                                    .getForObject(uriGastos
                                            + Arrays.toString(idsGastos).substring(1, Arrays.toString(idsGastos).length() - 1),
                                            double.class);
                        }
                        double totalParcial = totalPagos - totalGastos;
                        fila[1] = formaDePago.isAfectaCaja();
                        fila[2] = totalParcial;
                        totalGeneral += totalParcial;
                        if (formaDePago.isAfectaCaja()) {
                            totalCaja += totalParcial;
                        }
                        modeloTablaResumen.addRow(fila);
                    }
                }
                this.ftxt_saldoCaja.setValue(totalCaja);
                caja.setSaldoFinal(totalCaja);
                this.ftxt_TotalGeneral.setValue(totalGeneral);
                //Guarda el monto final del último calculo en la caja
                RestClient.getRestTemplate().put("/cajas", this.caja);
                if (totalGeneral < 0) {
                    ftxt_TotalGeneral.setBackground(Color.PINK);
                }
                if (totalGeneral > 0) {
                    ftxt_TotalGeneral.setBackground(Color.GREEN);
                }
                if (totalCaja > 0) {
                    ftxt_saldoCaja.setBackground(Color.GREEN);
                }
                if (totalCaja < 0) {
                    ftxt_saldoCaja.setBackground(Color.PINK);
                }
                tbl_Resumen.setModel(modeloTablaResumen);
                tbl_Resumen.setDefaultRenderer(Double.class, new ColoresNumerosTablaRenderer());
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                this.dispose();
            }
        }
    }

    private void limpiarYCargarTablas() {
        this.limpiarTablaResumen();
        this.cargarTablaResumenGeneral();
        this.limpiarTablaBalance();
        this.cargarDatosBalance(null);
    }

    private void lanzarReporteCaja() {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/cajas/" + this.caja.getId_Caja() + "/empresas/"
                                + EmpresaActiva.getInstance().getEmpresa().getId_Empresa() + "/reporte",
                                byte[].class);
                File f = new File("Caja.pdf");
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

    private void setTituloVentana() {
        if (this.caja != null) {
            this.setTitle("Arqueo de Caja - Apertura: " + formatter.format(this.caja.getFechaApertura()));
        } else {
            this.setTitle("Arqueo De Caja");
        }
    }

    private void lanzarReporteFacturaVenta(Object movimientoDeTabla) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/facturas/" + ((Pago) movimientoDeTabla).getFactura().getId_Factura() + "/reporte",
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

    private void verDetalleFacturaCompra(Object movimientoDeTabla) {
        FacturaCompra factura = (FacturaCompra) ((Pago) movimientoDeTabla).getFactura();
        try {
            factura.setPagos(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/pagos/facturas/" + factura.getId_Factura(),
                            Pago[].class)));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        DetalleFacturaCompraGUI gui_DetalleFacturaCompra = new DetalleFacturaCompraGUI(factura);
        gui_DetalleFacturaCompra.setModal(true);
        gui_DetalleFacturaCompra.setLocationRelativeTo(this);
        gui_DetalleFacturaCompra.setVisible(true);
    }

    private List<FormaDePago> getFormasDePago() {
        return new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/formas-de-pago/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                        FormaDePago[].class)));
    }

    private List<Pago> getPagosPorFechaYFormaDePago(long idFormaDePago, long fechaDesde, long fechaHasta) {
        String criteriaPagos = "/pagos/busqueda?"
                + "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                + "&idFormaDePago=" + idFormaDePago
                + "&desde=" + fechaDesde
                + "&hasta=" + fechaHasta;
        return new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject(criteriaPagos, Pago[].class)));
    }

    private List<Gasto> getGastosPorFechaYFormaDePago(long idFormaDePago, long fechaDesde, long fechaHasta) {
        String criteriaGastos = "/gastos/busqueda?"
                + "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                + "&idFormaDePago=" + idFormaDePago
                + "&desde=" + fechaDesde
                + "&hasta=" + fechaHasta;
        return new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject(criteriaGastos, Gasto[].class)));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Resumen = new javax.swing.JPanel();
        sp_TablaResumen = new javax.swing.JScrollPane();
        tbl_Resumen = new javax.swing.JTable();
        sp_Tabla = new javax.swing.JScrollPane();
        tbl_Balance = new javax.swing.JTable();
        lbl_movimientos = new javax.swing.JLabel();
        btn_VerDetalle = new javax.swing.JButton();
        btn_EliminarGasto = new javax.swing.JButton();
        lbl_estado = new javax.swing.JLabel();
        lbl_aviso = new javax.swing.JLabel();
        btn_Imprimir = new javax.swing.JButton();
        btn_CerrarCaja = new javax.swing.JButton();
        btn_AgregarGasto = new javax.swing.JButton();
        lbl_Total = new javax.swing.JLabel();
        lbl_totalCaja = new javax.swing.JLabel();
        ftxt_saldoCaja = new javax.swing.JFormattedTextField();
        ftxt_TotalGeneral = new javax.swing.JFormattedTextField();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Caja_16x16.png"))); // NOI18N
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

        pnl_Resumen.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen General"));

        tbl_Resumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resumen.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resumen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResumenMouseClicked(evt);
            }
        });
        tbl_Resumen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResumenKeyPressed(evt);
            }
        });
        sp_TablaResumen.setViewportView(tbl_Resumen);

        tbl_Balance.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Tabla.setViewportView(tbl_Balance);

        lbl_movimientos.setText("Movimientos por Forma de Pago (Seleccione una de la lista superior)");

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        btn_EliminarGasto.setForeground(java.awt.Color.blue);
        btn_EliminarGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CoinsDel_16x16.png"))); // NOI18N
        btn_EliminarGasto.setText("Eliminar Gasto");
        btn_EliminarGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarGastoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_ResumenLayout = new javax.swing.GroupLayout(pnl_Resumen);
        pnl_Resumen.setLayout(pnl_ResumenLayout);
        pnl_ResumenLayout.setHorizontalGroup(
            pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_TablaResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
            .addComponent(sp_Tabla, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnl_ResumenLayout.createSequentialGroup()
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_movimientos)
                    .addGroup(pnl_ResumenLayout.createSequentialGroup()
                        .addComponent(btn_VerDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_EliminarGasto)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnl_ResumenLayout.setVerticalGroup(
            pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ResumenLayout.createSequentialGroup()
                .addComponent(sp_TablaResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_movimientos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Tabla, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_VerDetalle)
                    .addComponent(btn_EliminarGasto)))
        );

        pnl_ResumenLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_EliminarGasto, btn_VerDetalle});

        lbl_estado.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lbl_estado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_estado.setText("Estado:");

        lbl_aviso.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lbl_aviso.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btn_Imprimir.setForeground(java.awt.Color.blue);
        btn_Imprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Printer_16x16.png"))); // NOI18N
        btn_Imprimir.setText("Imprimir");
        btn_Imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ImprimirActionPerformed(evt);
            }
        });

        btn_CerrarCaja.setForeground(java.awt.Color.blue);
        btn_CerrarCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CerrarCaja_16x16.png"))); // NOI18N
        btn_CerrarCaja.setText("Cerrar Caja");
        btn_CerrarCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CerrarCajaActionPerformed(evt);
            }
        });

        btn_AgregarGasto.setForeground(java.awt.Color.blue);
        btn_AgregarGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CoinsAdd_16x16.png"))); // NOI18N
        btn_AgregarGasto.setText("Nuevo Gasto");
        btn_AgregarGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarGastoActionPerformed(evt);
            }
        });

        lbl_Total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Total.setText("Total General:");

        lbl_totalCaja.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_totalCaja.setText("Total que afecta Caja:");

        ftxt_saldoCaja.setEditable(false);
        ftxt_saldoCaja.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_saldoCaja.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_saldoCaja.setText("0");
        ftxt_saldoCaja.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        ftxt_TotalGeneral.setEditable(false);
        ftxt_TotalGeneral.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalGeneral.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_TotalGeneral.setText("0");
        ftxt_TotalGeneral.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_AgregarGasto)
                        .addGap(0, 0, 0)
                        .addComponent(btn_CerrarCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftxt_TotalGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbl_totalCaja)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftxt_saldoCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(pnl_Resumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_estado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_aviso, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftxt_TotalGeneral, ftxt_saldoCaja});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_CerrarCaja, btn_Imprimir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_aviso, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_estado, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Resumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_totalCaja, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ftxt_saldoCaja, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Imprimir)
                    .addComponent(btn_AgregarGasto)
                    .addComponent(btn_CerrarCaja)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ftxt_TotalGeneral))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftxt_TotalGeneral, ftxt_saldoCaja, lbl_Total, lbl_totalCaja});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_CerrarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CerrarCajaActionPerformed
        if (caja != null) {
            if (caja.getEstado() == EstadoCaja.ABIERTA) {
                try {
                    String monto = JOptionPane.showInputDialog(this,
                            "Saldo del Sistema: " + new DecimalFormat("#.##").format(caja.getSaldoFinal())
                            + "\nSaldo Real:", "Cerrar Caja", JOptionPane.QUESTION_MESSAGE);
                    if (monto != null) {
                        RestClient.getRestTemplate().put("/cajas/" + caja.getId_Caja() + "/cierre?"
                                + "monto=" + Double.parseDouble(monto)
                                + "&idUsuarioCierre=" + UsuarioActivo.getInstance().getUsuario().getId_Usuario(),
                                Caja.class);
                        this.lanzarReporteCaja();
                        this.dispose();
                    }
                } catch (NumberFormatException e) {
                    String msjError = "Monto inválido";
                    LOGGER.error(msjError + " - " + e.getMessage());
                    JOptionPane.showMessageDialog(this, msjError, "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_CerrarCajaActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Balance.getSelectedRow() != -1) {
            Object movimientoDeTabla = this.listaMovimientos.get(Utilidades.getSelectedRowModelIndice(tbl_Balance));
            if (movimientoDeTabla instanceof Pago) {
                if (((Pago) movimientoDeTabla).getFactura() instanceof FacturaVenta) {
                    this.lanzarReporteFacturaVenta(movimientoDeTabla);
                }
                if (((Pago) movimientoDeTabla).getFactura() instanceof FacturaCompra) {
                    this.verDetalleFacturaCompra(movimientoDeTabla);
                }
            }
            if (movimientoDeTabla instanceof Gasto) {
                String mensaje = "En Concepto de: " + ((Gasto) movimientoDeTabla).getConcepto()
                        + "\nMonto: " + ((Gasto) movimientoDeTabla).getMonto() + "\nUsuario: " + ((Gasto) movimientoDeTabla).getUsuario().getNombre();
                JOptionPane.showMessageDialog(this, mensaje, "Resumen de Gasto", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btn_AgregarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarGastoActionPerformed
        try {
            if (this.caja.getEstado().equals(EstadoCaja.ABIERTA)) {
                List<FormaDePago> formasDePago = Arrays.asList(RestClient.getRestTemplate().getForObject("/formas-de-pago/empresas/"
                        + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), FormaDePago[].class));
                AgregarGastoGUI agregarGasto = new AgregarGastoGUI(formasDePago);
                agregarGasto.setLocationRelativeTo(null);
                agregarGasto.setEnabled(true);
                agregarGasto.setVisible(true);
                this.limpiarYCargarTablas();
            } else if (this.caja.getEstado().equals(EstadoCaja.CERRADA)) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_AgregarGastoActionPerformed

    private void btn_ImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ImprimirActionPerformed
        this.lanzarReporteCaja();
    }//GEN-LAST:event_btn_ImprimirActionPerformed

    private void btn_EliminarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarGastoActionPerformed
        if (tbl_Balance.getSelectedRow() != -1) {
            Object movimientoDeTabla = this.listaMovimientos.get(Utilidades.getSelectedRowModelIndice(tbl_Balance));
            if (movimientoDeTabla instanceof Gasto && this.caja.getEstado().equals(EstadoCaja.ABIERTA)) {
                int confirmacionEliminacion = JOptionPane.showConfirmDialog(this,
                        "¿Esta seguro que desea eliminar el gasto seleccionado?",
                        "Eliminar", JOptionPane.YES_NO_OPTION);
                if (confirmacionEliminacion == JOptionPane.YES_OPTION) {
                    Gasto gasto = (Gasto) movimientoDeTabla;
                    try {
                        RestClient.getRestTemplate().delete("/gastos/" + gasto.getId_Gasto());
                        this.limpiarYCargarTablas();
                    } catch (RestClientResponseException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (ResourceAccessException ex) {
                        LOGGER.error(ex.getMessage());
                        JOptionPane.showMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if(this.caja.getEstado().equals(EstadoCaja.CERRADA)) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_EliminarGastoActionPerformed

    private void tbl_ResumenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResumenMouseClicked
        this.cargarDatosBalance(null);
    }//GEN-LAST:event_tbl_ResumenMouseClicked

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        if (caja == null) {
            try {
                caja = RestClient.getRestTemplate().getForObject("/cajas/empresas/"
                        + EmpresaActiva.getInstance().getEmpresa().getId_Empresa() + "/ultima",
                        Caja.class);
                this.setTituloVentana();
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(getParent(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(getParent(),
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (this.caja.getEstado() == EstadoCaja.ABIERTA) {
            lbl_aviso.setText("Abierta");
            lbl_aviso.setForeground(Color.GREEN);
        } else {
            lbl_aviso.setText("Cerrada");
            lbl_aviso.setForeground(Color.RED);
        }
        this.limpiarYCargarTablas();
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbl_ResumenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResumenKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            this.cargarDatosBalance(evt);
        }
    }//GEN-LAST:event_tbl_ResumenKeyPressed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AgregarGasto;
    private javax.swing.JButton btn_CerrarCaja;
    private javax.swing.JButton btn_EliminarGasto;
    private javax.swing.JButton btn_Imprimir;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JFormattedTextField ftxt_TotalGeneral;
    private javax.swing.JFormattedTextField ftxt_saldoCaja;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_aviso;
    private javax.swing.JLabel lbl_estado;
    private javax.swing.JLabel lbl_movimientos;
    private javax.swing.JLabel lbl_totalCaja;
    private javax.swing.JPanel pnl_Resumen;
    private javax.swing.JScrollPane sp_Tabla;
    private javax.swing.JScrollPane sp_TablaResumen;
    javax.swing.JTable tbl_Balance;
    private javax.swing.JTable tbl_Resumen;
    // End of variables declaration//GEN-END:variables

}
