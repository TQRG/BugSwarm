package sic.vista.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Rol;
import sic.modelo.Usuario;
import sic.modelo.TipoDeOperacion;

public class DetalleUsuarioGUI extends JDialog {
    
    private Usuario usuarioModificar;
    private final TipoDeOperacion operacion;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleUsuarioGUI() {
        this.initComponents();
        operacion = TipoDeOperacion.ALTA;
        this.setIcon();
        this.setTitle("Nuevo Usuario");
    }

    public DetalleUsuarioGUI(Usuario usuario) {
        this.initComponents();
        this.usuarioModificar = usuario;
        operacion = TipoDeOperacion.ACTUALIZACION;
        this.setIcon();
        this.setTitle("Modificar Usuario");
    }    

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleUsuarioGUI.class.getResource("/sic/icons/Group_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void cargarUsuarioParaModificar() {
        txt_Usuario.setText(usuarioModificar.getNombre());        
        chk_Administrador.setSelected(usuarioModificar.isPermisosAdministrador());
        List<Rol> roles = usuarioModificar.getRol();
        for (Rol rol : roles) {
            if (Rol.ADMINISTRADOR.equals(rol)) {
                chk_Administrador.setSelected(true);
            }
            if (Rol.VENDEDOR.equals(rol)) {
                chk_Vendedor.setSelected(true);
            }
            if (Rol.VIAJANTE.equals(rol)) {
                chk_Viajante.setSelected(true);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Guardar = new javax.swing.JButton();
        panelPrincipal = new javax.swing.JPanel();
        lbl_Usuario = new javax.swing.JLabel();
        txt_Usuario = new javax.swing.JTextField();
        lbl_Contrasenia = new javax.swing.JLabel();
        txt_Contrasenia = new javax.swing.JPasswordField();
        lbl_RepetirContrasenia = new javax.swing.JLabel();
        txt_RepetirContrasenia = new javax.swing.JPasswordField();
        lbl_Administrador = new javax.swing.JLabel();
        chk_Administrador = new javax.swing.JCheckBox();
        lbl_Viajante = new javax.swing.JLabel();
        chk_Viajante = new javax.swing.JCheckBox();
        lbl_Vendedor = new javax.swing.JLabel();
        chk_Vendedor = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Usuario.setForeground(java.awt.Color.red);
        lbl_Usuario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Usuario.setText("* Usuario:");

        lbl_Contrasenia.setForeground(java.awt.Color.red);
        lbl_Contrasenia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Contrasenia.setText("* Contraseña:");

        txt_Contrasenia.setPreferredSize(new java.awt.Dimension(125, 20));

        lbl_RepetirContrasenia.setForeground(java.awt.Color.red);
        lbl_RepetirContrasenia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_RepetirContrasenia.setText("* Repetir:");

        txt_RepetirContrasenia.setPreferredSize(new java.awt.Dimension(125, 20));

        lbl_Administrador.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Administrador.setText("Administrador:");

        chk_Administrador.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chk_Administrador.setMargin(new java.awt.Insets(2, -2, 2, 2));

        lbl_Viajante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Viajante.setText("Viajante:");

        chk_Viajante.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chk_Viajante.setMargin(new java.awt.Insets(2, -2, 2, 2));

        lbl_Vendedor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Vendedor.setText("Vendedor:");

        chk_Vendedor.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chk_Vendedor.setMargin(new java.awt.Insets(2, -2, 2, 2));

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_Viajante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_Vendedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lbl_Administrador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbl_RepetirContrasenia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbl_Contrasenia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbl_Usuario, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chk_Viajante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_RepetirContrasenia, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                            .addComponent(txt_Contrasenia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_Usuario)
                            .addComponent(chk_Administrador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Vendedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Usuario)
                    .addComponent(txt_Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Contrasenia)
                    .addComponent(txt_Contrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_RepetirContrasenia)
                    .addComponent(txt_RepetirContrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Administrador)
                    .addComponent(lbl_Administrador))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Viajante)
                    .addComponent(lbl_Viajante))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Vendedor)
                    .addComponent(lbl_Vendedor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chk_Administrador, txt_Contrasenia, txt_RepetirContrasenia, txt_Usuario});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            if (operacion == TipoDeOperacion.ALTA) {
                if (new String(txt_Contrasenia.getPassword()).equals(new String(txt_RepetirContrasenia.getPassword()))) {
                    Usuario usuario = new Usuario();
                    usuario.setNombre(txt_Usuario.getText().trim());
                    usuario.setPassword(new String(txt_Contrasenia.getPassword()));
                    usuario.setPermisosAdministrador(chk_Administrador.isSelected());
                    List<Rol> roles = new ArrayList<>();
                    if (chk_Administrador.isSelected()) {
                        roles.add(Rol.ADMINISTRADOR);
                    }
                    if (chk_Vendedor.isSelected()) {
                        roles.add(Rol.VENDEDOR);
                    }
                    if (chk_Viajante.isSelected()) {
                        roles.add(Rol.VIAJANTE);
                    }
                    usuario.setRol(roles);
                    RestClient.getRestTemplate().postForObject("/usuarios", usuario, Usuario.class);                 
                    LOGGER.warn("El usuario " + usuario.getNombre() + " se creo correctamente.");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Las contraseñas introducidas deben ser las mismas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                if (new String(txt_Contrasenia.getPassword()).equals(new String(txt_RepetirContrasenia.getPassword()))) {
                    Usuario usuarioModificado = new Usuario();
                    usuarioModificado.setId_Usuario(usuarioModificar.getId_Usuario());
                    usuarioModificado.setNombre(txt_Usuario.getText().trim());
                    usuarioModificado.setPassword(new String(txt_Contrasenia.getPassword()));
                    usuarioModificado.setPermisosAdministrador(chk_Administrador.isSelected());
                    List<Rol> roles = new ArrayList<>();
                    if (chk_Administrador.isSelected()) {
                        roles.add(Rol.ADMINISTRADOR);
                    }
                    if (chk_Vendedor.isSelected()) {
                        roles.add(Rol.VENDEDOR);
                    }
                    if (chk_Viajante.isSelected()) {
                        roles.add(Rol.VIAJANTE);
                    }
                    usuarioModificado.setRol(roles);
                    RestClient.getRestTemplate().put("/usuarios", usuarioModificado);
                    LOGGER.warn("El usuario " + usuarioModificado.getNombre() + " se modifico correctamente.");
                    this.dispose();                    
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Las contraseñas introducidas deben ser las mismas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
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
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.cargarUsuarioParaModificar();
        }
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JCheckBox chk_Administrador;
    private javax.swing.JCheckBox chk_Vendedor;
    private javax.swing.JCheckBox chk_Viajante;
    private javax.swing.JLabel lbl_Administrador;
    private javax.swing.JLabel lbl_Contrasenia;
    private javax.swing.JLabel lbl_RepetirContrasenia;
    private javax.swing.JLabel lbl_Usuario;
    private javax.swing.JLabel lbl_Vendedor;
    private javax.swing.JLabel lbl_Viajante;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPasswordField txt_Contrasenia;
    private javax.swing.JPasswordField txt_RepetirContrasenia;
    private javax.swing.JTextField txt_Usuario;
    // End of variables declaration//GEN-END:variables
}
