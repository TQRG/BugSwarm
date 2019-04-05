package sic.vista.swing;

import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.EmpresaActiva;

public class ConfiguracionDelSistemaGUI extends JDialog {

    private ConfiguracionDelSistema cdsModificar;  
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public ConfiguracionDelSistemaGUI() {
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(ConfiguracionDelSistemaGUI.class.getResource("/sic/icons/Gears_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarConfiguracionParaModificar() {
        chk_PreImpresas.setSelected(cdsModificar.isUsarFacturaVentaPreImpresa());
        txt_CantMaximaRenglones.setValue(cdsModificar.getCantidadMaximaDeRenglonesEnFactura());
    }

    private ConfiguracionDelSistema getConfiguracionDelSistema() {
        cdsModificar.setUsarFacturaVentaPreImpresa(chk_PreImpresas.isSelected());
        cdsModificar.setCantidadMaximaDeRenglonesEnFactura(Integer.parseInt(txt_CantMaximaRenglones.getValue().toString()));
        return cdsModificar;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGeneral = new javax.swing.JPanel();
        panelReportes = new javax.swing.JPanel();
        lbl_PreImpresas = new javax.swing.JLabel();
        chk_PreImpresas = new javax.swing.JCheckBox();
        lbl_CantMaxRenglones = new javax.swing.JLabel();
        txt_CantMaximaRenglones = new javax.swing.JFormattedTextField();
        lbl_Leyenda = new javax.swing.JLabel();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configuración del Sistema");
        setIconImage(null);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        panelReportes.setBorder(javax.swing.BorderFactory.createTitledBorder("Reportes"));

        lbl_PreImpresas.setText("Usar Facturas Pre Impresas:");

        lbl_CantMaxRenglones.setText("Cantidad máxima de renglones en la Factura:");

        txt_CantMaximaRenglones.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        javax.swing.GroupLayout panelReportesLayout = new javax.swing.GroupLayout(panelReportes);
        panelReportes.setLayout(panelReportesLayout);
        panelReportesLayout.setHorizontalGroup(
            panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_CantMaxRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_PreImpresas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chk_PreImpresas)
                    .addComponent(txt_CantMaximaRenglones, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelReportesLayout.setVerticalGroup(
            panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chk_PreImpresas)
                    .addGroup(panelReportesLayout.createSequentialGroup()
                        .addComponent(lbl_PreImpresas)
                        .addGap(18, 18, 18)
                        .addGroup(panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_CantMaxRenglones)
                            .addComponent(txt_CantMaximaRenglones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelReportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelReportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lbl_Leyenda.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        lbl_Leyenda.setText("La siguiente configuración se aplica a la empresa seleccionada:");

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Save_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelGeneral, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_Leyenda, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_Leyenda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            cdsModificar = RestClient.getRestTemplate().getForObject("/configuraciones-del-sistema/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                    ConfiguracionDelSistema.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.cargarConfiguracionParaModificar();
    }//GEN-LAST:event_formWindowOpened

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            RestClient.getRestTemplate().put("/configuraciones-del-sistema", this.getConfiguracionDelSistema());
            JOptionPane.showMessageDialog(this, "La Configuración se guardó correctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JCheckBox chk_PreImpresas;
    private javax.swing.JLabel lbl_CantMaxRenglones;
    private javax.swing.JLabel lbl_Leyenda;
    private javax.swing.JLabel lbl_PreImpresas;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelReportes;
    private javax.swing.JFormattedTextField txt_CantMaximaRenglones;
    // End of variables declaration//GEN-END:variables
}
