package sic.vista.swing;

import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.RenglonFactura;
import sic.service.IProductoService;
import sic.util.RenderTabla;

public class GUI_ProductosFaltantes extends JDialog {

    private ModeloTabla modeloTablaFaltantes;
    private final List<RenglonFactura> faltantes;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IProductoService productoService = appContext.getBean(IProductoService.class);

    public GUI_ProductosFaltantes(List<RenglonFactura> faltantes) {
        this.initComponents();
        this.faltantes = faltantes;
        modeloTablaFaltantes = new ModeloTabla();
        this.setColumnas();
        this.cargarResultadosAlTable();
        ImageIcon iconoVentana = new ImageIcon(GUI_PuntoDeVenta.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[4];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripcion";
        encabezados[2] = "Cantidad Requerida";
        encabezados[3] = "Cantidad Disponible";
        modeloTablaFaltantes.setColumnIdentifiers(encabezados);
        tbl_Faltantes.setModel(modeloTablaFaltantes);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaFaltantes.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        modeloTablaFaltantes.setClaseColumnas(tipos);
        tbl_Faltantes.getTableHeader().setReorderingAllowed(false);
        tbl_Faltantes.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Faltantes.setDefaultRenderer(Double.class, new RenderTabla());

        //tamanios de columnas
        tbl_Faltantes.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbl_Faltantes.getColumnModel().getColumn(1).setPreferredWidth(300);
        tbl_Faltantes.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbl_Faltantes.getColumnModel().getColumn(3).setPreferredWidth(100);
    }

    private void limpiarJTables() {
        modeloTablaFaltantes = new ModeloTabla();
        tbl_Faltantes.setModel(modeloTablaFaltantes);;
        this.setColumnas();
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTables();
        for (RenglonFactura renglonProductoFaltante : faltantes) {
            Object[] fila = new Object[4];
            fila[0] = renglonProductoFaltante.getCodigoItem();
            fila[1] = renglonProductoFaltante.getDescripcionItem();
            fila[2] = renglonProductoFaltante.getCantidad();
            fila[3] = productoService.getProductoPorId(renglonProductoFaltante.getId_ProductoItem()).getCantidad();
            modeloTablaFaltantes.addRow(fila);
        }
        tbl_Faltantes.setModel(modeloTablaFaltantes);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_faltantes = new javax.swing.JLabel();
        sp_faltantes = new javax.swing.JScrollPane();
        tbl_Faltantes = new javax.swing.JTable();
        btn_volver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Productos Faltantes");
        setIconImage(null);

        lbl_faltantes.setText("Los siguientes productos no poseen stock suficiente para continuar con la operación:");

        tbl_Faltantes.setModel(new javax.swing.table.DefaultTableModel(
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
        sp_faltantes.setViewportView(tbl_Faltantes);

        btn_volver.setForeground(java.awt.Color.blue);
        btn_volver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ArrowLeft_16x16.png"))); // NOI18N
        btn_volver.setText("Volver");
        btn_volver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_volverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_faltantes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
                    .addComponent(sp_faltantes)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_volver)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_faltantes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_faltantes, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_volver)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_volverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_volverActionPerformed
        this.dispose();
    }//GEN-LAST:event_btn_volverActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_volver;
    private javax.swing.JLabel lbl_faltantes;
    private javax.swing.JScrollPane sp_faltantes;
    private javax.swing.JTable tbl_Faltantes;
    // End of variables declaration//GEN-END:variables
}
