package sic.vista.swing;

import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.CondicionIVA;
import sic.service.ICondicionIVAService;
import sic.service.BusinessServiceException;

public class GUI_DetalleCondicionIVA extends JDialog {

    private CondicionIVA condicionIVASeleccionada;
    private final DefaultListModel modeloList = new DefaultListModel();
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final ICondicionIVAService condicionIVAService = appContext.getBean(ICondicionIVAService.class);
    private static final Logger LOGGER = Logger.getLogger(GUI_DetalleCondicionIVA.class.getPackage().getName());

    public GUI_DetalleCondicionIVA() {
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/Money_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarListCondiciones() {
        modeloList.clear();
        List<CondicionIVA> condicionesIVA = condicionIVAService.getCondicionesIVA();
        for (CondicionIVA c : condicionesIVA) {
            modeloList.addElement(c);
        }
        lst_Condiciones.setModel(modeloList);
    }

    private void limpiarYRecargarComponentes() {
        txt_Nombre.setText("");
        chk_DiscriminaIVA.setSelected(false);
        condicionIVASeleccionada = null;
        this.cargarListCondiciones();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp_ListaMedidas = new javax.swing.JScrollPane();
        lst_Condiciones = new javax.swing.JList();
        panel = new javax.swing.JPanel();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        chk_DiscriminaIVA = new javax.swing.JCheckBox();
        lbl_Discrimina_IVA = new javax.swing.JLabel();
        btn_Agregar = new javax.swing.JButton();
        btn_Actualizar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        lbl_MisCondicionesDeIVA = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrar Condiciones de IVA");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lst_Condiciones.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Condiciones.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_CondicionesValueChanged(evt);
            }
        });
        sp_ListaMedidas.setViewportView(lst_Condiciones);

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("Nombre:");

        chk_DiscriminaIVA.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lbl_Discrimina_IVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Discrimina_IVA.setText("Discrimina IVA:");

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMoney_16x16.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Actualizar.setForeground(java.awt.Color.blue);
        btn_Actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMoney_16x16.png"))); // NOI18N
        btn_Actualizar.setText("Actualizar");
        btn_Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ActualizarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteMoney_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_Discrimina_IVA, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(lbl_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_Nombre)
                            .addComponent(chk_DiscriminaIVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(btn_Agregar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Actualizar, btn_Agregar, btn_Eliminar});

        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_DiscriminaIVA)
                    .addComponent(lbl_Discrimina_IVA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Agregar)
                    .addComponent(btn_Actualizar)
                    .addComponent(btn_Eliminar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbl_MisCondicionesDeIVA.setText("Condiciones de IVA:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_MisCondicionesDeIVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sp_ListaMedidas, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_MisCondicionesDeIVA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sp_ListaMedidas, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lst_CondicionesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_CondicionesValueChanged
        if (lst_Condiciones.getModel().getSize() != 0) {
            if (lst_Condiciones.getSelectedValue() != null) {
                condicionIVASeleccionada = (CondicionIVA) lst_Condiciones.getSelectedValue();
                txt_Nombre.setText(condicionIVASeleccionada.getNombre());
                chk_DiscriminaIVA.setSelected(condicionIVASeleccionada.isDiscriminaIVA());
            }
        }
    }//GEN-LAST:event_lst_CondicionesValueChanged

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        CondicionIVA condicionIVA = new CondicionIVA();
        condicionIVA.setNombre(txt_Nombre.getText().trim());
        condicionIVA.setDiscriminaIVA(chk_DiscriminaIVA.isSelected());
        try {
            condicionIVAService.guardar(condicionIVA);
            LOGGER.warn("La condicion de IVA " + txt_Nombre.getText().trim() + " se guardó correctamente.");
            this.limpiarYRecargarComponentes();            

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ActualizarActionPerformed
        try {
            //control de seleccion
            if (condicionIVASeleccionada == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una "
                        + "Condicion de IVA de la lista para poder continuar",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                condicionIVASeleccionada.setNombre(txt_Nombre.getText().trim());
                condicionIVASeleccionada.setDiscriminaIVA(chk_DiscriminaIVA.isSelected());
                condicionIVAService.actualizar(condicionIVASeleccionada);
                LOGGER.warn("La condicion de IVA " + txt_Nombre.getText().trim() + " se actualizó correctamente.");
                this.limpiarYRecargarComponentes();                
            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ActualizarActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        try {
            //control de seleccion
            if (condicionIVASeleccionada == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una "
                        + " Condicion de IVA de la lista para poder continuar",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                //eliminar                                              
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la condicion de IVA: "
                    + condicionIVASeleccionada.getNombre() + "?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {                    
                    condicionIVAService.eliminar(condicionIVASeleccionada);
                    LOGGER.warn("La condicion de IVA " + txt_Nombre.getText().trim() + " se eliminó correctamente.");
                    this.limpiarYRecargarComponentes();                
                }
            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            this.cargarListCondiciones();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Actualizar;
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JCheckBox chk_DiscriminaIVA;
    private javax.swing.JLabel lbl_Discrimina_IVA;
    private javax.swing.JLabel lbl_MisCondicionesDeIVA;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JList lst_Condiciones;
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane sp_ListaMedidas;
    private javax.swing.JTextField txt_Nombre;
    // End of variables declaration//GEN-END:variables
}
