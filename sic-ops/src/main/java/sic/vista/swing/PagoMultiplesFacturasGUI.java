package sic.vista.swing;

import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Movimiento;
import sic.util.RenderTabla;

public class PagoMultiplesFacturasGUI extends JDialog {

    private List<Factura> facturas = new ArrayList<>();
    private final Movimiento movimiento;
    private final ModeloTabla modeloTablaFacturas = new ModeloTabla();
    private double montoTotal = 0.0;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PagoMultiplesFacturasGUI(JInternalFrame parent, List<Factura> facturas, Movimiento movimiento) {                
        this.setIcon();        
        this.movimiento = movimiento;           
        dc_Fecha = new JDateChooser();
        dc_Fecha.setDate(new Date());        
        this.facturas = this.ordenarFacturasPorFechaAsc(facturas);
        this.initComponents();        
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(PagoMultiplesFacturasGUI.class.getResource("/sic/icons/Stamp_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
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
   
    private void cargarFacturas() {
        try {
            facturas.stream().forEach((factura) -> {
                Object[] fila = new Object[5];
                fila[0] = factura.getFecha();
                fila[1] = String.valueOf(factura.getTipoFactura());
                fila[2] = factura.getNumSerie() + " - " + factura.getNumFactura();
                fila[3] = factura.getTotal();        
                double saldoAPagar = RestClient.getRestTemplate()
                        .getForObject("/pagos/facturas/" + factura.getId_Factura() + "/saldo",
                        double.class);                  
                montoTotal += saldoAPagar;
                fila[4] = saldoAPagar;
                modeloTablaFacturas.addRow(fila);
            });
            ftxt_Monto.setValue(montoTotal);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFormasDePago() {
         List<FormaDePago> formasDePago = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/formas-de-pago/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                FormaDePago[].class)));
        formasDePago.stream().forEach((formaDePago) -> {
            cmb_FormaDePago.addItem(formaDePago);
        });
    }
    
    private List<Factura> ordenarFacturasPorFechaAsc(List<Factura> facturas) {
        Comparator comparador = new Comparator<Factura>() {
            @Override
            public int compare(Factura f1, Factura f2) {
                return f1.getFecha().compareTo(f2.getFecha());
            }
        };        
        facturas.sort(comparador);
        return facturas;
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
        lbl_leyenda = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnl_Parametros.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_FormaDePago.setForeground(java.awt.Color.red);
        lbl_FormaDePago.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FormaDePago.setText("* Forma de Pago:");

        lbl_Monto.setForeground(java.awt.Color.red);
        lbl_Monto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Monto.setText("* Monto:");

        ftxt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
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

        lbl_leyenda.setText("Facturas a pagar:");

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_leyenda))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_leyenda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
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
            try {
                if (ftxt_Monto.getValue() == null) {
                    ftxt_Monto.setText("0");
                }
                Calendar fechaYHora = dc_Fecha.getCalendar();
                fechaYHora.set(Calendar.HOUR_OF_DAY, (int) spinner_Hora.getValue());
                fechaYHora.set(Calendar.MINUTE, (int) spinner_Minutos.getValue());
                double montoDelPago = Double.parseDouble(ftxt_Monto.getValue().toString());
                int indice = 0;
                long[] idsFacturas = new long[facturas.size()];
                for (Factura factura : facturas) {
                    idsFacturas[indice] = factura.getId_Factura();
                    indice++;
                }
                RestClient.getRestTemplate().put("/pagos/pagar-multiples-facturas?"
                        + "idFactura=" + Arrays.toString(idsFacturas).substring(1, Arrays.toString(idsFacturas).length() - 1)
                        + "&monto=" + montoDelPago
                        + "&idFormaDePago=" + ((FormaDePago) cmb_FormaDePago.getSelectedItem()).getId_FormaDePago()
                        + "&nota=" + ftxt_Nota.getText()
                        + "&fechaHora=" + fechaYHora.getTimeInMillis(),
                        null);
                this.dispose();
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_lbl_AceptarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {            
            tbl_InformacionFacturas.setModel(modeloTablaFacturas);
            if (movimiento == Movimiento.VENTA) {
                this.setTitle("Pago Multiple - Cliente: " + ((FacturaVenta) facturas.get(0)).getCliente().getRazonSocial());
            }
            if (movimiento == Movimiento.COMPRA) {
                this.setTitle("Pago Multiple - Proveedor: " + ((FacturaCompra) facturas.get(0)).getProveedor().getRazonSocial());
            }
            this.setColumnasTabla();
            this.cargarFormasDePago();            
            this.cargarFacturas();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowOpened

    private void ftxt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxt_MontoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxt_Monto.selectAll();
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
    private javax.swing.JLabel lbl_leyenda;
    private javax.swing.JPanel pnl_Botones;
    private javax.swing.JPanel pnl_Parametros;
    private javax.swing.JSpinner spinner_Hora;
    private javax.swing.JSpinner spinner_Minutos;
    private javax.swing.JTable tbl_InformacionFacturas;
    // End of variables declaration//GEN-END:variables
  
}
