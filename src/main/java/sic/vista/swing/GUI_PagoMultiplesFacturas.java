package sic.vista.swing;

import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.IFormaDePagoService;
import sic.service.IPagoService;
import sic.service.Movimiento;
import sic.util.RenderTabla;

public class GUI_PagoMultiplesFacturas extends JDialog {

    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IFormaDePagoService formaDePagoService = appContext.getBean(IFormaDePagoService.class);
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);
    private final IPagoService pagoService = appContext.getBean(IPagoService.class);
    private final IFacturaService facturaService = appContext.getBean(IFacturaService.class);
    private final List<FacturaVenta> facturasVenta = new ArrayList<>();
    private final List<FacturaCompra> facturasCompra = new ArrayList<>();
    private final Movimiento movimiento;
    private final ModeloTabla modeloTablaFacturas;
    private double montoTotal = 0.0;

    public GUI_PagoMultiplesFacturas(JInternalFrame parent, boolean modal, List<Factura> facturas, Movimiento movimiento) {
        this.modeloTablaFacturas = new ModeloTabla();
        this.setModal(modal);
        this.movimiento = movimiento;
        this.cargarFacturas(facturaService.ordenarFacturasPorFechaAsc(facturas));
        dc_Fecha = new JDateChooser();
        dc_Fecha.setDate(new Date());
        initComponents();
        this.cargarComponentes();
    }

    private void cargarComponentes() {
        this.setColumnasTabla();
        if (movimiento == Movimiento.VENTA) {
            this.setTitle("Pago Multiple - Cliente: " + facturasVenta.get(0).getCliente().getRazonSocial());
            this.cargarFacturasEnTabla();
        }
        if (movimiento == Movimiento.COMPRA) {
            this.setTitle("Pago Multiple - Proveedor: " + facturasCompra.get(0).getProveedor().getRazonSocial());
            this.cargarFacturasEnTabla();
        }
    }

    private void setColumnasTabla() {
        tbl_InformacionFacturas.setAutoCreateRowSorter(true);

        String[] encabezados = new String[5];
        encabezados[0] = "Fecha";
        encabezados[1] = "Tipo";
        encabezados[2] = "Nº Factura";
        encabezados[3] = "Total";
        encabezados[4] = "Total Adeudado";
        modeloTablaFacturas.setColumnIdentifiers(encabezados);
        tbl_InformacionFacturas.setModel(modeloTablaFacturas);

        Class[] tipos = new Class[modeloTablaFacturas.getColumnCount()];
        tipos[0] = Date.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = Double.class;
        tipos[4] = Double.class;
        modeloTablaFacturas.setClaseColumnas(tipos);
        tbl_InformacionFacturas.getTableHeader().setReorderingAllowed(false);
        tbl_InformacionFacturas.getTableHeader().setResizingAllowed(true);

        tbl_InformacionFacturas.setDefaultRenderer(Double.class, new RenderTabla());

        tbl_InformacionFacturas.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_InformacionFacturas.getColumnModel().getColumn(1).setPreferredWidth(40);
        tbl_InformacionFacturas.getColumnModel().getColumn(2).setPreferredWidth(120);
        tbl_InformacionFacturas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbl_InformacionFacturas.getColumnModel().getColumn(4).setPreferredWidth(120);
    }

    private void cargarFacturasEnTabla() {
        this.cargarRenglones();
        tbl_InformacionFacturas.setModel(modeloTablaFacturas);
    }

    private void cargarFacturas(List<Factura> facturas) {
        if (this.movimiento == Movimiento.VENTA) {
            for (Factura factura : facturas) {
                this.facturasVenta.add((FacturaVenta) factura);
            }
        }
        if (this.movimiento == Movimiento.COMPRA) {
            for (Factura factura : facturas) {
                this.facturasCompra.add((FacturaCompra) factura);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Parametros = new javax.swing.JPanel();
        lbl_FormaDePago = new javax.swing.JLabel();
        lbl_Monto = new javax.swing.JLabel();
        cmb_FormaDePago = new javax.swing.JComboBox<>();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        lbl_Nota = new javax.swing.JLabel();
        ftxt_Nota = new javax.swing.JTextField();
        lbl_Fecha = new javax.swing.JLabel();
        dc_Fecha = new com.toedter.calendar.JDateChooser();
        spinner_Hora = new javax.swing.JSpinner();
        spinner_Minutos = new javax.swing.JSpinner();
        pnl_Botones = new javax.swing.JPanel();
        lbl_Aceptar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_InformacionFacturas = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnl_Parametros.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_FormaDePago.setForeground(java.awt.Color.red);
        lbl_FormaDePago.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FormaDePago.setText("*Forma De Pago:");

        lbl_Monto.setForeground(java.awt.Color.red);
        lbl_Monto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Monto.setText("*Monto:");

        ftxt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        ftxt_Monto.setText("0");
        ftxt_Monto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxt_MontoFocusGained(evt);
            }
        });

        lbl_Nota.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nota.setText("Nota:");

        lbl_Fecha.setForeground(java.awt.Color.red);
        lbl_Fecha.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Fecha.setText("* Fecha y Hora:");

        dc_Fecha.setDate(new Date());
        dc_Fecha.setDateFormatString("dd/MM/yyyy");

        spinner_Hora.setModel(new SpinnerNumberModel(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 00, 23, 1));

        spinner_Minutos.setModel(new SpinnerNumberModel(Calendar.getInstance().get(Calendar.MINUTE), 00, 59, 1));

        javax.swing.GroupLayout pnl_ParametrosLayout = new javax.swing.GroupLayout(pnl_Parametros);
        pnl_Parametros.setLayout(pnl_ParametrosLayout);
        pnl_ParametrosLayout.setHorizontalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lbl_Fecha)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_Fecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_Hora, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(spinner_Minutos, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lbl_Nota)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_Nota, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Monto)
                            .addComponent(lbl_FormaDePago))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_Fecha, lbl_FormaDePago, lbl_Monto, lbl_Nota});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmb_FormaDePago, ftxt_Monto, ftxt_Nota});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {spinner_Hora, spinner_Minutos});

        pnl_ParametrosLayout.setVerticalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dc_Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Fecha)
                    .addComponent(spinner_Hora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinner_Minutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_FormaDePago)
                    .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Monto)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Nota)
                    .addComponent(ftxt_Nota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_FormaDePago, lbl_Monto, lbl_Nota});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmb_FormaDePago, ftxt_Monto, ftxt_Nota});

        lbl_Aceptar.setForeground(java.awt.Color.blue);
        lbl_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        lbl_Aceptar.setText("Aceptar");
        lbl_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lbl_AceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_BotonesLayout = new javax.swing.GroupLayout(pnl_Botones);
        pnl_Botones.setLayout(pnl_BotonesLayout);
        pnl_BotonesLayout.setHorizontalGroup(
            pnl_BotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_BotonesLayout.createSequentialGroup()
                .addGap(0, 304, Short.MAX_VALUE)
                .addComponent(lbl_Aceptar))
        );
        pnl_BotonesLayout.setVerticalGroup(
            pnl_BotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_Aceptar)
        );

        tbl_InformacionFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbl_InformacionFacturas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(pnl_Botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 110, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbl_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbl_AceptarActionPerformed
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Esta seguro que desea realizar esta operacion?",
                "Pago", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            if (ftxt_Monto.getValue() == null) {
                ftxt_Monto.setText("0");
            }
            Calendar fechaYHora = dc_Fecha.getCalendar();
            fechaYHora.set(Calendar.HOUR_OF_DAY, (int) spinner_Hora.getValue());
            fechaYHora.set(Calendar.MINUTE, (int) spinner_Minutos.getValue());
            double monto = Double.parseDouble(ftxt_Monto.getValue().toString());
            if (movimiento == Movimiento.VENTA) {
                if (monto <= pagoService.calcularTotalAdeudadoFacturasVenta(facturasVenta)) {
                    pagoService.pagarMultiplesFacturasVenta(facturasVenta, monto,
                            (FormaDePago) cmb_FormaDePago.getSelectedItem(), ftxt_Nota.getText(), fechaYHora.getTime());
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle(
                            "Mensajes").getString("mensaje_pago_mayorADeuda_monto"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            if (movimiento == Movimiento.COMPRA) {
                if (monto <= pagoService.calcularTotalAdeudadoFacturasCompra(facturasCompra)) {
                    pagoService.pagarMultiplesFacturasCompra(facturasCompra, monto,
                            (FormaDePago) cmb_FormaDePago.getSelectedItem(), ftxt_Nota.getText(), fechaYHora.getTime());
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle(
                            "Mensajes").getString("mensaje_pago_mayorADeuda_monto"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_lbl_AceptarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        for (FormaDePago formaDePago : formaDePagoService.getFormasDePago(empresaService.getEmpresaActiva().getEmpresa())) {
            cmb_FormaDePago.addItem(formaDePago);
        }
    }//GEN-LAST:event_formWindowOpened

    private void ftxt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxt_MontoFocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ftxt_Monto.selectAll();
            }
        });
    }//GEN-LAST:event_ftxt_MontoFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<FormaDePago> cmb_FormaDePago;
    private com.toedter.calendar.JDateChooser dc_Fecha;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JTextField ftxt_Nota;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton lbl_Aceptar;
    private javax.swing.JLabel lbl_Fecha;
    private javax.swing.JLabel lbl_FormaDePago;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JLabel lbl_Nota;
    private javax.swing.JPanel pnl_Botones;
    private javax.swing.JPanel pnl_Parametros;
    private javax.swing.JSpinner spinner_Hora;
    private javax.swing.JSpinner spinner_Minutos;
    private javax.swing.JTable tbl_InformacionFacturas;
    // End of variables declaration//GEN-END:variables

    private void cargarRenglones() {
        if (movimiento == Movimiento.VENTA) {
            for (FacturaVenta factura : facturasVenta) {
                this.cargarRenglonFactura(factura);
            }
        }
        if (movimiento == Movimiento.COMPRA) {
            for (FacturaCompra factura : facturasCompra) {
                this.cargarRenglonFactura(factura);
            }
        }
        ftxt_Monto.setValue(montoTotal);
    }

    private void cargarRenglonFactura(Factura factura) {
        double saldoAPagar = 0.0;
        Object[] fila = new Object[5];
        fila[0] = factura.getFecha();
        fila[1] = String.valueOf(factura.getTipoFactura());
        fila[2] = factura.getNumSerie() + " - " + factura.getNumFactura();
        fila[3] = factura.getTotal();
        saldoAPagar = pagoService.getSaldoAPagar(factura);
        montoTotal += saldoAPagar;
        fila[4] = saldoAPagar;
        modeloTablaFacturas.addRow(fila);
    }

}
