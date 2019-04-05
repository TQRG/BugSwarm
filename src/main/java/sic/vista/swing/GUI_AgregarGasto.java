package sic.vista.swing;

import java.util.Date;
import javax.swing.JDialog;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.Usuario;
import sic.service.IFormaDePagoService;
import sic.service.IGastoService;
import sic.service.IUsuarioService;

public class GUI_AgregarGasto extends JDialog {

    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IFormaDePagoService formaDePagoService = appContext.getBean(IFormaDePagoService.class);    
    private final IGastoService gastoService = appContext.getBean(IGastoService.class);
    private final IUsuarioService usuarioService = appContext.getBean(IUsuarioService.class);

    public GUI_AgregarGasto(javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public Gasto construirGasto(String concepto, double monto, FormaDePago formaDePago) {
        Empresa empresa = EmpresaActiva.getInstance().getEmpresa();
        Usuario usuario = usuarioService.getUsuarioActivo().getUsuario();
        int nroDeGasto = gastoService.getUltimoNumeroDeGasto(empresa.getId_Empresa()) + 1;
        Gasto gasto = new Gasto();
        gasto.setConcepto(concepto);
        gasto.setEliminado(false);
        gasto.setEmpresa(empresa);
        gasto.setFecha(new Date());
        gasto.setFormaDePago(formaDePago);
        gasto.setMonto(monto);
        gasto.setNroGasto(nroDeGasto);
        gasto.setUsuario(usuario);
        return gasto;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Parametros = new javax.swing.JPanel();
        lbl_FormaDePago = new javax.swing.JLabel();
        lbl_Concepto = new javax.swing.JLabel();
        lbl_Monto = new javax.swing.JLabel();
        cmb_FormaDePago = new javax.swing.JComboBox<>();
        ftxt_Concepto = new javax.swing.JTextField();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        pnl_Botones = new javax.swing.JPanel();
        lbl_Aceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Gasto");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lbl_FormaDePago.setText("Forma De Pago:");

        lbl_Concepto.setText("Concepto:");

        lbl_Monto.setText("Monto:");

        ftxt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,###,##0.00"))));
        ftxt_Monto.setText("0");

        javax.swing.GroupLayout pnl_ParametrosLayout = new javax.swing.GroupLayout(pnl_Parametros);
        pnl_Parametros.setLayout(pnl_ParametrosLayout);
        pnl_ParametrosLayout.setHorizontalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lbl_FormaDePago)
                        .addGap(18, 18, 18)
                        .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lbl_Monto)
                        .addGap(18, 18, 18)
                        .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lbl_Concepto)
                        .addGap(18, 18, 18)
                        .addComponent(ftxt_Concepto, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_Concepto, lbl_FormaDePago, lbl_Monto});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmb_FormaDePago, ftxt_Concepto, ftxt_Monto});

        pnl_ParametrosLayout.setVerticalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_FormaDePago)
                    .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Concepto)
                    .addComponent(ftxt_Concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Monto)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_Aceptar)
                .addContainerGap())
        );
        pnl_BotonesLayout.setVerticalGroup(
            pnl_BotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_Aceptar)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(pnl_Botones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        for (FormaDePago formaDePago : formaDePagoService.getFormasDePago(EmpresaActiva.getInstance().getEmpresa())) {
            cmb_FormaDePago.addItem(formaDePago);
        }
    }//GEN-LAST:event_formWindowOpened

    private void lbl_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbl_AceptarActionPerformed
        if (ftxt_Monto.getValue() == null) {
            ftxt_Monto.setText("0");
        }
        if (ftxt_Concepto.getText() == null) {
            ftxt_Concepto.setText("");
        }
        gastoService.guardar(this.construirGasto(ftxt_Concepto.getText(), Double.parseDouble(ftxt_Monto.getValue().toString()), (FormaDePago) cmb_FormaDePago.getSelectedItem()));
        this.dispose();
    }//GEN-LAST:event_lbl_AceptarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<FormaDePago> cmb_FormaDePago;
    private javax.swing.JTextField ftxt_Concepto;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JButton lbl_Aceptar;
    private javax.swing.JLabel lbl_Concepto;
    private javax.swing.JLabel lbl_FormaDePago;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JPanel pnl_Botones;
    private javax.swing.JPanel pnl_Parametros;
    // End of variables declaration//GEN-END:variables
}
