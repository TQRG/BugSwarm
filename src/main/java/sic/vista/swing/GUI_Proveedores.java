package sic.vista.swing;

import java.beans.PropertyVetoException;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.BusquedaProveedorCriteria;
import sic.modelo.EmpresaActiva;
import sic.modelo.Localidad;
import sic.modelo.Pais;
import sic.modelo.Proveedor;
import sic.modelo.Provincia;
import sic.service.ILocalidadService;
import sic.service.IPaisService;
import sic.service.IProveedorService;
import sic.service.IProvinciaService;
import sic.service.BusinessServiceException;
import sic.util.Utilidades;

public class GUI_Proveedores extends JInternalFrame {

    private ModeloTabla modeloTablaResultados;
    private List<Proveedor> proveedores;
    private Proveedor provSeleccionado;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IPaisService paisService = appContext.getBean(IPaisService.class);
    private final IProvinciaService provinciaService = appContext.getBean(IProvinciaService.class);
    private final ILocalidadService localidadService = appContext.getBean(ILocalidadService.class);
    private final IProveedorService proveedorService = appContext.getBean(IProveedorService.class);    
    private static final Logger LOGGER = Logger.getLogger(GUI_Proveedores.class.getPackage().getName());

    public GUI_Proveedores() {
        this.initComponents();
        this.setSize(750, 450);
        modeloTablaResultados = new ModeloTabla();
        btn_Seleccionar.setVisible(false);
    }

    public Proveedor getProvSeleccionado() {
        return provSeleccionado;
    }

    public void setProvSeleccionado(Proveedor provSeleccionado) {
        this.provSeleccionado = provSeleccionado;
    }

    private void cargarComboBoxPaises() {
        List<Pais> paises;
        cmb_Pais.removeAllItems();
        paises = paisService.getPaises();
        Pais paisTodos = new Pais();
        paisTodos.setNombre("Todos");
        cmb_Pais.addItem(paisTodos);
        for (Pais pais : paises) {
            cmb_Pais.addItem(pais);
        }
    }

    private void cargarComboBoxProvinciasDelPais(Pais paisSeleccionado) {
        List<Provincia> provincias;
        cmb_Provincia.removeAllItems();
        provincias = provinciaService.getProvinciasDelPais(paisSeleccionado);
        Provincia provinciaTodas = new Provincia();
        provinciaTodas.setNombre("Todas");
        cmb_Provincia.addItem(provinciaTodas);
        for (Provincia prov : provincias) {
            cmb_Provincia.addItem(prov);
        }
    }

    private void cargarComboBoxLocalidadesDeLaProvincia(Provincia provSeleccionada) {
        List<Localidad> Localidades;
        cmb_Localidad.removeAllItems();
        Localidades = localidadService.getLocalidadesDeLaProvincia(provSeleccionada);
        Localidad localidadTodas = new Localidad();
        localidadTodas.setNombre("Todas");
        cmb_Localidad.addItem(localidadTodas);
        for (Localidad loc : Localidades) {
            cmb_Localidad.addItem(loc);
        }
    }

    private void setColumnas() {
        //sorting
        tbl_Resultados.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[13];
        encabezados[0] = "Codigo";
        encabezados[1] = "Razon Social";
        encabezados[2] = "Direccion";
        encabezados[3] = "Condicion IVA";
        encabezados[4] = "ID Fiscal";
        encabezados[5] = "Tel. Primario";
        encabezados[6] = "Tel. Secundario";
        encabezados[7] = "Contacto";
        encabezados[8] = "Email";
        encabezados[9] = "Web";
        encabezados[10] = "Localidad";
        encabezados[11] = "Provincia";
        encabezados[12] = "Pais";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);

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
        tipos[9] = String.class;
        tipos[10] = String.class;
        tipos[11] = String.class;
        tipos[12] = String.class;
        modeloTablaResultados.setClaseColumnas(tipos);

        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(300);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(200);
    }

    private void cargarResultadosAlTable() {
        limpiarJTable();
        for (Proveedor proveedor : proveedores) {
            Object[] fila = new Object[13];
            fila[0] = proveedor.getCodigo();
            fila[1] = proveedor.getRazonSocial();
            fila[2] = proveedor.getDireccion();
            fila[3] = proveedor.getCondicionIVA().getNombre();
            fila[4] = proveedor.getId_Fiscal();
            fila[5] = proveedor.getTelPrimario();
            fila[6] = proveedor.getTelSecundario();
            fila[7] = proveedor.getContacto();
            fila[8] = proveedor.getEmail();
            fila[9] = proveedor.getWeb();
            fila[10] = proveedor.getLocalidad().getNombre();
            fila[11] = proveedor.getLocalidad().getProvincia().getNombre();
            fila[12] = proveedor.getLocalidad().getProvincia().getPais().getNombre();
            modeloTablaResultados.addRow(fila);
        }

        tbl_Resultados.setModel(modeloTablaResultados);
        String mensaje = proveedores.size() + " proveedores encontrados";
        lbl_cantResultados.setText(mensaje);
    }

    private void buscar() {
        BusquedaProveedorCriteria criteria = new BusquedaProveedorCriteria();
        criteria.setBuscaPorCodigo(chk_Codigo.isSelected());
        criteria.setCodigo(txt_Codigo.getText().trim());
        criteria.setBuscaPorRazonSocial(chk_RazonSocial.isSelected());
        criteria.setRazonSocial(txt_RazonSocial.getText().trim());
        criteria.setBuscaPorId_Fiscal(chk_Id_Fiscal.isSelected());
        criteria.setId_Fiscal(txt_Id_Fiscal.getText().trim());
        criteria.setBuscaPorLocalidad(chk_Ubicacion.isSelected());
        criteria.setLocalidad((Localidad) cmb_Localidad.getSelectedItem());
        criteria.setBuscaPorProvincia(chk_Ubicacion.isSelected());
        criteria.setProvincia((Provincia) cmb_Provincia.getSelectedItem());
        criteria.setBuscaPorPais(chk_Ubicacion.isSelected());
        criteria.setPais((Pais) cmb_Pais.getSelectedItem());
        criteria.setCantRegistros(0);
        criteria.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
        if (criteria.getPais().getNombre().equals("Todos")) {
            criteria.setBuscaPorPais(false);
        }
        if (criteria.getProvincia().getNombre().equals("Todas")) {
            criteria.setBuscaPorProvincia(false);
        }
        if (criteria.getLocalidad().getNombre().equals("Todas")) {
            criteria.setBuscaPorLocalidad(false);
        }
        try {
            proveedores = proveedorService.buscarProveedores(criteria);
            this.cargarResultadosAlTable();
        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            LOGGER.warn(ex.getMessage());
        } 
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        setColumnas();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        chk_Codigo = new javax.swing.JCheckBox();
        txt_Codigo = new javax.swing.JTextField();
        chk_Ubicacion = new javax.swing.JCheckBox();
        cmb_Provincia = new javax.swing.JComboBox();
        cmb_Pais = new javax.swing.JComboBox();
        cmb_Localidad = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        txt_RazonSocial = new javax.swing.JTextField();
        chk_RazonSocial = new javax.swing.JCheckBox();
        txt_Id_Fiscal = new javax.swing.JTextField();
        chk_Id_Fiscal = new javax.swing.JCheckBox();
        lbl_cantResultados = new javax.swing.JLabel();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btn_Seleccionar = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Proveedores");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ProviderBag_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Codigo.setText("Código:");
        chk_Codigo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_CodigoItemStateChanged(evt);
            }
        });

        txt_Codigo.setEnabled(false);

        chk_Ubicacion.setText("Ubicación:");
        chk_Ubicacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UbicacionItemStateChanged(evt);
            }
        });

        cmb_Provincia.setEnabled(false);
        cmb_Provincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProvinciaItemStateChanged(evt);
            }
        });

        cmb_Pais.setEnabled(false);
        cmb_Pais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_PaisItemStateChanged(evt);
            }
        });

        cmb_Localidad.setEnabled(false);

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        txt_RazonSocial.setEnabled(false);

        chk_RazonSocial.setText("Razón Social:");
        chk_RazonSocial.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_RazonSocialItemStateChanged(evt);
            }
        });

        txt_Id_Fiscal.setEnabled(false);

        chk_Id_Fiscal.setText("ID Fiscal:");
        chk_Id_Fiscal.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_Id_FiscalItemStateChanged(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_Id_Fiscal)
                            .addComponent(chk_RazonSocial)
                            .addComponent(chk_Codigo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txt_RazonSocial, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(txt_Codigo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_Id_Fiscal))
                        .addGap(18, 18, 18)
                        .addComponent(chk_Ubicacion))
                    .addComponent(btn_Buscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cmb_Localidad, 0, 219, Short.MAX_VALUE)
                        .addComponent(cmb_Provincia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmb_Pais, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Codigo)
                    .addComponent(txt_Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Ubicacion)
                    .addComponent(cmb_Pais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_RazonSocial)
                    .addComponent(txt_RazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_Localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Id_Fiscal)
                    .addComponent(txt_Id_Fiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_Buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditProviderBag_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteProviderBag_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Nuevo)
                .addGap(0, 0, 0)
                .addComponent(btn_Modificar)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Nuevo)
                    .addComponent(btn_Modificar)
                    .addComponent(btn_Eliminar)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo});

        btn_Seleccionar.setForeground(java.awt.Color.blue);
        btn_Seleccionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ArrowRight_16x16.png"))); // NOI18N
        btn_Seleccionar.setText("Seleccionar");
        btn_Seleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SeleccionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_Seleccionar))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(btn_Seleccionar))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chk_CodigoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_CodigoItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Codigo.isSelected() == true) {
            txt_Codigo.setEnabled(true);
            txt_Codigo.requestFocus();
        } else {
            txt_Codigo.setEnabled(false);
        }
    }//GEN-LAST:event_chk_CodigoItemStateChanged

    private void chk_UbicacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UbicacionItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Ubicacion.isSelected() == true) {
            cmb_Pais.setEnabled(true);
            cmb_Provincia.setEnabled(true);
            cmb_Localidad.setEnabled(true);
            cmb_Pais.requestFocus();
        } else {
            cmb_Pais.setEnabled(false);
            cmb_Provincia.setEnabled(false);
            cmb_Localidad.setEnabled(false);
        }
    }//GEN-LAST:event_chk_UbicacionItemStateChanged

    private void cmb_PaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_PaisItemStateChanged
        if (cmb_Pais.getItemCount() > 0) {
            try {
                if (!cmb_Pais.getSelectedItem().toString().equals("Todos")) {
                    cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());
                } else {
                    cmb_Provincia.removeAllItems();
                    Provincia provinciaTodas = new Provincia();
                    provinciaTodas.setNombre("Todas");
                    cmb_Provincia.addItem(provinciaTodas);
                    cmb_Localidad.removeAllItems();
                    Localidad localidadTodas = new Localidad();
                    localidadTodas.setNombre("Todas");
                    cmb_Localidad.addItem(localidadTodas);
                }

            } catch (BusinessServiceException ex) {
                JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_cmb_PaisItemStateChanged

    private void cmb_ProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProvinciaItemStateChanged
        if (cmb_Provincia.getItemCount() > 0) {
            try {
                if (!cmb_Provincia.getSelectedItem().toString().equals("Todas")) {
                    cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());
                } else {
                    cmb_Localidad.removeAllItems();
                    Localidad localidadTodas = new Localidad();
                    localidadTodas.setNombre("Todas");
                    cmb_Localidad.addItem(localidadTodas);
                }

            } catch (BusinessServiceException ex) {
                JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            cmb_Localidad.removeAllItems();
        }
    }//GEN-LAST:event_cmb_ProvinciaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.buscar();
        if (proveedores.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_busqueda_sin_resultados"),
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        GUI_DetalleProveedor gui_DetalleProveedor = new GUI_DetalleProveedor();
        gui_DetalleProveedor.setModal(true);
        gui_DetalleProveedor.setLocationRelativeTo(this);
        gui_DetalleProveedor.setVisible(true);

        try {
            this.cargarComboBoxPaises();

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar el proveedor: "
                    + proveedores.get(indexFilaSeleccionada) + "?", "Eliminar",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    proveedorService.eliminar(proveedores.get(indexFilaSeleccionada));
                    this.buscar();

                } catch (BusinessServiceException ex) {
                    JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_ModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            GUI_DetalleProveedor gui_DetalleProveedor = new GUI_DetalleProveedor(proveedores.get(indexFilaSeleccionada));
            gui_DetalleProveedor.setModal(true);
            gui_DetalleProveedor.setLocationRelativeTo(this);
            gui_DetalleProveedor.setVisible(true);
            this.buscar();

            try {
                this.cargarComboBoxPaises();

            } catch (BusinessServiceException ex) {
                JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void btn_SeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SeleccionarActionPerformed
        if (tbl_Resultados.getSelectedRow() == -1) {
            JOptionPane.showInternalMessageDialog(this,
                    "Selecione un Proveedor de la lista para continuar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            provSeleccionado = proveedores.get(indexFilaSeleccionada);
            this.dispose();
        }
    }//GEN-LAST:event_btn_SeleccionarActionPerformed

    private void chk_RazonSocialItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_RazonSocialItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_RazonSocial.isSelected() == true) {
            txt_RazonSocial.setEnabled(true);
            txt_RazonSocial.requestFocus();
        } else {
            txt_RazonSocial.setEnabled(false);
        }
    }//GEN-LAST:event_chk_RazonSocialItemStateChanged

    private void chk_Id_FiscalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_Id_FiscalItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Id_Fiscal.isSelected() == true) {
            txt_Id_Fiscal.setEnabled(true);
            txt_Id_Fiscal.requestFocus();
        } else {
            txt_Id_Fiscal.setEnabled(false);
        }
    }//GEN-LAST:event_chk_Id_FiscalItemStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.cargarComboBoxPaises();
            this.setColumnas();
            this.setMaximum(true);

        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JButton btn_Seleccionar;
    private javax.swing.JCheckBox chk_Codigo;
    private javax.swing.JCheckBox chk_Id_Fiscal;
    private javax.swing.JCheckBox chk_RazonSocial;
    private javax.swing.JCheckBox chk_Ubicacion;
    private javax.swing.JComboBox cmb_Localidad;
    private javax.swing.JComboBox cmb_Pais;
    private javax.swing.JComboBox cmb_Provincia;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txt_Codigo;
    private javax.swing.JTextField txt_Id_Fiscal;
    private javax.swing.JTextField txt_RazonSocial;
    // End of variables declaration//GEN-END:variables
}
