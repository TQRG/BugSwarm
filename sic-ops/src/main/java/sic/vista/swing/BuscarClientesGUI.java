package sic.vista.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.util.Utilidades;

public class BuscarClientesGUI extends JDialog {

    private final PuntoDeVentaGUI gui_PrincipalTPV;
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<Cliente> clientes;
    private Cliente clienteSeleccionado;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public BuscarClientesGUI(JDialog parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.setIcon();
        gui_PrincipalTPV = (PuntoDeVentaGUI) parent;
        this.setSize(gui_PrincipalTPV.getWidth() - 100, gui_PrincipalTPV.getHeight() - 200);
        this.setLocationRelativeTo(parent);
        this.setColumnas();
        //listeners        
        txt_TextoBusqueda.addKeyListener(keyHandler);
        tbl_Resultado.addKeyListener(keyHandler);
        //timer
        Timer timer = new Timer(false);
        txt_TextoBusqueda.addKeyListener(new KeyAdapter() {
            private TimerTask task;
            @Override
            public void keyTyped(KeyEvent e) {
                if (task != null) {
                    task.cancel();
                }
                task = new TimerTask() {
                    @Override
                    public void run() {
                        buscar();
                    }
                };
                timer.schedule(task, 450);
            }
        });   
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(BuscarClientesGUI.class.getResource("/sic/icons/Client_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
    }

    private void buscar() {
        try {
            clientes = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/clientes/busqueda/criteria?"
                            + "&razonSocial=" + txt_TextoBusqueda.getText().trim()
                            + "&nombreFantasia=" + txt_TextoBusqueda.getText().trim()
                            + "&idFiscal=" + txt_TextoBusqueda.getText().trim()
                            + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            Cliente[].class)));
            this.cargarResultadosAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[13];
        encabezados[0] = "ID Fiscal";
        encabezados[1] = "Razon Social";
        encabezados[2] = "Nombre Fantasia";
        encabezados[3] = "Direccion";
        encabezados[4] = "Condicion IVA";
        encabezados[5] = "Tel. Primario";
        encabezados[6] = "Tel. Secundario";
        encabezados[7] = "Contacto";
        encabezados[8] = "Email";
        encabezados[9] = "Fecha Alta";
        encabezados[10] = "Localidad";
        encabezados[11] = "Provincia";
        encabezados[12] = "Pais";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultado.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = String.class;
        tipos[6] = String.class;
        tipos[7] = String.class;
        tipos[8] = String.class;
        tipos[9] = Date.class;
        tipos[10] = String.class;
        tipos[11] = String.class;
        tipos[12] = String.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultado.getTableHeader().setReorderingAllowed(false);
        tbl_Resultado.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Resultado.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbl_Resultado.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbl_Resultado.getColumnModel().getColumn(2).setPreferredWidth(250);
        tbl_Resultado.getColumnModel().getColumn(3).setPreferredWidth(250);
        tbl_Resultado.getColumnModel().getColumn(4).setPreferredWidth(250);
        tbl_Resultado.getColumnModel().getColumn(5).setPreferredWidth(180);
        tbl_Resultado.getColumnModel().getColumn(6).setPreferredWidth(180);
        tbl_Resultado.getColumnModel().getColumn(7).setPreferredWidth(200);
        tbl_Resultado.getColumnModel().getColumn(8).setPreferredWidth(250);
        tbl_Resultado.getColumnModel().getColumn(9).setPreferredWidth(100);
        tbl_Resultado.getColumnModel().getColumn(10).setPreferredWidth(200);
        tbl_Resultado.getColumnModel().getColumn(11).setPreferredWidth(200);
        tbl_Resultado.getColumnModel().getColumn(12).setPreferredWidth(200);
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        clientes.stream().map((cliente) -> {
            Object[] fila = new Object[13];
            fila[0] = cliente.getIdFiscal();
            fila[1] = cliente.getRazonSocial();
            fila[2] = cliente.getNombreFantasia();
            fila[3] = cliente.getDireccion();
            fila[4] = cliente.getCondicionIVA();
            fila[5] = cliente.getTelPrimario();
            fila[6] = cliente.getTelSecundario();
            fila[7] = cliente.getContacto();
            fila[8] = cliente.getEmail();
            fila[9] = cliente.getFechaAlta();
            fila[10] = cliente.getLocalidad();
            fila[11] = cliente.getLocalidad().getProvincia();
            fila[12] = cliente.getLocalidad().getProvincia().getPais();
            return fila;
        }).forEachOrdered((fila) -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultado.setModel(modeloTablaResultados);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultado.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void seleccionarCliente() {
        if (tbl_Resultado.getSelectedRow() != -1) {
            int filaSeleccionada = tbl_Resultado.getSelectedRow();
            clienteSeleccionado = clientes.get(filaSeleccionada);
        } else {
            clienteSeleccionado = null;
        }
        this.dispose();
    }

    /**
     * Clase interna para manejar las hotkeys
     */
    class HotKeysHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFondo = new javax.swing.JPanel();
        txt_TextoBusqueda = new javax.swing.JTextField();
        btn_Buscar = new javax.swing.JButton();
        btn_Aceptar = new javax.swing.JButton();
        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultado = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        txt_TextoBusqueda.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_TextoBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_TextoBusquedaKeyTyped(evt);
            }
        });

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_LupaBuscar.png"))); // NOI18N
        btn_Buscar.setFocusable(false);
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        btn_Aceptar.setForeground(java.awt.Color.blue);
        btn_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btn_Aceptar.setFocusable(false);
        btn_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AceptarActionPerformed(evt);
            }
        });

        tbl_Resultado.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_Resultado.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultado.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resultado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbl_ResultadoFocusGained(evt);
            }
        });
        tbl_Resultado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResultadoKeyPressed(evt);
            }
        });
        sp_Resultado.setViewportView(tbl_Resultado);

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sp_Resultado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(txt_TextoBusqueda)
                        .addGap(55, 55, 55))
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_Buscar)
                            .addComponent(btn_Aceptar))))
                .addContainerGap())
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_TextoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultado, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Aceptar)
                .addContainerGap())
        );

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Buscar, txt_TextoBusqueda});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AceptarActionPerformed
        this.seleccionarCliente();
}//GEN-LAST:event_btn_AceptarActionPerformed

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.buscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void tbl_ResultadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadoKeyPressed
        if (evt.getKeyCode() == 10) {
            this.seleccionarCliente();
        }
        if (evt.getKeyCode() == 9) {
            txt_TextoBusqueda.requestFocus();
        }
    }//GEN-LAST:event_tbl_ResultadoKeyPressed

    private void txt_TextoBusquedaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_TextoBusquedaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txt_TextoBusquedaKeyTyped

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.buscar();
    }//GEN-LAST:event_formWindowOpened

    private void tbl_ResultadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadoFocusGained
        //Si no hay nada seleccionado y NO esta vacio el table, selecciona la primer fila
        if ((tbl_Resultado.getSelectedRow() == -1) && (tbl_Resultado.getRowCount() != 0)) {
            tbl_Resultado.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadoFocusGained
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Aceptar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTable tbl_Resultado;
    private javax.swing.JTextField txt_TextoBusqueda;
    // End of variables declaration//GEN-END:variables
}
