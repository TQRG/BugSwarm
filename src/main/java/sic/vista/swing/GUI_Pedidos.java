package sic.vista.swing;

import sic.util.ColoresEstadosRenderer;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.BusquedaPedidoCriteria;
import sic.modelo.Cliente;
import sic.modelo.Pedido;
import sic.modelo.RenglonPedido;
import sic.modelo.Usuario;
import sic.service.BusinessServiceException;
import sic.service.EstadoPedido;
import sic.service.IClienteService;
import sic.service.IEmpresaService;
import sic.service.IPedidoService;
import sic.service.IUsuarioService;
import sic.util.RenderTabla;

public class GUI_Pedidos extends JInternalFrame {

    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IEmpresaService empresaService = appContext.getBean(IEmpresaService.class);
    private final IPedidoService pedidoService = appContext.getBean(IPedidoService.class);
    private final IClienteService clienteService = appContext.getBean(IClienteService.class);
    private final IUsuarioService usuarioService = appContext.getBean(IUsuarioService.class);
    private List<Pedido> pedidos;
    private ModeloTabla modeloTablaPedidos;
    private ModeloTabla modeloTablaRenglones;
    private final int cantidadResultadosParaMostrar = 0; //deshabilitada momentaneamente
    private static final Logger LOGGER = Logger.getLogger(GUI_Pedidos.class.getPackage().getName());

    public GUI_Pedidos() {
        initComponents();
        this.setSize(850, 600);
        this.limpiarJTables();
        cmb_Cliente.addItem("Seleccione un Cliente...");
        cmb_Vendedor.addItem("Seleccione un Vendedor...");
        txt_NumeroPedido.setEnabled(false);
        dc_FechaDesde.setEnabled(false);
        dc_FechaHasta.setEnabled(false);
        cmb_Cliente.setEnabled(false);
        cmb_Vendedor.setEnabled(false);
    }

    public void buscar() {
        cambiarEstadoEnabled(false);
        pb_Filtro.setIndeterminate(true);
        SwingWorker<List<Pedido>, Void> worker = new SwingWorker<List<Pedido>, Void>() {
            @Override
            protected List<Pedido> doInBackground() throws Exception {
                try {
                    BusquedaPedidoCriteria criteria = new BusquedaPedidoCriteria();
                    criteria.setEmpresa(empresaService.getEmpresaActiva().getEmpresa());
                    criteria.setFechaDesde(dc_FechaDesde.getDate());
                    criteria.setFechaHasta(dc_FechaHasta.getDate());
                    criteria.setNroPedido(Long.valueOf(txt_NumeroPedido.getText()));
                    criteria.setBuscaCliente(chk_Cliente.isSelected());
                    if (chk_Cliente.isSelected()) {
                        criteria.setCliente((Cliente) cmb_Cliente.getSelectedItem());
                    }
                    criteria.setBuscaPorFecha(chk_Fecha.isSelected());
                    criteria.setBuscaPorNroPedido(chk_NumeroPedido.isSelected());
                    criteria.setBuscaUsuario(chk_Vendedor.isSelected());
                    if (chk_Vendedor.isSelected()) {
                        criteria.setUsuario((Usuario) cmb_Vendedor.getSelectedItem());
                    }
                    criteria.setCantRegistros(cantidadResultadosParaMostrar);
                    pedidos = pedidoService.buscarConCriteria(criteria);                    

                } catch (BusinessServiceException ex) {
                    JOptionPane.showInternalMessageDialog(getParent(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                } 
                cambiarEstadoEnabled(true);
                return pedidos;
            }

            @Override
            protected void done() {
                cargarResultadosAlTable();
                pb_Filtro.setIndeterminate(false);
                try {
                    if (get().isEmpty()) {
                        JOptionPane.showInternalMessageDialog(getParent(),
                                ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_busqueda_sin_resultados"),
                                "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (InterruptedException ex) {
                    String msjError = "La tarea que se estaba realizando fue interrumpida. Intente nuevamente.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);

                } catch (ExecutionException ex) {
                    String msjError = "Se produjo un error en la ejecución de la tarea solicitada. Intente nuevamente.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void verFacturasVenta() {
        if (tbl_Pedidos.getSelectedRow() != -1) {
            GUI_FacturasVenta gui_facturaVenta = new GUI_FacturasVenta();
            gui_facturaVenta.setLocation(getDesktopPane().getWidth() / 2 - gui_facturaVenta.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui_facturaVenta.getHeight() / 2);
            getDesktopPane().add(gui_facturaVenta);
            long numeroDePedido = (long) tbl_Pedidos.getValueAt(tbl_Pedidos.getSelectedRow(), 2);
            gui_facturaVenta.setVisible(true);
            BusquedaFacturaVentaCriteria criteria = new BusquedaFacturaVentaCriteria();
            criteria.setBuscarPorPedido(true);
            criteria.setNroPedido(numeroDePedido);
            criteria.setEmpresa(empresaService.getEmpresaActiva().getEmpresa());
            gui_facturaVenta.buscarConCriteria(criteria);

            try {
                gui_facturaVenta.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cambiarEstadoEnabled(boolean status) {
        chk_Fecha.setEnabled(status);
        if (status == true && chk_Fecha.isSelected() == true) {            
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
        chk_NumeroPedido.setEnabled(status);
        if (status == true && chk_NumeroPedido.isSelected() == true) {
            txt_NumeroPedido.setEnabled(true);
        } else {
            txt_NumeroPedido.setEnabled(false);
        }
        chk_Cliente.setEnabled(status);
        if (status == true && chk_Cliente.isSelected() == true) {
            cmb_Cliente.setEnabled(true);
        } else {
            cmb_Cliente.setEnabled(false);
        }        
        chk_Vendedor.setEnabled(status);
        if (status == true && chk_Vendedor.isSelected() == true) {
            cmb_Vendedor.setEnabled(true);
        } else {
            cmb_Vendedor.setEnabled(false);
        }        
        btn_Buscar.setEnabled(status);        
        btn_NuevoPedido.setEnabled(status);
        btn_VerFacturas.setEnabled(status);
        btn_Facturar.setEnabled(status);
        bnt_modificaPedido.setEnabled(status);
        btn_eliminarPedido.setEnabled(status);
        btn_imprimirPedido.setEnabled(status);
    }
    
    private void cargarResultadosAlTable() {
        this.limpiarJTables();
        for (Pedido pedido : pedidos) {
            Object[] fila = new Object[7];
            fila[0] = pedido.getEstado();
            fila[1] = pedido.getFecha();
            fila[2] = pedido.getNroPedido();
            fila[3] = pedido.getCliente().getRazonSocial();
            fila[4] = pedido.getUsuario().getNombre();
            fila[5] = pedido.getTotalEstimado();
            fila[6] = pedido.getTotalActual();
            modeloTablaPedidos.addRow(fila);
        }
        tbl_Pedidos.setModel(modeloTablaPedidos);
        tbl_Pedidos.setDefaultRenderer(EstadoPedido.class, new ColoresEstadosRenderer());
        lbl_cantResultados.setText(pedidos.size() + " pedidos encontrados");
    }

    private void limpiarJTables() {
        modeloTablaPedidos = new ModeloTabla();
        tbl_Pedidos.setModel(modeloTablaPedidos);
        modeloTablaRenglones = new ModeloTabla();
        tbl_RenglonesPedido.setModel(modeloTablaRenglones);
        this.setColumnasPedido();
        this.setColumnasRenglonesPedido();
    }

    private void limpiarTablaRenglones() {
        modeloTablaRenglones = new ModeloTabla();
        tbl_RenglonesPedido.setModel(modeloTablaRenglones);
        this.setColumnasRenglonesPedido();
    }

    private void setColumnasRenglonesPedido() {
        //sorting
        tbl_RenglonesPedido.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezadoRenglones = new String[6];
        encabezadoRenglones[0] = "Codigo";
        encabezadoRenglones[1] = "Descripcion";
        encabezadoRenglones[2] = "Cantidad";
        encabezadoRenglones[3] = "Precio Lista";
        encabezadoRenglones[4] = "% Descuento";
        encabezadoRenglones[5] = "SubTotal";
        modeloTablaRenglones.setColumnIdentifiers(encabezadoRenglones);
        tbl_RenglonesPedido.setModel(modeloTablaRenglones);

        //tipo de dato columnas
        Class[] tiposRenglones = new Class[modeloTablaRenglones.getColumnCount()];
        tiposRenglones[0] = String.class;
        tiposRenglones[1] = String.class;
        tiposRenglones[2] = Integer.class;
        tiposRenglones[3] = Double.class;
        tiposRenglones[4] = Double.class;
        tiposRenglones[5] = Double.class;
        modeloTablaRenglones.setClaseColumnas(tiposRenglones);
        tbl_RenglonesPedido.getTableHeader().setReorderingAllowed(false);
        tbl_RenglonesPedido.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_RenglonesPedido.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_RenglonesPedido.getColumnModel().getColumn(0).setPreferredWidth(25);
        tbl_RenglonesPedido.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbl_RenglonesPedido.getColumnModel().getColumn(2).setPreferredWidth(25);
        tbl_RenglonesPedido.getColumnModel().getColumn(3).setPreferredWidth(25);
        tbl_RenglonesPedido.getColumnModel().getColumn(4).setPreferredWidth(25);
        tbl_RenglonesPedido.getColumnModel().getColumn(5).setPreferredWidth(25);
    }

    private void setColumnasPedido() {
        //sorting
        tbl_Pedidos.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[7];
        encabezados[0] = "Estado";
        encabezados[1] = "Fecha Pedido";
        encabezados[2] = "Nº Pedido";
        encabezados[3] = "Cliente";
        encabezados[4] = "Usuario (Vendedor)";
        encabezados[5] = "Total Estimado";
        encabezados[6] = "Total Actual";
        modeloTablaPedidos.setColumnIdentifiers(encabezados);
        tbl_Pedidos.setModel(modeloTablaPedidos);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaPedidos.getColumnCount()];
        tipos[0] = EstadoPedido.class;
        tipos[1] = Date.class;
        tipos[2] = Long.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = Double.class;
        tipos[6] = Double.class;
        modeloTablaPedidos.setClaseColumnas(tipos);
        tbl_Pedidos.getTableHeader().setReorderingAllowed(false);
        tbl_Pedidos.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Pedidos.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_Pedidos.getColumnModel().getColumn(0).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(1).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(2).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(3).setPreferredWidth(150);
        tbl_Pedidos.getColumnModel().getColumn(4).setPreferredWidth(100);
        tbl_Pedidos.getColumnModel().getColumn(5).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(6).setPreferredWidth(25);
    }

    private void cargarComboBoxClientes() {
        cmb_Cliente.removeAllItems();
        List<Cliente> clientes;
        clientes = clienteService.getClientes(empresaService.getEmpresaActiva().getEmpresa());
        for (Cliente cliente : clientes) {
            cmb_Cliente.addItem(cliente);
        }
    }

    private void cargarComboBoxUsuarios() {
        cmb_Vendedor.removeAllItems();
        List<Usuario> usuarios;
        usuarios = usuarioService.getUsuarios();
        for (Usuario usuario : usuarios) {
            cmb_Vendedor.addItem(usuario);
        }
    }

    private boolean existeClienteDisponible() {
        return !clienteService.getClientes(empresaService.getEmpresaActiva().getEmpresa()).isEmpty();
    }

    private void cargarRenglonesDelPedidoSeleccionadoEnTabla(java.awt.event.KeyEvent evt) {
        int row = tbl_Pedidos.getSelectedRow();
        if (evt != null) {
            if ((evt.getKeyCode() == KeyEvent.VK_UP) && row > 0) {
                row--;
            }
            if ((evt.getKeyCode() == KeyEvent.VK_DOWN) && (row + 1) < tbl_Pedidos.getRowCount()) {
                row++;
            }
        }
        this.limpiarTablaRenglones();
        this.setColumnasRenglonesPedido();
        long nroPedido = (long) tbl_Pedidos.getValueAt(row, 2);
        Pedido paraListarRenglones = pedidoService.getPedidoPorNumeroConRenglonesActualizandoSubtotales(nroPedido, empresaService.getEmpresaActiva().getEmpresa().getId_Empresa());
        for (RenglonPedido renglon : paraListarRenglones.getRenglones()) {
            Object[] fila = new Object[6];
            fila[0] = renglon.getProducto().getCodigo();
            fila[1] = renglon.getProducto().getDescripcion();
            fila[2] = renglon.getCantidad();
            fila[3] = renglon.getProducto().getPrecioLista();
            fila[4] = renglon.getDescuento_porcentaje();
            fila[5] = renglon.getSubTotal();
            modeloTablaRenglones.addRow(fila);
        }
        tbl_RenglonesPedido.setModel(modeloTablaRenglones);
    }

    private void lanzarReportePedido(Pedido pedido) {
        try {
            JasperPrint report = pedidoService.getReportePedido(pedido);
            JDialog viewer = new JDialog(new JFrame(), "Vista Previa", true);
            viewer.setSize(this.getWidth() - 25, this.getHeight() - 25);
            ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/SIC_16_square.png"));
            viewer.setIconImage(iconoVentana.getImage());
            viewer.setLocationRelativeTo(null);
            JRViewer jrv = new JRViewer(report);
            viewer.getContentPane().add(jrv);
            viewer.setVisible(true);
        } catch (JRException jre) {
            String msjError = "Se produjo un error procesando el reporte.";
            LOGGER.error(msjError + " - " + jre.getMessage());
            JOptionPane.showMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_Filtros = new javax.swing.JPanel();
        chk_NumeroPedido = new javax.swing.JCheckBox();
        chk_Fecha = new javax.swing.JCheckBox();
        lbl_Desde = new javax.swing.JLabel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        lbl_Hasta = new javax.swing.JLabel();
        chk_Cliente = new javax.swing.JCheckBox();
        cmb_Cliente = new javax.swing.JComboBox();
        cmb_Vendedor = new javax.swing.JComboBox();
        chk_Vendedor = new javax.swing.JCheckBox();
        btn_Buscar = new javax.swing.JButton();
        txt_NumeroPedido = new javax.swing.JFormattedTextField();
        pb_Filtro = new javax.swing.JProgressBar();
        lbl_cantResultados = new javax.swing.JLabel();
        panel_resultados = new javax.swing.JPanel();
        sp_RenglonesDelPedido = new javax.swing.JScrollPane();
        tbl_RenglonesPedido = new javax.swing.JTable();
        sp_Pedidos = new javax.swing.JScrollPane();
        tbl_Pedidos = new javax.swing.JTable();
        btn_NuevoPedido = new javax.swing.JButton();
        btn_VerFacturas = new javax.swing.JButton();
        btn_Facturar = new javax.swing.JButton();
        btn_imprimirPedido = new javax.swing.JButton();
        bnt_modificaPedido = new javax.swing.JButton();
        btn_eliminarPedido = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Pedidos");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturar_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        panel_Filtros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_NumeroPedido.setText("Nº Pedido:");
        chk_NumeroPedido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumeroPedidoItemStateChanged(evt);
            }
        });

        chk_Fecha.setText("Fecha Pedido:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        lbl_Desde.setText("Desde:");

        lbl_Hasta.setText("Hasta:");

        chk_Cliente.setText("Cliente:");
        chk_Cliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ClienteItemStateChanged(evt);
            }
        });

        cmb_Cliente.setToolTipText("");

        chk_Vendedor.setText("Usuario (Vendedor):");
        chk_Vendedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_VendedorItemStateChanged(evt);
            }
        });

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        txt_NumeroPedido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NumeroPedido.setText("0");

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panel_FiltrosLayout = new javax.swing.GroupLayout(panel_Filtros);
        panel_Filtros.setLayout(panel_FiltrosLayout);
        panel_FiltrosLayout.setHorizontalGroup(
            panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_FiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_FiltrosLayout.createSequentialGroup()
                        .addComponent(chk_Vendedor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmb_Vendedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panel_FiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pb_Filtro, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_FiltrosLayout.createSequentialGroup()
                        .addComponent(chk_Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_Desde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_Hasta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_FiltrosLayout.createSequentialGroup()
                        .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_NumeroPedido))
                    .addGroup(panel_FiltrosLayout.createSequentialGroup()
                        .addComponent(chk_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmb_Cliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panel_FiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dc_FechaDesde, dc_FechaHasta});

        panel_FiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chk_Cliente, chk_Fecha, chk_NumeroPedido, chk_Vendedor});

        panel_FiltrosLayout.setVerticalGroup(
            panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_FiltrosLayout.createSequentialGroup()
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Desde)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Fecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_NumeroPedido)
                    .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_Cliente)
                    .addComponent(cmb_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_Vendedor)
                    .addComponent(cmb_Vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btn_Buscar)
                        .addComponent(pb_Filtro, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmb_Cliente, cmb_Vendedor, txt_NumeroPedido});

        panel_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Buscar, pb_Filtro});

        panel_resultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_RenglonesPedido.setModel(new javax.swing.table.DefaultTableModel(
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
        sp_RenglonesDelPedido.setViewportView(tbl_RenglonesPedido);

        tbl_Pedidos.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_Pedidos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Pedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_PedidosMouseClicked(evt);
            }
        });
        tbl_Pedidos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_PedidosKeyPressed(evt);
            }
        });
        sp_Pedidos.setViewportView(tbl_Pedidos);

        btn_NuevoPedido.setForeground(java.awt.Color.blue);
        btn_NuevoPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoNuevo_16x16.png"))); // NOI18N
        btn_NuevoPedido.setText("Nuevo");
        btn_NuevoPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoPedidoActionPerformed(evt);
            }
        });

        btn_VerFacturas.setForeground(java.awt.Color.blue);
        btn_VerFacturas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturas_16x16.png"))); // NOI18N
        btn_VerFacturas.setText("Ver Facturas");
        btn_VerFacturas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerFacturasActionPerformed(evt);
            }
        });

        btn_Facturar.setForeground(java.awt.Color.blue);
        btn_Facturar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturar_16x16.png"))); // NOI18N
        btn_Facturar.setText("Facturar");
        btn_Facturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_FacturarActionPerformed(evt);
            }
        });

        btn_imprimirPedido.setForeground(new java.awt.Color(0, 0, 255));
        btn_imprimirPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Printer_16x16.png"))); // NOI18N
        btn_imprimirPedido.setText("Imprimir");
        btn_imprimirPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_imprimirPedidoActionPerformed(evt);
            }
        });

        bnt_modificaPedido.setForeground(new java.awt.Color(0, 0, 225));
        bnt_modificaPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoEditar_16x16.png"))); // NOI18N
        bnt_modificaPedido.setText("Modificar");
        bnt_modificaPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bnt_modificaPedidoActionPerformed(evt);
            }
        });

        btn_eliminarPedido.setForeground(new java.awt.Color(0, 0, 255));
        btn_eliminarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btn_eliminarPedido.setText("Eliminar");
        btn_eliminarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarPedidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_resultadosLayout = new javax.swing.GroupLayout(panel_resultados);
        panel_resultados.setLayout(panel_resultadosLayout);
        panel_resultadosLayout.setHorizontalGroup(
            panel_resultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Pedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
            .addComponent(sp_RenglonesDelPedido)
            .addGroup(panel_resultadosLayout.createSequentialGroup()
                .addComponent(btn_NuevoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btn_VerFacturas)
                .addGap(0, 0, 0)
                .addComponent(btn_Facturar)
                .addGap(0, 0, 0)
                .addComponent(bnt_modificaPedido)
                .addGap(0, 0, 0)
                .addComponent(btn_eliminarPedido)
                .addGap(0, 0, 0)
                .addComponent(btn_imprimirPedido)
                .addGap(0, 236, Short.MAX_VALUE))
        );
        panel_resultadosLayout.setVerticalGroup(
            panel_resultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_resultadosLayout.createSequentialGroup()
                .addComponent(sp_Pedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_RenglonesDelPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_resultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_NuevoPedido)
                    .addComponent(btn_VerFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Facturar)
                    .addComponent(bnt_modificaPedido)
                    .addComponent(btn_imprimirPedido)
                    .addComponent(btn_eliminarPedido)))
        );

        panel_resultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {bnt_modificaPedido, btn_Facturar, btn_NuevoPedido, btn_VerFacturas, btn_imprimirPedido});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_resultados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 355, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panel_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_resultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.buscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void chk_ClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ClienteItemStateChanged
        if (chk_Cliente.isSelected() == true) {
            cmb_Cliente.setEnabled(true);
            this.cargarComboBoxClientes();
            cmb_Cliente.requestFocus();
        } else {
            cmb_Cliente.removeAllItems();
            cmb_Cliente.addItem("Seleccione un Cliente...");
            cmb_Cliente.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ClienteItemStateChanged

    private void chk_VendedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_VendedorItemStateChanged
        if (chk_Vendedor.isSelected() == true) {
            cmb_Vendedor.setEnabled(true);
            this.cargarComboBoxUsuarios();
            cmb_Vendedor.requestFocus();
        } else {
            cmb_Vendedor.removeAllItems();
            cmb_Vendedor.addItem("Seleccione un Vendedor...");
            cmb_Vendedor.setEnabled(false);
        }
    }//GEN-LAST:event_chk_VendedorItemStateChanged

    private void chk_NumeroPedidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumeroPedidoItemStateChanged
        if (chk_NumeroPedido.isSelected() == true) {
            txt_NumeroPedido.setEnabled(true);
            txt_NumeroPedido.requestFocus();
        } else {
            txt_NumeroPedido.removeAll();
            txt_NumeroPedido.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumeroPedidoItemStateChanged

    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
    }//GEN-LAST:event_chk_FechaItemStateChanged

    private void btn_NuevoPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoPedidoActionPerformed
        try {
            if (this.existeClienteDisponible()) {
                GUI_PuntoDeVenta gui_puntoDeVenta = new GUI_PuntoDeVenta();
                Pedido pedido = new Pedido();
                pedido.setObservaciones("Los precios se encuentran sujetos a modificaciones.");
                gui_puntoDeVenta.setPedido(pedido);
                gui_puntoDeVenta.setModal(true);
                gui_puntoDeVenta.setLocationRelativeTo(this);
                gui_puntoDeVenta.setVisible(true);
                this.buscar();
            } else {
                String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente");
                JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevoPedidoActionPerformed

    private void btn_FacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_FacturarActionPerformed
        try {
            if (tbl_Pedidos.getSelectedRow() != -1) {
                long nroPedido = (long) tbl_Pedidos.getValueAt(tbl_Pedidos.getSelectedRow(), 2);
                Pedido pedido = pedidoService.getPedidoPorNumero(nroPedido, empresaService.getEmpresaActiva().getEmpresa().getId_Empresa());
                if (pedido.getEstado() == EstadoPedido.CERRADO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_facturado"), "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (this.existeClienteDisponible()) {
                    GUI_PuntoDeVenta gui_puntoDeVenta = new GUI_PuntoDeVenta();
                    gui_puntoDeVenta.setPedido(pedido);
                    gui_puntoDeVenta.setModal(true);
                    gui_puntoDeVenta.setLocationRelativeTo(this);
                    gui_puntoDeVenta.setVisible(true);
                    this.buscar();
                } else {
                    String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente");
                    JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_FacturarActionPerformed

    private void btn_VerFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerFacturasActionPerformed
        this.verFacturasVenta();
    }//GEN-LAST:event_btn_VerFacturasActionPerformed

    private void tbl_PedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_PedidosMouseClicked
        this.cargarRenglonesDelPedidoSeleccionadoEnTabla(null);
    }//GEN-LAST:event_tbl_PedidosMouseClicked

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setMaximum(true);

        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbl_PedidosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_PedidosKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            this.cargarRenglonesDelPedidoSeleccionadoEnTabla(evt);
        }
    }//GEN-LAST:event_tbl_PedidosKeyPressed

    private void btn_imprimirPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_imprimirPedidoActionPerformed
        if (tbl_Pedidos.getSelectedRow() != -1) {
            long nroPedido = (long) tbl_Pedidos.getValueAt(tbl_Pedidos.getSelectedRow(), 2);
            Pedido pedido = pedidoService.getPedidoPorNumero(nroPedido, empresaService.getEmpresaActiva().getEmpresa().getId_Empresa());
            this.lanzarReportePedido(pedidoService.calcularTotalActualDePedido(pedido));
        }
    }//GEN-LAST:event_btn_imprimirPedidoActionPerformed

    private void bnt_modificaPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_modificaPedidoActionPerformed
        try {
            if (tbl_Pedidos.getSelectedRow() != -1) {
                long nroPedido = (long) tbl_Pedidos.getValueAt(tbl_Pedidos.getSelectedRow(), 2);
                Pedido pedido = pedidoService.getPedidoPorNumero(nroPedido, empresaService.getEmpresaActiva().getEmpresa().getId_Empresa());
                if (pedido.getEstado() == EstadoPedido.CERRADO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_facturado"), "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (pedido.getEstado() == EstadoPedido.ACTIVO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_procesado"), "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (this.existeClienteDisponible()) {
                    GUI_PuntoDeVenta gui_puntoDeVenta = new GUI_PuntoDeVenta();
                    gui_puntoDeVenta.setPedido(pedido);
                    gui_puntoDeVenta.setModificarPedido(true);
                    gui_puntoDeVenta.setModal(true);
                    gui_puntoDeVenta.setLocationRelativeTo(this);
                    gui_puntoDeVenta.setVisible(true);
                    this.buscar();
                } else {
                    String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente");
                    JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bnt_modificaPedidoActionPerformed

    private void btn_eliminarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarPedidoActionPerformed
        try {
            if (tbl_Pedidos.getSelectedRow() != -1) {
                long nroPedido = (long) tbl_Pedidos.getValueAt(tbl_Pedidos.getSelectedRow(), 2);
                Pedido pedido = pedidoService.getPedidoPorNumero(nroPedido, empresaService.getEmpresaActiva().getEmpresa().getId_Empresa());
                if (pedido.getEstado() == EstadoPedido.CERRADO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_facturado"), "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (pedido.getEstado() == EstadoPedido.ACTIVO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_procesado"), "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (this.existeClienteDisponible()) {
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "¿Esta seguro que desea eliminar el pedido seleccionado?",
                            "Eliminar", JOptionPane.YES_NO_OPTION);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        pedidoService.eliminar(pedido);
                        this.buscar();
                    }
                } else {
                    String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente");
                    JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                }

            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showInternalMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_eliminarPedidoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bnt_modificaPedido;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Facturar;
    private javax.swing.JButton btn_NuevoPedido;
    private javax.swing.JButton btn_VerFacturas;
    private javax.swing.JButton btn_eliminarPedido;
    private javax.swing.JButton btn_imprimirPedido;
    private javax.swing.JCheckBox chk_Cliente;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumeroPedido;
    private javax.swing.JCheckBox chk_Vendedor;
    private javax.swing.JComboBox cmb_Cliente;
    private javax.swing.JComboBox cmb_Vendedor;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panel_Filtros;
    private javax.swing.JPanel panel_resultados;
    private javax.swing.JProgressBar pb_Filtro;
    private javax.swing.JScrollPane sp_Pedidos;
    private javax.swing.JScrollPane sp_RenglonesDelPedido;
    private javax.swing.JTable tbl_Pedidos;
    private javax.swing.JTable tbl_RenglonesPedido;
    private javax.swing.JFormattedTextField txt_NumeroPedido;
    // End of variables declaration//GEN-END:variables
}
