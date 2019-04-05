package sic.vista.swing;

import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.FormaDePago;
import sic.service.IEmpresaService;
import sic.service.IFormaDePagoService;
import sic.service.BusinessServiceException;

public class GUI_FormasDePago extends JDialog {

    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<FormaDePago> formasDePago;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IFormaDePagoService formaDePagoService = appContext.getBean(IFormaDePagoService.class);
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);

    public GUI_FormasDePago() {
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/Wallet_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[3];
        encabezados[0] = "Predeterminado";
        encabezados[1] = "Nombre";
        encabezados[2] = "Afecta la Caja";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_FormasDePago.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Boolean.class;
        tipos[1] = String.class;
        tipos[2] = Boolean.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_FormasDePago.getTableHeader().setReorderingAllowed(false);
        tbl_FormasDePago.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_FormasDePago.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_FormasDePago.getColumnModel().getColumn(1).setPreferredWidth(400);
        tbl_FormasDePago.getColumnModel().getColumn(2).setPreferredWidth(100);
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        for (FormaDePago fdp : formasDePago) {
            Object[] fila = new Object[3];
            fila[0] = fdp.isPredeterminado();
            fila[1] = fdp.getNombre();
            fila[2] = fdp.isAfectaCaja();
            modeloTablaResultados.addRow(fila);
        }
        tbl_FormasDePago.setModel(modeloTablaResultados);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_FormasDePago.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void getFormasDePagos() {
        formasDePago = formaDePagoService.getFormasDePago(empresaService.getEmpresaActiva().getEmpresa());
    }

    private void agregarFormaDePago() {
        try {
            FormaDePago formaDePago = new FormaDePago();
            formaDePago.setNombre(txt_Nombre.getText().trim());
            formaDePago.setAfectaCaja(chk_AfectaCaja.isSelected());
            formaDePago.setEmpresa(empresaService.getEmpresaActiva().getEmpresa());
            formaDePagoService.guardar(formaDePago);
            txt_Nombre.setText("");
            chk_AfectaCaja.setSelected(false);
            this.getFormasDePagos();
            this.cargarResultadosAlTable();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        } 
    }

    private void eliminarFormaDePago() {
        if (tbl_FormasDePago.getSelectedRow() != -1) {
            int filaSeleccionada = tbl_FormasDePago.getSelectedRow();
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la forma de pago seleccionada?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    formaDePagoService.eliminar(formasDePago.get(filaSeleccionada));
                    formasDePago.remove(filaSeleccionada);
                    this.getFormasDePagos();
                    this.cargarResultadosAlTable();

                } catch (BusinessServiceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void setPredeterminado() {
        if (tbl_FormasDePago.getSelectedRow() != -1) {
            int filaSeleccionada = tbl_FormasDePago.getSelectedRow();

            try {
                if (formaDePagoService.getFormasDePagoPorId(formasDePago.get(filaSeleccionada).getId_FormaDePago()) != null) {
                    formaDePagoService.setFormaDePagoPredeterminada(formasDePago.get(filaSeleccionada));
                    this.getFormasDePagos();
                    this.cargarResultadosAlTable();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontro la Forma de Pago"
                            + " seleccionada, puede que haya sido eliminado desde otro puesto"
                            + " de trabajo SIC.\nActualize la lista y vuelva a intentarlo.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (BusinessServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verificarExistenciaPredeterminado() {
        String mensaje = "No existe ninguna Forma de Pago establecida como Predeterminada,\ndebe "
                + "establecer una para poder utilizar el T.P.V. (Terminal Punto de Venta).";
        if (!formaDePagoService.getFormasDePago(empresaService.getEmpresaActiva().getEmpresa()).isEmpty()) {
            if (formaDePagoService.getFormaDePagoPredeterminada(empresaService.getEmpresaActiva().getEmpresa()) == null) {
                JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        lbl_AfectaCaja = new javax.swing.JLabel();
        chk_AfectaCaja = new javax.swing.JCheckBox();
        sp_FormasDePago = new javax.swing.JScrollPane();
        tbl_FormasDePago = new javax.swing.JTable();
        btn_Agregar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btn_SetPredeterminado = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Formas de Pago");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Nombre.setText("Nombre:");

        lbl_AfectaCaja.setText("Afecta la Caja:");

        chk_AfectaCaja.setText(" (se utilizará para el conteo del cierre de Caja)");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_Nombre)
                    .addComponent(lbl_AfectaCaja))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .addComponent(chk_AfectaCaja))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_AfectaCaja)
                    .addComponent(chk_AfectaCaja)))
        );

        tbl_FormasDePago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        sp_FormasDePago.setViewportView(tbl_FormasDePago);

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddWallet_16x16.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteWallet_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        btn_SetPredeterminado.setForeground(java.awt.Color.blue);
        btn_SetPredeterminado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/WalletArrow_16x16.png"))); // NOI18N
        btn_SetPredeterminado.setText("Establecer como Predeterminado");
        btn_SetPredeterminado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SetPredeterminadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sp_FormasDePago, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Agregar)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Eliminar)
                        .addGap(0, 0, 0)
                        .addComponent(btn_SetPredeterminado)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Agregar, btn_Eliminar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_FormasDePago, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Agregar)
                    .addComponent(btn_Eliminar)
                    .addComponent(btn_SetPredeterminado))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Agregar, btn_Eliminar, btn_SetPredeterminado});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        this.agregarFormaDePago();
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        this.eliminarFormaDePago();
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_SetPredeterminadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SetPredeterminadoActionPerformed
        this.setPredeterminado();
    }//GEN-LAST:event_btn_SetPredeterminadoActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            this.setColumnas();
            this.getFormasDePagos();
            this.cargarResultadosAlTable();
            this.verificarExistenciaPredeterminado();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_SetPredeterminado;
    private javax.swing.JCheckBox chk_AfectaCaja;
    private javax.swing.JLabel lbl_AfectaCaja;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JPanel panel1;
    private javax.swing.JScrollPane sp_FormasDePago;
    private javax.swing.JTable tbl_FormasDePago;
    private javax.swing.JTextField txt_Nombre;
    // End of variables declaration//GEN-END:variables
}
