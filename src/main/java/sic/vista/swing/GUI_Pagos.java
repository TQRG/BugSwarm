package sic.vista.swing;

import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.Factura;
import sic.modelo.Pago;
import sic.service.IPagoService;
import sic.util.FormatoFechasEnTablasRenderer;
import sic.util.FormatterFechaHora;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class GUI_Pagos extends JDialog {

    private ModeloTabla modeloTablaResultados;
    private List<Pago> pagos;
    private final Factura facturaRelacionada;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IPagoService pagoService = appContext.getBean(IPagoService.class);    
    private static final Logger LOGGER = Logger.getLogger(GUI_Pagos.class.getPackage().getName());

    public GUI_Pagos(Factura factura) {
        this.initComponents();
        this.setIcon();
        modeloTablaResultados = new ModeloTabla();        
        txt_TotalAdeudado.setValue(0.00);
        txt_TotalPagado.setValue(0.00);
        txt_SaldoAPagar.setValue(0.00);
        facturaRelacionada = factura;
        FormatterFechaHora formateador = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHA_HISPANO);
        String tituloVentana;
        if (factura.getNumSerie() == 0 && factura.getNumFactura() == 0) {
            tituloVentana = "Pagos de la Factura Nº: (sin numero) con Fecha: " + formateador.format(factura.getFecha());
        } else {
            tituloVentana = "Pagos de la Factura Nº: " + factura.getNumSerie() + " - " + factura.getNumFactura()
                    + " con Fecha: " + formateador.format(factura.getFecha());
        }
        this.setTitle(tituloVentana);
        this.setColumnas();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/Stamp_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void getPagosDeLaFactura() {
        pagos = pagoService.getPagosDeLaFactura(facturaRelacionada);
    }

    private void setColumnas() {
        //sorting
        tbl_Resultados.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[4];
        encabezados[0] = "Fecha";        
        encabezados[1] = "Forma de Pago";
        encabezados[2] = "Monto";
        encabezados[3] = "Nota";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Date.class;        
        tipos[1] = String.class;
        tipos[2] = Double.class;
        tipos[3] = String.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(Double.class, new RenderTabla());

        //size de columnas        
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(140);             
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(90); 
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(385); 
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        for (Pago pago : pagos) {
            Object[] fila = new Object[4];
            fila[0] = pago.getFecha();            
            fila[1] = pago.getFormaDePago().getNombre();
            fila[2] = pago.getMonto();
            fila[3] = pago.getNota();
            modeloTablaResultados.addRow(fila);
        }
        tbl_Resultados.getColumnModel().getColumn(0).setCellRenderer(new FormatoFechasEnTablasRenderer());
        tbl_Resultados.setModel(modeloTablaResultados);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }    

    private void eliminarPago() {
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar el pago seleccionado?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                pagoService.eliminar(pagos.get(indexFilaSeleccionada));
                LOGGER.warn("El Pago: " + pagos.get(indexFilaSeleccionada).toString() + " se eliminó correctamente.");
                pagos.remove(indexFilaSeleccionada);
                this.cargarResultadosAlTable();
                this.actualizarSaldos();
            }
        }
    }

    
    private void actualizarSaldos() {
        txt_TotalAdeudado.setValue(facturaRelacionada.getTotal());
        txt_TotalPagado.setValue(pagoService.getTotalPagado(facturaRelacionada));
        txt_SaldoAPagar.setValue(pagoService.getSaldoAPagar(facturaRelacionada));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        panelSaldos = new javax.swing.JPanel();
        lbl_TA = new javax.swing.JLabel();
        lbl_TP = new javax.swing.JLabel();
        lbl_Saldo = new javax.swing.JLabel();
        txt_TotalAdeudado = new javax.swing.JFormattedTextField();
        txt_TotalPagado = new javax.swing.JFormattedTextField();
        txt_SaldoAPagar = new javax.swing.JFormattedTextField();
        lbl_AvisoPagado = new javax.swing.JLabel();
        btn_Nuevo = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pagos");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Resultado.setViewportView(tbl_Resultados);

        panelSaldos.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_TA.setForeground(java.awt.Color.red);
        lbl_TA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TA.setText("Total Adeudado:");

        lbl_TP.setForeground(java.awt.Color.green);
        lbl_TP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TP.setText("Total Pagado:");

        lbl_Saldo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Saldo.setText("Saldo a Pagar:");

        txt_TotalAdeudado.setEditable(false);
        txt_TotalAdeudado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));
        txt_TotalAdeudado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_TotalAdeudado.setFocusable(false);

        txt_TotalPagado.setEditable(false);
        txt_TotalPagado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));
        txt_TotalPagado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_TotalPagado.setFocusable(false);

        txt_SaldoAPagar.setEditable(false);
        txt_SaldoAPagar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));
        txt_SaldoAPagar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_SaldoAPagar.setFocusable(false);

        javax.swing.GroupLayout panelSaldosLayout = new javax.swing.GroupLayout(panelSaldos);
        panelSaldos.setLayout(panelSaldosLayout);
        panelSaldosLayout.setHorizontalGroup(
            panelSaldosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaldosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_TA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_TotalAdeudado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_TP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_TotalPagado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Saldo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_SaldoAPagar)
                .addContainerGap())
        );
        panelSaldosLayout.setVerticalGroup(
            panelSaldosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaldosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaldosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TA)
                    .addComponent(txt_TotalAdeudado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Saldo)
                    .addComponent(txt_SaldoAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_TP)
                    .addComponent(txt_TotalPagado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbl_AvisoPagado.setText("NOTA: Cuando el total pagado cumpla con el valor de la factura, se marcará automaticamente como pagada.");

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddStamp_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteStamp_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSaldos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_Resultado)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_AvisoPagado)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_Nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(btn_Eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Nuevo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_AvisoPagado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultado, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Nuevo)
                    .addComponent(btn_Eliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSaldos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_Nuevo});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        this.eliminarPago();
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        GUI_DetallePago gui_DetallePago = new GUI_DetallePago(facturaRelacionada);
        gui_DetallePago.setModal(true);
        gui_DetallePago.setLocationRelativeTo(this);
        gui_DetallePago.setVisible(true);
        this.getPagosDeLaFactura();
        this.cargarResultadosAlTable();
        this.actualizarSaldos();
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.getPagosDeLaFactura();
        this.cargarResultadosAlTable();    
        this.actualizarSaldos();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JLabel lbl_AvisoPagado;
    private javax.swing.JLabel lbl_Saldo;
    private javax.swing.JLabel lbl_TA;
    private javax.swing.JLabel lbl_TP;
    private javax.swing.JPanel panelSaldos;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txt_SaldoAPagar;
    private javax.swing.JFormattedTextField txt_TotalAdeudado;
    private javax.swing.JFormattedTextField txt_TotalPagado;
    // End of variables declaration//GEN-END:variables
}