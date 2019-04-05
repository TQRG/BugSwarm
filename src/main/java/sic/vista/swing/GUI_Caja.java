package sic.vista.swing;

import sic.util.ColoresNumerosTablaRenderer;
import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.Caja;
import sic.modelo.Empresa;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.Pago;
import sic.service.EstadoCaja;
import sic.service.ICajaService;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.IFormaDePagoService;
import sic.service.IGastoService;
import sic.service.IPagoService;
import sic.service.IUsuarioService;
import sic.util.FormatoFechasEnTablasRenderer;
import sic.util.FormatterFechaHora;
import sic.util.FormatterNumero;
import sic.util.Utilidades;

public class GUI_Caja extends javax.swing.JDialog {

    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final ICajaService cajaService = appContext.getBean(ICajaService.class);
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);
    private final IFormaDePagoService formaDePagoService = appContext.getBean(IFormaDePagoService.class);
    private final IFacturaService facturaService = appContext.getBean(IFacturaService.class);
    private final IGastoService gastoService = appContext.getBean(IGastoService.class);
    private final IUsuarioService usuarioService = appContext.getBean(IUsuarioService.class);
    private final IPagoService pagoService = appContext.getBean(IPagoService.class);
    private final FormatterFechaHora formatoHora = new FormatterFechaHora(FormatterFechaHora.FORMATO_HORA_INTERNACIONAL);
    private ModeloTabla modeloTablaBalance;
    private ModeloTabla modeloTablaResumen;
    private List<Object> listaMovimientos = new ArrayList<>();
    private Caja caja;
    private static final Logger LOGGER = Logger.getLogger(GUI_Caja.class.getPackage().getName());

    public GUI_Caja(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.caja = cajaService.getUltimaCaja(empresaService.getEmpresaActiva().getEmpresa().getId_Empresa());
        this.setTituloVentana();
    }

    public GUI_Caja(Frame parent, boolean modal, Caja caja) {
        super(parent, modal);
        initComponents();
        this.caja = caja;
        this.setTituloVentana();
    }

    public GUI_Caja(Caja caja) {
        initComponents();
        this.caja = caja;
        this.setTituloVentana();
    }

    private void cargarDatosBalance() {
        lbl_aviso.setText("Cerrada");
        lbl_aviso.setForeground(Color.RED);
        this.btn_AgregarGasto.setEnabled(false);
        this.btn_EliminarGasto.setEnabled(false);
        if (this.caja != null) {
            if (this.caja.getEstado() == EstadoCaja.ABIERTA) {
                lbl_aviso.setText("Abierta");
                lbl_aviso.setForeground(Color.GREEN);
                this.btn_AgregarGasto.setEnabled(true);
                this.btn_EliminarGasto.setEnabled(true);
            }
            this.listaMovimientos.clear();
            Date hasta = new Date();
            if (this.caja.getEstado() == EstadoCaja.CERRADA) {
                hasta = this.caja.getFechaCierre();
            }
            List<Pago> pagos = pagoService.getPagosEntreFechasYFormaDePago(
                    empresaService.getEmpresaActiva().getEmpresa().getId_Empresa(),
                    ((FormaDePago) cmb_FormasDePago.getSelectedItem()).getId_FormaDePago(),
                    this.caja.getFechaApertura(), hasta);
            this.listaMovimientos.addAll(pagos);
            List<Object> gastos = gastoService.getGastosPorFechaYFormaDePago(
                    empresaService.getEmpresaActiva().getEmpresa().getId_Empresa(),
                    ((FormaDePago) cmb_FormasDePago.getSelectedItem()).getId_FormaDePago(),
                    this.caja.getFechaApertura(), hasta);
            this.listaMovimientos.addAll(gastos);
            this.cargarMovimientosEnLaTablaBalance(this.listaMovimientos);
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
        encabezados[0] = "Fecha";
        encabezados[1] = "Concepto";
        encabezados[2] = "Monto";
        modeloTablaBalance.setColumnIdentifiers(encabezados);
        tbl_Balance.setModel(modeloTablaBalance);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaBalance.getColumnCount()];
        tipos[0] = Date.class;
        tipos[1] = String.class;
        tipos[2] = Double.class;
        modeloTablaBalance.setClaseColumnas(tipos);
        tbl_Balance.getTableHeader().setReorderingAllowed(false);
        tbl_Balance.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Balance.getColumnModel().getColumn(0).setPreferredWidth(15);
        tbl_Balance.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbl_Balance.getColumnModel().getColumn(2).setPreferredWidth(15);

    }

    private void cargarMovimientosEnLaTablaBalance(List<Object> movimientos) {
        this.limpiarTablaBalance();
        for (Object movimiento : movimientos) {
            Object[] fila = new Object[5];

            if (movimiento instanceof Gasto) {
                fila[0] = ((Gasto) movimiento).getFecha();
                fila[1] = "Gasto: " + ((Gasto) movimiento).getConcepto();
                fila[2] = -((Gasto) movimiento).getMonto();
            }

            if (movimiento instanceof Pago) {
                String tipoFactura = "";
                fila[0] = ((Pago) movimiento).getFecha();
                if ((((Pago) movimiento).getFactura() instanceof FacturaCompra)) {
                    fila[2] = -((Pago) movimiento).getMonto();
                    tipoFactura = "Compra";
                }
                if ((((Pago) movimiento).getFactura() instanceof FacturaVenta)) {
                    fila[2] = ((Pago) movimiento).getMonto();
                    tipoFactura = "Venta";
                }
                fila[1] = "Pago por: Factura " + tipoFactura
                        + " Nº " + ((Pago) movimiento).getFactura().getNumSerie()
                        + " - " + ((Pago) movimiento).getFactura().getNumFactura();
            }
            modeloTablaBalance.addRow(fila);
        }
        this.calcularTotalBalance();
        tbl_Balance.setModel(modeloTablaBalance);
        tbl_Balance.getColumnModel().getColumn(2).setCellRenderer(new ColoresNumerosTablaRenderer());
        tbl_Balance.getColumnModel().getColumn(0).setCellRenderer(new FormatoFechasEnTablasRenderer());
        //Ordena la tabla segun la Fecha
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tbl_Balance.getModel());
        tbl_Balance.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        int columnIndexToSort = 0;
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
        encabezados[2] = "Monto";
        modeloTablaResumen.setColumnIdentifiers(encabezados);
        tbl_Resumen.setModel(modeloTablaResumen);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResumen.getColumnCount()];
        tipos[0] = String.class;
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
            Empresa empresaActiva = empresaService.getEmpresaActiva().getEmpresa();
            double totalGeneral = this.caja.getSaldoInicial();
            Object[] saldoInicial = new Object[3];
            saldoInicial[0] = "Saldo Apertura";
            saldoInicial[1] = true;
            saldoInicial[2] = totalGeneral;
            double totalCaja = this.caja.getSaldoInicial();
            modeloTablaResumen.addRow(saldoInicial);
            List<FormaDePago> formasDePago = formaDePagoService.getFormasDePago(empresaActiva);
            for (FormaDePago formaDePago : formasDePago) {
                Date hasta = new Date();
                if (this.caja.getEstado() == EstadoCaja.CERRADA) {
                    hasta = this.caja.getFechaCierre();
                }
                List<Pago> pagosPorFormaDePago = pagoService.getPagosEntreFechasYFormaDePago(empresaActiva.getId_Empresa(),
                        formaDePago.getId_FormaDePago(), this.caja.getFechaApertura(), hasta);
                List<Object> gastosPorFormaDePago = gastoService.getGastosPorFechaYFormaDePago(empresaActiva.getId_Empresa(),
                        formaDePago.getId_FormaDePago(), this.caja.getFechaApertura(), hasta);
                if (pagosPorFormaDePago.size() > 0 || gastosPorFormaDePago.size() > 0) {
                    Object[] fila = new Object[3];
                    fila[0] = formaDePago.getNombre();
                    this.listaMovimientos.clear();
                    this.listaMovimientos.addAll(pagosPorFormaDePago);
                    this.listaMovimientos.addAll(gastosPorFormaDePago);
                    double totalParcial = cajaService.calcularTotalPorMovimiento(this.listaMovimientos);
                    fila[1] = formaDePago.isAfectaCaja();
                    fila[2] = totalParcial;
                    totalGeneral += totalParcial;
                    if (formaDePago.isAfectaCaja()) {
                        totalCaja += totalParcial;
                    }
                    modeloTablaResumen.addRow(fila);
                }
            }
            totalCaja = Utilidades.truncarDecimal(totalCaja, 2);
            this.ftxt_saldoCaja.setValue(totalCaja);
            caja.setSaldoFinal(totalCaja);
            this.ftxt_TotalGeneral.setValue(Math.floor(totalGeneral * 100) / 100);
            //Guarda el monto final del último calculo en la caja
            cajaService.actualizar(this.caja);
            if (this.caja.getSaldoFinal() < 0) {
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
        }
    }

    private void calcularTotalBalance() {
        double total = 0.0;
        for (int i = 0; i < modeloTablaBalance.getRowCount(); i++) {
            total += (Double) modeloTablaBalance.getValueAt(i, 2);
        }
        ftxt_Detalle.setValue(total);
        if ((Double) ftxt_Detalle.getValue() < 0) {
            ftxt_Detalle.setBackground(Color.PINK);
        }
        if ((Double) ftxt_Detalle.getValue() > 0) {
            ftxt_Detalle.setBackground(Color.GREEN);
        }
    }

    private void cargarElementosFormaDePago() {
        List<FormaDePago> formasDePago = formaDePagoService.getFormasDePago(empresaService.getEmpresaActiva().getEmpresa());
        if (cmb_FormasDePago.getItemCount() != formasDePago.size()) {
            cmb_FormasDePago.removeAllItems();
            for (FormaDePago formaDePago : formasDePago) {
                cmb_FormasDePago.addItem(formaDePago);
            }
        }
        this.limpiarTablaBalance();
        this.cargarDatosBalance();
    }

    private void limpiarYCargarTablas() {
        this.limpiarTablaResumen();
        this.cargarTablaResumenGeneral();
        this.cargarElementosFormaDePago();
    }

    private void lanzarReporteCaja() throws JRException {
        List<String> dataSource = new ArrayList<>();
        dataSource.add((String) tbl_Resumen.getValueAt(0, 0)
                + "-" + String.valueOf(FormatterNumero.formatConRedondeo((Number) tbl_Resumen.getValueAt(0, 2))));

        List<FormaDePago> formasDePago = formaDePagoService.getFormasDePago(empresaService.getEmpresaActiva().getEmpresa());
        double totalPorCorte = this.caja.getSaldoInicial();
        for (FormaDePago formaDePago : formasDePago) {
            double totalPorCorteFormaDePago = 0.0;
            List<Pago> pagos = pagoService.getPagosEntreFechasYFormaDePago(empresaService.getEmpresaActiva().getEmpresa().getId_Empresa(),
                    formaDePago.getId_FormaDePago(), this.caja.getFechaApertura(), this.caja.getFechaCorteInforme());
            List<Object> gastos = gastoService.getGastosPorFechaYFormaDePago(empresaService.getEmpresaActiva().getEmpresa().getId_Empresa(),
                    formaDePago.getId_FormaDePago(), this.caja.getFechaApertura(), this.caja.getFechaCorteInforme());
            for (Pago pago : pagos) {
                totalPorCorteFormaDePago += pagoService.getTotalPagado(pago.getFactura());
            }
            for (Object gasto : gastos) {
                totalPorCorteFormaDePago += ((Gasto) gasto).getMonto();
            }
            if (totalPorCorteFormaDePago > 0) {
                dataSource.add(formaDePago.getNombre() + "-" + totalPorCorteFormaDePago);
            }
            totalPorCorte += totalPorCorteFormaDePago;
        }
        dataSource.add("Total hasta la hora de control:-" + String.valueOf(FormatterNumero.formatConRedondeo((Number) totalPorCorte)));
        dataSource.add("..........................Corte a las: " + formatoHora.format(this.caja.getFechaCorteInforme()) + "...........................-");

        for (int f = 1; f < tbl_Resumen.getRowCount(); f++) {
            if ((boolean) tbl_Resumen.getValueAt(f, 1) == true) {
                dataSource.add((String) tbl_Resumen.getValueAt(f, 0) + " (Afecta Caja)"
                        + "-" + String.valueOf(FormatterNumero.formatConRedondeo((Number) tbl_Resumen.getValueAt(f, 2))));
            } else {
                dataSource.add((String) tbl_Resumen.getValueAt(f, 0) + " (No afecta Caja)"
                        + "-" + String.valueOf(FormatterNumero.formatConRedondeo((Number) tbl_Resumen.getValueAt(f, 2))));
            }
        }

        JasperPrint report = cajaService.getReporteCaja(this.caja, dataSource, this.caja.getUsuarioCierraCaja());
        JDialog viewer = new JDialog(new JFrame(), "Vista Previa", true);
        viewer.setSize(this.getWidth() - 25, this.getHeight() - 25);
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/SIC_16_square.png"));
        viewer.setIconImage(iconoVentana.getImage());
        viewer.setLocationRelativeTo(null);
        JRViewer jrv = new JRViewer(report);
        viewer.getContentPane().add(jrv);
        viewer.setVisible(true);
    }

    private void setTituloVentana() {
        if (this.caja != null) {
            this.setTitle("Arqueo de Caja - Apertura: " + formatoHora.format(this.caja.getFechaApertura()));
        } else {
            this.setTitle("Arqueo De Caja");
        }
    }

    private void lanzarReporteFacturaVenta(Object movimientoDeTabla) {
        try {
            FacturaVenta factura = (FacturaVenta) ((Pago) movimientoDeTabla).getFactura();
            factura.setPagos(pagoService.getPagosDeLaFactura(factura));
            JasperPrint report = facturaService.getReporteFacturaVenta(factura);
            JDialog viewer = new JDialog(new JFrame(), "Vista Previa", true);
            viewer.setSize(this.getWidth(), this.getHeight());
            ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/SIC_16_square.png"));
            viewer.setIconImage(iconoVentana.getImage());
            viewer.setLocationRelativeTo(null);
            JRViewer jrv = new JRViewer(report);
            viewer.getContentPane().add(jrv);
            viewer.setVisible(true);
        } catch (JRException ex) {
            String msjError = "Se produjo un error procesando el reporte.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verDetalleFacturaCompra(Object movimientoDeTabla) {
        FacturaCompra factura = (FacturaCompra) ((Pago) movimientoDeTabla).getFactura();
        factura.setPagos(pagoService.getPagosDeLaFactura(factura));
        GUI_DetalleFacturaCompra gui_DetalleFacturaCompra = new GUI_DetalleFacturaCompra(factura);
        gui_DetalleFacturaCompra.setModal(true);
        gui_DetalleFacturaCompra.setLocationRelativeTo(this);
        gui_DetalleFacturaCompra.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Tabla = new javax.swing.JPanel();
        sp_Tabla = new javax.swing.JScrollPane();
        tbl_Balance = new javax.swing.JTable();
        btn_VerDetalle = new javax.swing.JButton();
        lbl_total = new javax.swing.JLabel();
        btn_AgregarGasto = new javax.swing.JButton();
        ftxt_Detalle = new javax.swing.JFormattedTextField();
        lbl_FormaDePago = new javax.swing.JLabel();
        cmb_FormasDePago = new javax.swing.JComboBox<>();
        btn_EliminarGasto = new javax.swing.JButton();
        pnl_Resumen = new javax.swing.JPanel();
        sp_TablaResumen = new javax.swing.JScrollPane();
        tbl_Resumen = new javax.swing.JTable();
        ftxt_TotalGeneral = new javax.swing.JFormattedTextField();
        lbl_Total = new javax.swing.JLabel();
        lbl_totalCaja = new javax.swing.JLabel();
        ftxt_saldoCaja = new javax.swing.JFormattedTextField();
        lbl_estado = new javax.swing.JLabel();
        lbl_aviso = new javax.swing.JLabel();
        btn_Imprimir = new javax.swing.JButton();
        btn_CerrarCaja = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnl_Tabla.setBorder(javax.swing.BorderFactory.createTitledBorder("Movimientos por Forma de Pago"));

        tbl_Balance.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Tabla.setViewportView(tbl_Balance);

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        lbl_total.setText("Total:");

        btn_AgregarGasto.setForeground(java.awt.Color.blue);
        btn_AgregarGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CoinsAdd_16x16.png"))); // NOI18N
        btn_AgregarGasto.setText("Agregar Gasto");
        btn_AgregarGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarGastoActionPerformed(evt);
            }
        });

        ftxt_Detalle.setEditable(false);
        ftxt_Detalle.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        ftxt_Detalle.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_Detalle.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        lbl_FormaDePago.setText("Forma de Pago:");

        cmb_FormasDePago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_FormasDePagoActionPerformed(evt);
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

        javax.swing.GroupLayout pnl_TablaLayout = new javax.swing.GroupLayout(pnl_Tabla);
        pnl_Tabla.setLayout(pnl_TablaLayout);
        pnl_TablaLayout.setHorizontalGroup(
            pnl_TablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Tabla)
            .addGroup(pnl_TablaLayout.createSequentialGroup()
                .addComponent(btn_VerDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btn_AgregarGasto)
                .addGap(0, 0, 0)
                .addComponent(btn_EliminarGasto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                .addComponent(lbl_total)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftxt_Detalle, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnl_TablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_FormaDePago)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmb_FormasDePago, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_TablaLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_AgregarGasto, btn_EliminarGasto, btn_VerDetalle});

        pnl_TablaLayout.setVerticalGroup(
            pnl_TablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_TablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_TablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_FormaDePago)
                    .addComponent(cmb_FormasDePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Tabla, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_TablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_VerDetalle)
                    .addComponent(btn_AgregarGasto)
                    .addComponent(btn_EliminarGasto)
                    .addComponent(lbl_total)
                    .addComponent(ftxt_Detalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pnl_TablaLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_AgregarGasto, btn_EliminarGasto, btn_VerDetalle});

        pnl_Resumen.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen General"));

        tbl_Resumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sp_TablaResumen.setViewportView(tbl_Resumen);
        tbl_Resumen.getAccessibleContext().setAccessibleParent(sp_Tabla);

        ftxt_TotalGeneral.setEditable(false);
        ftxt_TotalGeneral.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        ftxt_TotalGeneral.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_TotalGeneral.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        lbl_Total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Total.setText("Total General:");

        lbl_totalCaja.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_totalCaja.setText("Total que afecta Caja:");

        ftxt_saldoCaja.setEditable(false);
        ftxt_saldoCaja.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        ftxt_saldoCaja.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_saldoCaja.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        javax.swing.GroupLayout pnl_ResumenLayout = new javax.swing.GroupLayout(pnl_Resumen);
        pnl_Resumen.setLayout(pnl_ResumenLayout);
        pnl_ResumenLayout.setHorizontalGroup(
            pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_TablaResumen)
            .addGroup(pnl_ResumenLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(pnl_ResumenLayout.createSequentialGroup()
                        .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_TotalGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_ResumenLayout.createSequentialGroup()
                        .addComponent(lbl_totalCaja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_saldoCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        pnl_ResumenLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftxt_TotalGeneral, ftxt_saldoCaja});

        pnl_ResumenLayout.setVerticalGroup(
            pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ResumenLayout.createSequentialGroup()
                .addComponent(sp_TablaResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_totalCaja)
                    .addComponent(ftxt_saldoCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ftxt_TotalGeneral)))
        );

        pnl_ResumenLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftxt_TotalGeneral, ftxt_saldoCaja, lbl_Total, lbl_totalCaja});

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_estado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_aviso, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pnl_Resumen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btn_Imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(btn_CerrarCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pnl_Tabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_aviso, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_estado, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Resumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Tabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_CerrarCaja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Imprimir))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.limpiarYCargarTablas();
    }//GEN-LAST:event_formWindowOpened

    private void btn_CerrarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CerrarCajaActionPerformed
        if (this.caja != null) {
            if (this.caja.getEstado() == EstadoCaja.ABIERTA) {
                try {
                    String monto = JOptionPane.showInputDialog(this,
                            "Saldo del Sistema: " + this.caja.getSaldoFinal()
                            + "\nSaldo Real:", "Cerrar Caja", JOptionPane.QUESTION_MESSAGE);
                    if (monto != null) {
                        this.caja.setSaldoReal(Double.parseDouble(monto));
                        this.caja.setFechaCierre(new Date());
                        this.caja.setUsuarioCierraCaja(usuarioService.getUsuarioActivo().getUsuario());
                        this.caja.setEstado(EstadoCaja.CERRADA);
                        this.cajaService.actualizar(caja);
                        try {
                            this.lanzarReporteCaja();
                            this.dispose();
                        } catch (JRException ex) {
                            String msjError = "Se produjo un error procesando el reporte.";
                            LOGGER.error(msjError + " - " + ex.getMessage());
                            JOptionPane.showMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (java.lang.NumberFormatException e) {
                    String msjError = "Monto inválido";
                    LOGGER.error(msjError + " - " + e.getMessage());
                    JOptionPane.showMessageDialog(this, msjError, "Error", JOptionPane.INFORMATION_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Caja Cerrada", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_CerrarCajaActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        this.limpiarYCargarTablas();
    }//GEN-LAST:event_formWindowGainedFocus

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
        GUI_AgregarGasto agregarGasto = new GUI_AgregarGasto(this, true);
        agregarGasto.setLocationRelativeTo(null);
        agregarGasto.setVisible(true);
        this.cargarElementosFormaDePago();
    }//GEN-LAST:event_btn_AgregarGastoActionPerformed

    private void cmb_FormasDePagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_FormasDePagoActionPerformed
        this.limpiarTablaBalance();
        this.cargarDatosBalance();
    }//GEN-LAST:event_cmb_FormasDePagoActionPerformed

    private void btn_ImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ImprimirActionPerformed
        try {
            this.lanzarReporteCaja();
        } catch (JRException ex) {
            String msjError = "Se produjo un error procesando el reporte.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ImprimirActionPerformed

    private void btn_EliminarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarGastoActionPerformed
        if (tbl_Balance.getSelectedRow() != -1) {
            Object movimientoDeTabla = this.listaMovimientos.get(Utilidades.getSelectedRowModelIndice(tbl_Balance));
            if (movimientoDeTabla instanceof Gasto) {
                int confirmacionEliminacion = JOptionPane.showConfirmDialog(this,
                        "¿Esta seguro que desea eliminar el gasto seleccionado?",
                        "Eliminar", JOptionPane.YES_NO_OPTION);
                if (confirmacionEliminacion == JOptionPane.YES_OPTION) {
                    Gasto gasto = (Gasto) movimientoDeTabla;
                    gasto.setEliminado(true);
                    gastoService.actualizar(gasto);
                    this.limpiarYCargarTablas();
                }
            }
        }
    }//GEN-LAST:event_btn_EliminarGastoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AgregarGasto;
    private javax.swing.JButton btn_CerrarCaja;
    private javax.swing.JButton btn_EliminarGasto;
    private javax.swing.JButton btn_Imprimir;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JComboBox<FormaDePago> cmb_FormasDePago;
    private javax.swing.JFormattedTextField ftxt_Detalle;
    private javax.swing.JFormattedTextField ftxt_TotalGeneral;
    private javax.swing.JFormattedTextField ftxt_saldoCaja;
    private javax.swing.JLabel lbl_FormaDePago;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_aviso;
    private javax.swing.JLabel lbl_estado;
    private javax.swing.JLabel lbl_total;
    private javax.swing.JLabel lbl_totalCaja;
    private javax.swing.JPanel pnl_Resumen;
    private javax.swing.JPanel pnl_Tabla;
    private javax.swing.JScrollPane sp_Tabla;
    private javax.swing.JScrollPane sp_TablaResumen;
    javax.swing.JTable tbl_Balance;
    private javax.swing.JTable tbl_Resumen;
    // End of variables declaration//GEN-END:variables
}
