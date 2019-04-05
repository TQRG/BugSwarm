package sic.vista.swing;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.UsuarioActivo;
import sic.modelo.Usuario;

public class UsuariosGUI extends JDialog {

    private final DefaultListModel modeloListUsuarios = new DefaultListModel();
    private Usuario usuarioSeleccionado;
    private boolean mismoUsuarioActivo = false; 
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public UsuariosGUI() {
        this.initComponents();
        this.setIcon();        
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(UsuariosGUI.class.getResource("/sic/icons/Group_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void comprobarPrivilegiosUsuarioActivo() {
        //Comprueba si el usuario es Administrador
        if (UsuarioActivo.getInstance().getUsuario().isPermisosAdministrador() == true) {
            this.cargarUsuarios();            
        } else {
            JOptionPane.showMessageDialog(this,
                    "No tiene privilegios de Administrador para poder acceder a esta seccion.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }

    private void cargarUsuarios() {
        modeloListUsuarios.clear();
        try {
            List<Usuario> usuarios = Arrays.asList(RestClient.getRestTemplate().getForObject("/usuarios", Usuario[].class));
            usuarios.stream().forEach((u) -> {
                modeloListUsuarios.addElement(u);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        lst_Usuarios.setModel(modeloListUsuarios);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        sp_ListaMedidas = new javax.swing.JScrollPane();
        lst_Usuarios = new javax.swing.JList();
        btn_Actualizar = new javax.swing.JButton();
        btn_Agregar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        lbl_Usuarios = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrar Usuarios");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lst_Usuarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Usuarios.setVisibleRowCount(9);
        lst_Usuarios.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_UsuariosValueChanged(evt);
            }
        });
        sp_ListaMedidas.setViewportView(lst_Usuarios);

        btn_Actualizar.setForeground(java.awt.Color.blue);
        btn_Actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditGroup_16x16.png"))); // NOI18N
        btn_Actualizar.setText("Actualizar");
        btn_Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ActualizarActionPerformed(evt);
            }
        });

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddGroup_16x16.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteGroup_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(btn_Agregar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(sp_ListaMedidas))
                .addContainerGap())
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Actualizar, btn_Agregar, btn_Eliminar});

        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(sp_ListaMedidas, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Agregar)
                    .addComponent(btn_Actualizar)
                    .addComponent(btn_Eliminar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Actualizar, btn_Agregar, btn_Eliminar});

        lbl_Usuarios.setText("Usuarios:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Usuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_Usuarios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        try {
            if (usuarioSeleccionado != null) {
                //Si el usuario activo corresponde con el usuario seleccionado para modificar
                int respuesta;
                if (UsuarioActivo.getInstance().getUsuario().getNombre().equals(usuarioSeleccionado.getNombre())) {
                    respuesta = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_eliminar_usuario_propio"),
                            "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                } else {
                    respuesta = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_eliminar_usuario")
                            + usuarioSeleccionado.getNombre() + "?",
                            "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                }

                if (respuesta == JOptionPane.YES_OPTION) {
                    RestClient.getRestTemplate().delete("/usuarios/" + usuarioSeleccionado.getId_Usuario());
                    LOGGER.warn("El usuario " + usuarioSeleccionado.getNombre() + " se elimino correctamente.");
                    usuarioSeleccionado = null;
                    this.cargarUsuarios();
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
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        DetalleUsuarioGUI gui_DetalleUsuario = new DetalleUsuarioGUI();
        gui_DetalleUsuario.setModal(true);
        gui_DetalleUsuario.setLocationRelativeTo(this);
        gui_DetalleUsuario.setVisible(true);
        this.cargarUsuarios();
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ActualizarActionPerformed
        if (usuarioSeleccionado != null) {
            //Si el usuario activo corresponde con el usuario seleccionado para modificar
            int respuesta = JOptionPane.YES_OPTION;            
            if (UsuarioActivo.getInstance().getUsuario().getNombre().equals(usuarioSeleccionado.getNombre())) {
                mismoUsuarioActivo = true;
                respuesta = JOptionPane.showConfirmDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_modificar_el_usuario_propio"),
                        "Atención", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);                
            }

            if (respuesta == JOptionPane.YES_OPTION) {
                DetalleUsuarioGUI gui_DetalleUsuario = new DetalleUsuarioGUI(usuarioSeleccionado);
                gui_DetalleUsuario.setModal(true);
                gui_DetalleUsuario.setLocationRelativeTo(this);
                gui_DetalleUsuario.setVisible(true);                
                if (mismoUsuarioActivo == true) {
                    UsuarioActivo.getInstance().setUsuario(usuarioSeleccionado);
                }
                this.cargarUsuarios();
            }
        }
    }//GEN-LAST:event_btn_ActualizarActionPerformed

    private void lst_UsuariosValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_UsuariosValueChanged
        usuarioSeleccionado = (Usuario) lst_Usuarios.getSelectedValue();        
    }//GEN-LAST:event_lst_UsuariosValueChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.comprobarPrivilegiosUsuarioActivo();        
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Actualizar;
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JLabel lbl_Usuarios;
    private javax.swing.JList lst_Usuarios;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JScrollPane sp_ListaMedidas;
    // End of variables declaration//GEN-END:variables
}
