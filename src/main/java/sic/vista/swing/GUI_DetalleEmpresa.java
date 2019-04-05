package sic.vista.swing;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.CondicionIVA;
import sic.modelo.Empresa;
import sic.modelo.Localidad;
import sic.modelo.Pais;
import sic.modelo.Provincia;
import sic.service.ICondicionIVAService;
import sic.service.IEmpresaService;
import sic.service.ILocalidadService;
import sic.service.IPaisService;
import sic.service.IProvinciaService;
import sic.service.BusinessServiceException;
import sic.service.TipoDeOperacion;
import sic.util.FiltroImagenes;
import sic.util.Utilidades;
import sic.util.Validator;

public class GUI_DetalleEmpresa extends JDialog {
    
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final ICondicionIVAService condicionIVAService = appContext.getBean(ICondicionIVAService.class);
    private final IPaisService paisService = appContext.getBean(IPaisService.class);
    private final IProvinciaService provinciaService = appContext.getBean(IProvinciaService.class);
    private final ILocalidadService localidadService = appContext.getBean(ILocalidadService.class);
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);
    private byte[] logo = null;
    private Empresa empresaModificar;
    private final TipoDeOperacion operacion;
    private static final Logger LOGGER = Logger.getLogger(GUI_DetalleEmpresa.class.getPackage().getName());

    public GUI_DetalleEmpresa() {
        this.initComponents();
        this.setIcon();
        this.setTitle("Nueva Empresa");
        operacion = TipoDeOperacion.ALTA;
    }

    public GUI_DetalleEmpresa(Empresa empresa) {
        this.initComponents();
        this.setIcon();
        this.setTitle("Modificar Empresa");
        operacion = TipoDeOperacion.ACTUALIZACION;
        empresaModificar = empresa;
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/Empresa_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }   
    
    private void cargarComboBoxCondicionesIVA() {         
        cmb_CondicionIVA.removeAllItems();
        List<CondicionIVA> condicionesIVA = condicionIVAService.getCondicionesIVA();
        for (CondicionIVA c : condicionesIVA) {
            cmb_CondicionIVA.addItem(c);
        }
    }

    private void cargarComboBoxPaises() {
        cmb_Pais.removeAllItems();
        List<Pais> paises = paisService.getPaises();
        for (Pais p : paises) {
            cmb_Pais.addItem(p);
        }
    }

    private void cargarComboBoxProvinciasDelPais(Pais paisSeleccionado) {
        cmb_Provincia.removeAllItems();
        List<Provincia> provincias = provinciaService.getProvinciasDelPais(paisSeleccionado);
        for (Provincia p : provincias) {
            cmb_Provincia.addItem(p);
        }
    }

    private void cargarComboBoxLocalidadesDeLaProvincia(Provincia provSeleccionada) {
        cmb_Localidad.removeAllItems();
        List<Localidad> Localidades = localidadService.getLocalidadesDeLaProvincia(provSeleccionada);
        for (Localidad l : Localidades) {
            cmb_Localidad.addItem(l);
        }
    }
    
    private void cargarEmpresaParaModificar() {
        txt_Nombre.setText(empresaModificar.getNombre());
        txt_Lema.setText(empresaModificar.getLema());
        txt_Direccion.setText(empresaModificar.getDireccion());
        cmb_CondicionIVA.setSelectedItem(empresaModificar.getCondicionIVA());

        if (empresaModificar.getCuip() == 0) {
            txt_CUIP.setText("");
        } else {
            txt_CUIP.setText(String.valueOf(empresaModificar.getCuip()));
        }

        if (empresaModificar.getIngresosBrutos() == 0) {
            txt_IngBrutos.setText("");
        } else {
            txt_IngBrutos.setText(String.valueOf(empresaModificar.getIngresosBrutos()));
        }

        dc_FechaInicioActividad.setDate(empresaModificar.getFechaInicioActividad());
        txt_Email.setText(empresaModificar.getEmail());
        txt_Telefono.setText(empresaModificar.getTelefono());
        cmb_Pais.setSelectedItem(empresaModificar.getLocalidad().getProvincia().getPais());
        cmb_Provincia.setSelectedItem(empresaModificar.getLocalidad().getProvincia());
        cmb_Localidad.setSelectedItem(empresaModificar.getLocalidad());

        if (empresaModificar.getLogo() == null) {
            lbl_Logo.setText("SIN IMAGEN");
            logo = null;
        } else {
            lbl_Logo.setText("");
            logo = empresaModificar.getLogo();
            ImageIcon imagenLogo = new ImageIcon(logo);
            ImageIcon logoRedimensionado = new ImageIcon(imagenLogo.getImage().getScaledInstance(114, 114, Image.SCALE_SMOOTH));
            lbl_Logo.setIcon(logoRedimensionado);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        lbl_Nombre = new javax.swing.JLabel();
        lbl_Direccion = new javax.swing.JLabel();
        lbl_Lema = new javax.swing.JLabel();
        lbl_CondicionIVA = new javax.swing.JLabel();
        lbl_CUIP = new javax.swing.JLabel();
        lbl_IngBrutos = new javax.swing.JLabel();
        lbl_FIA = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        txt_Lema = new javax.swing.JTextField();
        txt_Direccion = new javax.swing.JTextField();
        txt_CUIP = new javax.swing.JTextField();
        txt_IngBrutos = new javax.swing.JTextField();
        cmb_CondicionIVA = new javax.swing.JComboBox();
        btn_NuevaCondicionIVA = new javax.swing.JButton();
        dc_FechaInicioActividad = new com.toedter.calendar.JDateChooser();
        lbl_Logo = new javax.swing.JLabel();
        btn_ExaminarArchivos = new javax.swing.JButton();
        btn_EliminarLogo = new javax.swing.JButton();
        lbl_Email = new javax.swing.JLabel();
        txt_Email = new javax.swing.JTextField();
        lbl_Telefono = new javax.swing.JLabel();
        txt_Telefono = new javax.swing.JTextField();
        lbl_Pais = new javax.swing.JLabel();
        cmb_Pais = new javax.swing.JComboBox();
        btn_NuevoPais = new javax.swing.JButton();
        lbl_Provincia = new javax.swing.JLabel();
        cmb_Provincia = new javax.swing.JComboBox();
        btn_NuevaProvincia = new javax.swing.JButton();
        lbl_Localidad = new javax.swing.JLabel();
        cmb_Localidad = new javax.swing.JComboBox();
        btn_NuevaLocalidad = new javax.swing.JButton();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Nombre.setForeground(java.awt.Color.red);
        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("* Nombre:");

        lbl_Direccion.setForeground(java.awt.Color.red);
        lbl_Direccion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Direccion.setText("* Dirección:");

        lbl_Lema.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Lema.setText("Lema:");

        lbl_CondicionIVA.setForeground(java.awt.Color.red);
        lbl_CondicionIVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CondicionIVA.setText("* Condición IVA:");

        lbl_CUIP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CUIP.setText("CUIT / CUIL / CUIP:");

        lbl_IngBrutos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_IngBrutos.setText("Ingresos Brutos:");

        lbl_FIA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FIA.setText("Fecha Inicio Actividad:");

        btn_NuevaCondicionIVA.setForeground(java.awt.Color.blue);
        btn_NuevaCondicionIVA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMoney_16x16.png"))); // NOI18N
        btn_NuevaCondicionIVA.setText("Nueva");
        btn_NuevaCondicionIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaCondicionIVAActionPerformed(evt);
            }
        });

        dc_FechaInicioActividad.setDateFormatString("dd/MM/yyyy");

        lbl_Logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Logo.setText("SIN IMAGEN");
        lbl_Logo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbl_Logo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btn_ExaminarArchivos.setForeground(java.awt.Color.blue);
        btn_ExaminarArchivos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddPicture_16x16.png"))); // NOI18N
        btn_ExaminarArchivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ExaminarArchivosActionPerformed(evt);
            }
        });

        btn_EliminarLogo.setForeground(java.awt.Color.blue);
        btn_EliminarLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/RemovePicture_16x16.png"))); // NOI18N
        btn_EliminarLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarLogoActionPerformed(evt);
            }
        });

        lbl_Email.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Email.setText("E-Mail:");

        lbl_Telefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Telefono.setText("Teléfono:");

        lbl_Pais.setForeground(java.awt.Color.red);
        lbl_Pais.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Pais.setText("* Pais:");

        cmb_Pais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_PaisItemStateChanged(evt);
            }
        });

        btn_NuevoPais.setForeground(java.awt.Color.blue);
        btn_NuevoPais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevoPais.setText("Nuevo");
        btn_NuevoPais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoPaisActionPerformed(evt);
            }
        });

        lbl_Provincia.setForeground(java.awt.Color.red);
        lbl_Provincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Provincia.setText("* Provincia:");

        cmb_Provincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProvinciaItemStateChanged(evt);
            }
        });

        btn_NuevaProvincia.setForeground(java.awt.Color.blue);
        btn_NuevaProvincia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevaProvincia.setText("Nueva");
        btn_NuevaProvincia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaProvinciaActionPerformed(evt);
            }
        });

        lbl_Localidad.setForeground(java.awt.Color.red);
        lbl_Localidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Localidad.setText("* Localidad:");

        btn_NuevaLocalidad.setForeground(java.awt.Color.blue);
        btn_NuevaLocalidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevaLocalidad.setText("Nueva");
        btn_NuevaLocalidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaLocalidadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Localidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Provincia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Pais, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Telefono, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Email, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_CUIP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_IngBrutos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_FIA, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .addComponent(lbl_CondicionIVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Direccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Lema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(lbl_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_ExaminarArchivos)
                            .addComponent(btn_EliminarLogo))
                        .addGap(0, 177, Short.MAX_VALUE))
                    .addComponent(txt_Direccion, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_Lema, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                        .addComponent(cmb_CondicionIVA, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevaCondicionIVA))
                    .addComponent(txt_CUIP)
                    .addComponent(txt_IngBrutos)
                    .addComponent(dc_FechaInicioActividad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_Email)
                    .addComponent(txt_Telefono)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(cmb_Pais, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevoPais))
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(cmb_Provincia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevaProvincia))
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(cmb_Localidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevaLocalidad)))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(btn_ExaminarArchivos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_EliminarLogo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Nombre)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Lema)
                    .addComponent(txt_Lema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Direccion)
                    .addComponent(txt_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_NuevaCondicionIVA)
                    .addComponent(cmb_CondicionIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CondicionIVA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_CUIP)
                    .addComponent(txt_CUIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_IngBrutos)
                    .addComponent(txt_IngBrutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_FIA)
                    .addComponent(dc_FechaInicioActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Email)
                    .addComponent(txt_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Telefono))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_NuevoPais)
                    .addComponent(cmb_Pais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Pais))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Provincia)
                    .addComponent(cmb_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaProvincia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Localidad)
                    .addComponent(cmb_Localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaLocalidad))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaCondicionIVA, cmb_CondicionIVA});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoPais, cmb_Pais});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaProvincia, cmb_Provincia});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaLocalidad, cmb_Localidad});

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Guardar)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            //TO DO - Esta validacion debería ser hecha por un componente swing
            String cuip_ingresado = txt_CUIP.getText().trim();
            if (cuip_ingresado.equals("")) {
                cuip_ingresado = "0";
            }
            if (!Validator.esNumericoPositivo(cuip_ingresado)) {
                throw new BusinessServiceException("El CUIT/CUIL/CUIP ingresado es inválido.");
            }

            String ingBrutos_ingresado = txt_IngBrutos.getText().trim();
            if (ingBrutos_ingresado.equals("")) {
                ingBrutos_ingresado = "0";
            }
            if (!Validator.esNumericoPositivo(ingBrutos_ingresado)) {
                throw new BusinessServiceException("Ingresos Brutos ingresado es inválido.");
            }

            String mensaje = "";
            if (operacion == TipoDeOperacion.ALTA) {
                Empresa empresa = Empresa.builder()
                    .nombre(txt_Nombre.getText().trim())
                    .lema(txt_Lema.getText().trim())
                    .direccion(txt_Direccion.getText().trim())
                    .condicionIVA((CondicionIVA) cmb_CondicionIVA.getSelectedItem())
                    .cuip(Long.parseLong(cuip_ingresado))
                    .ingresosBrutos(Long.parseLong(ingBrutos_ingresado))
                    .fechaInicioActividad(dc_FechaInicioActividad.getDate())
                    .email(txt_Email.getText().trim())
                    .telefono(txt_Telefono.getText().trim())
                    .localidad((Localidad) cmb_Localidad.getSelectedItem())
                    .logo(logo)
                    .build();
                empresaService.guardar(empresa);
                mensaje = "La Empresa " + txt_Nombre.getText().trim() + " se guardó correctamente.";                
            }

            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                empresaModificar.setNombre(txt_Nombre.getText().trim());
                empresaModificar.setLema(txt_Lema.getText().trim());
                empresaModificar.setDireccion(txt_Direccion.getText().trim());
                empresaModificar.setCondicionIVA((CondicionIVA) cmb_CondicionIVA.getSelectedItem());
                empresaModificar.setCuip(Long.parseLong(cuip_ingresado));
                empresaModificar.setIngresosBrutos(Long.parseLong(ingBrutos_ingresado));
                empresaModificar.setFechaInicioActividad(dc_FechaInicioActividad.getDate());
                empresaModificar.setEmail(txt_Email.getText().trim());
                empresaModificar.setTelefono(txt_Telefono.getText().trim());
                empresaModificar.setLocalidad((Localidad) cmb_Localidad.getSelectedItem());
                empresaModificar.setLogo(logo);
                empresaService.actualizar(empresaModificar);
                mensaje = "La Empresa " + txt_Nombre.getText().trim() + " se modificó correctamente.";                
            }

            LOGGER.warn(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);            
            this.dispose();

        } catch (BusinessServiceException ex) {
            LOGGER.error(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarComboBoxCondicionesIVA();
        this.cargarComboBoxPaises();        
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.cargarEmpresaParaModificar();
        }
    }//GEN-LAST:event_formWindowOpened

    private void btn_EliminarLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarLogoActionPerformed
        lbl_Logo.setIcon(null);
        lbl_Logo.setText("SIN IMAGEN");
        logo = null;
    }//GEN-LAST:event_btn_EliminarLogoActionPerformed

    private void btn_ExaminarArchivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ExaminarArchivosActionPerformed
        JFileChooser menuElegirLogo = new JFileChooser();
        menuElegirLogo.setAcceptAllFileFilterUsed(false);
        menuElegirLogo.addChoosableFileFilter(new FiltroImagenes());

        try {
            if (menuElegirLogo.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (Utilidades.esTamanioValido(menuElegirLogo.getSelectedFile(), 512000)) {
                    File file = menuElegirLogo.getSelectedFile();
                    logo = Utilidades.convertirFileIntoByteArray(file);
                    ImageIcon logoProvisional = new ImageIcon(menuElegirLogo.getSelectedFile().getAbsolutePath());
                    ImageIcon logoRedimensionado = new ImageIcon(logoProvisional.getImage().getScaledInstance(114, 114, Image.SCALE_SMOOTH));
                    lbl_Logo.setIcon(logoRedimensionado);
                    lbl_Logo.setText("");

                } else {
                    JOptionPane.showMessageDialog(this, "El tamaño del archivo seleccionado, supera el límite de 512kb.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    logo = null;
                }
            } else {
                logo = null;
            }

        } catch (IOException ex) {
            String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_errorIOException");
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ExaminarArchivosActionPerformed

    private void btn_NuevaCondicionIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaCondicionIVAActionPerformed
        GUI_DetalleCondicionIVA gui_DetalleCondicionIVA = new GUI_DetalleCondicionIVA();
        gui_DetalleCondicionIVA.setModal(true);
        gui_DetalleCondicionIVA.setLocationRelativeTo(this);
        gui_DetalleCondicionIVA.setVisible(true);

        try {
            this.cargarComboBoxCondicionesIVA();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevaCondicionIVAActionPerformed

    private void btn_NuevaLocalidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaLocalidadActionPerformed
        GUI_DetalleLocalidad gui_DetalleLocalidad = new GUI_DetalleLocalidad();
        gui_DetalleLocalidad.setModal(true);
        gui_DetalleLocalidad.setLocationRelativeTo(this);
        gui_DetalleLocalidad.setVisible(true);

        try {
            this.cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevaLocalidadActionPerformed

    private void btn_NuevaProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaProvinciaActionPerformed
        GUI_DetalleProvincia gui_DetalleProvincia = new GUI_DetalleProvincia();
        gui_DetalleProvincia.setModal(true);
        gui_DetalleProvincia.setLocationRelativeTo(this);
        gui_DetalleProvincia.setVisible(true);

        try {
            this.cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevaProvinciaActionPerformed

    private void btn_NuevoPaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoPaisActionPerformed
        GUI_DetallePais gui_DetallePais = new GUI_DetallePais();
        gui_DetallePais.setModal(true);
        gui_DetallePais.setLocationRelativeTo(this);
        gui_DetallePais.setVisible(true);

        try {
            this.cargarComboBoxPaises();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevoPaisActionPerformed

    private void cmb_ProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProvinciaItemStateChanged
        if (cmb_Provincia.getItemCount() > 0) {
            try {
                this.cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());

            } catch (BusinessServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            cmb_Localidad.removeAllItems();
        }
    }//GEN-LAST:event_cmb_ProvinciaItemStateChanged

    private void cmb_PaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_PaisItemStateChanged
        if (cmb_Pais.getItemCount() > 0) {
            try {
                this.cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());

            } catch (BusinessServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_cmb_PaisItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_EliminarLogo;
    private javax.swing.JButton btn_ExaminarArchivos;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_NuevaCondicionIVA;
    private javax.swing.JButton btn_NuevaLocalidad;
    private javax.swing.JButton btn_NuevaProvincia;
    private javax.swing.JButton btn_NuevoPais;
    private javax.swing.JComboBox cmb_CondicionIVA;
    private javax.swing.JComboBox cmb_Localidad;
    private javax.swing.JComboBox cmb_Pais;
    private javax.swing.JComboBox cmb_Provincia;
    private com.toedter.calendar.JDateChooser dc_FechaInicioActividad;
    private javax.swing.JLabel lbl_CUIP;
    private javax.swing.JLabel lbl_CondicionIVA;
    private javax.swing.JLabel lbl_Direccion;
    private javax.swing.JLabel lbl_Email;
    private javax.swing.JLabel lbl_FIA;
    private javax.swing.JLabel lbl_IngBrutos;
    private javax.swing.JLabel lbl_Lema;
    private javax.swing.JLabel lbl_Localidad;
    private javax.swing.JLabel lbl_Logo;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JLabel lbl_Pais;
    private javax.swing.JLabel lbl_Provincia;
    private javax.swing.JLabel lbl_Telefono;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTextField txt_CUIP;
    private javax.swing.JTextField txt_Direccion;
    private javax.swing.JTextField txt_Email;
    private javax.swing.JTextField txt_IngBrutos;
    private javax.swing.JTextField txt_Lema;
    private javax.swing.JTextField txt_Nombre;
    private javax.swing.JTextField txt_Telefono;
    // End of variables declaration//GEN-END:variables
}
