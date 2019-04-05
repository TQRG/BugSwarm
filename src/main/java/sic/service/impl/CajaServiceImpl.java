package sic.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import sic.service.ICajaService;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.BusquedaCajaCriteria;
import sic.modelo.Caja;
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.Pago;
import sic.repository.ICajaRepository;
import sic.service.EstadoCaja;
import sic.service.BusinessServiceException;
import sic.service.IEmpresaService;
import sic.service.IFormaDePagoService;
import sic.service.IGastoService;
import sic.service.IPagoService;
import sic.service.ServiceException;
import sic.util.FormatterFechaHora;
import sic.util.FormatterNumero;
import sic.util.Utilidades;

@Service
public class CajaServiceImpl implements ICajaService {

    private final ICajaRepository cajaRepository;
    private final IFormaDePagoService formaDePagoService;
    private final IPagoService pagoService;
    private final IGastoService gastoService;
    private final IEmpresaService empresaService;
    private final FormatterFechaHora formatoHora = new FormatterFechaHora(FormatterFechaHora.FORMATO_HORA_INTERNACIONAL);
    private static final Logger LOGGER = Logger.getLogger(CajaServiceImpl.class.getPackage().getName());

    @Autowired
    public CajaServiceImpl(ICajaRepository cajaRepository, IFormaDePagoService formaDePagoService,
                           IPagoService pagoService, IGastoService gastoService, IEmpresaService empresaService) {
        this.cajaRepository = cajaRepository;
        this.formaDePagoService = formaDePagoService;
        this.pagoService = pagoService;
        this.gastoService = gastoService;
        this.empresaService = empresaService;
    }

    @Override
    public void validarCaja(Caja caja) {
        //Entrada de Datos
        //Requeridos
        if (caja.getFechaApertura() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_fecha_vacia"));
        }
        if (caja.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_empresa_vacia"));
        }
        if (caja.getUsuarioAbreCaja() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_usuario_vacio"));
        }
        //Duplicados        
        if (cajaRepository.getCajaPorIdYEmpresa(caja.getId_Caja(), caja.getEmpresa().getId_Empresa()) != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_duplicada"));
        }        
    }

    @Override
    @Transactional
    public void guardar(Caja caja) {
        this.validarCaja(caja);
        caja.setNroCaja(this.getUltimoNumeroDeCaja(EmpresaActiva.getInstance().getEmpresa().getId_Empresa()) + 1);
        cajaRepository.guardar(caja);
        LOGGER.warn("La Caja " + caja + " se guardó correctamente." );
    }

    @Override
    @Transactional
    public void actualizar(Caja caja) {        
        cajaRepository.actualizar(caja);
    }
    
    @Override
    @Transactional
    public void eliminar(Caja caja) {
        caja.setEliminada(true);
        this.actualizar(caja);
    }

    @Override
    public Caja getUltimaCaja(long id_Empresa) {        
        return cajaRepository.getUltimaCaja(id_Empresa);        
    }
    
    @Override
    public Caja getCajaPorId(Long id) {
        return cajaRepository.getCajaPorId(id);
    }

    @Override
    public Caja getCajaPorIdYEmpresa(long id_Caja, long id_Empresa) {        
        return cajaRepository.getCajaPorIdYEmpresa(id_Caja, id_Empresa);        
    }

    @Override
    public int getUltimoNumeroDeCaja(long id_Empresa) {
        return cajaRepository.getUltimoNumeroDeCaja(id_Empresa);        
    }
 
    @Override
    public double calcularTotalPagos(List<Pago> movimientos) {
        double total = 0.0;
        for (Object movimiento : movimientos) {
                Pago pago = (Pago) movimiento;
                if (pago.getFactura() instanceof FacturaVenta) {
                    total += pago.getMonto();
                }
                if (pago.getFactura() instanceof FacturaCompra) {
                    total -= pago.getMonto();
                }
        }
        return total;
    }
    
    @Override
    public double calcularTotalGastos(List<Gasto> movimientos) {
        double total = 0.0;
        for (Object movimiento : movimientos) {
                total += ((Gasto) movimiento).getMonto();
        }
        return total;
    }

    @Override
    public List<Caja> getCajas(long id_Empresa, Date desde, Date hasta) {        
        return cajaRepository.getCajas(id_Empresa, desde, hasta);        
    }

    @Override
    public List<Caja> getCajasCriteria(BusquedaCajaCriteria criteria) {
        if (criteria.isBuscaPorFecha() == true & (criteria.getFechaDesde() == null | criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_fechas_invalidas"));
        }
        if (criteria.isBuscaPorFecha() == true) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(criteria.getFechaDesde());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            criteria.setFechaDesde(cal.getTime());
            cal.setTime(criteria.getFechaHasta());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            criteria.setFechaHasta(cal.getTime());
        }        
        
        return cajaRepository.getCajasCriteria(criteria);        
    }

    @Override
    public byte[] getReporteCaja(Caja caja, Long idEmpresa) {
        
        Empresa empresa = empresaService.getEmpresaPorId(idEmpresa);
        List<String> dataSource = new ArrayList<>();
        dataSource.add("Saldo Apertura-" + String.valueOf(FormatterNumero.formatConRedondeo(caja.getSaldoInicial())));

        List<FormaDePago> formasDePago = formaDePagoService.getFormasDePago(empresa);
        double totalPorCorte = caja.getSaldoInicial();
        for (FormaDePago formaDePago : formasDePago) {
            double totalPorCorteFormaDePago = 0.0;
            List<Pago> pagos = pagoService.getPagosEntreFechasYFormaDePago(idEmpresa,
                    formaDePago.getId_FormaDePago(), caja.getFechaApertura(), caja.getFechaCorteInforme());
            List<Gasto> gastos = gastoService.getGastosPorFechaYFormaDePago(idEmpresa,
                    formaDePago.getId_FormaDePago(), caja.getFechaApertura(), caja.getFechaCorteInforme());
            for (Pago pago : pagos) {
                totalPorCorteFormaDePago += pagoService.getTotalPagado(pago.getFactura());
            }
            for (Gasto gasto : gastos) {
                totalPorCorteFormaDePago -= ((Gasto) gasto).getMonto();
            }
            if (totalPorCorteFormaDePago > 0) {
                dataSource.add(formaDePago.getNombre() + "-" + totalPorCorteFormaDePago);
            }
            totalPorCorte += totalPorCorteFormaDePago;
        }
        dataSource.add("Total hasta la hora de control:-" + String.valueOf(FormatterNumero.formatConRedondeo((Number) totalPorCorte)));
        dataSource.add("..........................Corte a las: " + formatoHora.format(caja.getFechaCorteInforme()) + "...........................-");

        Date fechaReporte = new Date();
        if (caja.getFechaCierre() != null) {
            fechaReporte = caja.getFechaCierre();
        }
        for (FormaDePago formaDePago : formasDePago) {
            double totalFormaDePago = 0.0;
            List<Pago> pagos = pagoService.getPagosEntreFechasYFormaDePago(idEmpresa, formaDePago.getId_FormaDePago(),
                    caja.getFechaApertura(), fechaReporte);
            List<Gasto> gastos = gastoService.getGastosPorFechaYFormaDePago(idEmpresa, formaDePago.getId_FormaDePago(),
                    caja.getFechaApertura(), fechaReporte);
            totalFormaDePago = this.calcularTotalPagos(pagos) - this.calcularTotalGastos(gastos);
            if (totalFormaDePago > 0) {
                if (formaDePago.isAfectaCaja()) {
                    dataSource.add(formaDePago.getNombre() + " (Afecta Caja)"
                            + "-" + Utilidades.truncarDecimal(totalFormaDePago,2));
                } else {
                    dataSource.add(formaDePago.getNombre() + " (No afecta Caja)"
                            + "-" + Utilidades.truncarDecimal(totalFormaDePago,2));
                }
            }
        }
        
        
        ClassLoader classLoader = PedidoServiceImpl.class.getClassLoader();
        InputStream isFileReport = classLoader.getResourceAsStream("sic/vista/reportes/Caja.jasper");
        Map params = new HashMap();
        params.put("empresa", caja.getEmpresa());
        params.put("caja", caja);
        params.put("usuario", caja.getUsuarioCierraCaja());
        params.put("logo", Utilidades.convertirByteArrayIntoImage(caja.getEmpresa().getLogo()));
        JRBeanCollectionDataSource listaDS = new JRBeanCollectionDataSource(dataSource);
        try {
            return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(isFileReport, params, listaDS));
        } catch (JRException ex) {
            throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_reporte"), ex);
        }
    }

    @Override
    public Caja cerrarCajaDiaAnterior(Empresa empresa) {
        Caja cajaACerrar = this.getUltimaCaja(empresa.getId_Empresa());
        if ((cajaACerrar != null) && (cajaACerrar.getEstado() == EstadoCaja.ABIERTA)) {
            Calendar fechaAperturaMasUnDia = Calendar.getInstance();
            fechaAperturaMasUnDia.setTime(cajaACerrar.getFechaApertura());
            fechaAperturaMasUnDia.add(Calendar.DATE, 1);
            if (fechaAperturaMasUnDia.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)
                    || fechaAperturaMasUnDia.before(Calendar.getInstance())) {
                cajaACerrar.setFechaCierre(new Date());
                cajaACerrar.setUsuarioCierraCaja(cajaACerrar.getUsuarioAbreCaja());
                cajaACerrar.setEstado(EstadoCaja.CERRADA);
                cajaACerrar.setSaldoReal(cajaACerrar.getSaldoFinal());
            }
        }
        return cajaACerrar;
    }

}
