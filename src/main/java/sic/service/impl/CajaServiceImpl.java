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
import javax.persistence.EntityNotFoundException;
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
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.Pago;
import sic.repository.ICajaRepository;
import sic.modelo.EstadoCaja;
import sic.service.BusinessServiceException;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.IFormaDePagoService;
import sic.service.IGastoService;
import sic.service.IPagoService;
import sic.service.IUsuarioService;
import sic.service.ServiceException;
import sic.util.FormatterFechaHora;
import sic.util.FormatterNumero;
import sic.util.Utilidades;
import sic.util.Validator;

@Service
public class CajaServiceImpl implements ICajaService {

    private final ICajaRepository cajaRepository;
    private final IFormaDePagoService formaDePagoService;
    private final IPagoService pagoService;
    private final IGastoService gastoService;
    private final IEmpresaService empresaService;
    private final IFacturaService facturaService;
    private final IUsuarioService usuarioService;
    private final FormatterFechaHora formatoHora = new FormatterFechaHora(FormatterFechaHora.FORMATO_HORA_INTERNACIONAL);
    private static final Logger LOGGER = Logger.getLogger(CajaServiceImpl.class.getPackage().getName());
    private static final int CANTIDAD_DECIMALES_TRUNCAMIENTO = 2;

    @Autowired
    public CajaServiceImpl(ICajaRepository cajaRepository, IFormaDePagoService formaDePagoService,
                           IPagoService pagoService, IGastoService gastoService,
                           IEmpresaService empresaService, IFacturaService facturaService,
                           IUsuarioService usuarioService) {
        this.cajaRepository = cajaRepository;
        this.formaDePagoService = formaDePagoService;
        this.pagoService = pagoService;
        this.gastoService = gastoService;
        this.empresaService = empresaService;
        this.facturaService = facturaService;
        this.usuarioService = usuarioService;
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
        //Una Caja por dia
        Caja ultimaCaja = this.getUltimaCaja(caja.getEmpresa().getId_Empresa());
        if(ultimaCaja.getEstado() == EstadoCaja.ABIERTA) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_anterior_abierta"));
        }
        if (Validator.compararFechas(ultimaCaja.getFechaApertura(), caja.getFechaApertura()) <= 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_fecha_apertura_no_valida"));
        }
        //Duplicados        
        if (cajaRepository.getCajaPorIdYEmpresa(caja.getId_Caja(), caja.getEmpresa().getId_Empresa()) != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_duplicada"));
        }        
    }

    @Override
    @Transactional
    public Caja guardar(Caja caja) {
        caja.setFechaApertura(new Date());
        this.validarCaja(caja);
        caja.setNroCaja(this.getUltimoNumeroDeCaja(caja.getEmpresa().getId_Empresa()) + 1);        
        caja = cajaRepository.guardar(caja);
        LOGGER.warn("La Caja " + caja + " se guardó correctamente." );
        return caja;
    }

    @Override
    @Transactional
    public void actualizar(Caja caja) {        
        cajaRepository.actualizar(caja);
    }
    
    @Override
    @Transactional
    public void eliminar(Long idCaja) {
        Caja caja = this.getCajaPorId(idCaja);
        if (caja == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_no_existente"));
        }
        caja.setEliminada(true);
        this.actualizar(caja);
    }

    @Override
    public Caja getUltimaCaja(long id_Empresa) {        
        return cajaRepository.getUltimaCaja(id_Empresa);        
    }
    
    @Override
    public Caja getCajaPorId(Long id) {
        Caja caja = cajaRepository.getCajaPorId(id);
        if (caja == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_no_existente"));
        }
        return caja;
    }

    @Override
    public Caja getCajaPorIdYEmpresa(long id_Caja, long id_Empresa) {        
        return cajaRepository.getCajaPorIdYEmpresa(id_Caja, id_Empresa);        
    }
    
    @Override
    public Caja getCajaPorNroYEmpresa(int nroCaja, long id_Empresa) {        
        return cajaRepository.getCajaPorNroYEmpresa(nroCaja, id_Empresa);        
    }

    @Override
    public int getUltimoNumeroDeCaja(long id_Empresa) {
        return cajaRepository.getUltimoNumeroDeCaja(id_Empresa);        
    }
 
    @Override
    public double calcularTotalPagos(List<Pago> pagos) {
        double total = 0.0;
        for (Pago pago : pagos) {
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
    public double calcularTotalGastos(List<Gasto> gastos) {
        double total = 0.0;
        for (Gasto gasto : gastos) {
            total += gasto.getMonto();
        }
        return total;
    }

    @Override
    public List<Caja> getCajas(long id_Empresa, Date desde, Date hasta) {        
        return cajaRepository.getCajas(id_Empresa, desde, hasta);        
    }

    @Override
    public List<Caja> getCajasCriteria(BusquedaCajaCriteria criteria) {
        //Empresa
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Fecha
        if (criteria.isBuscaPorFecha() == true && (criteria.getFechaDesde() == null || criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_fechas_invalidas"));
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
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
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
                totalPorCorteFormaDePago += facturaService.getTotalPagado(pago.getFactura());
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
                    dataSource.add(formaDePago.getNombre() + " (Afecta Caja)" + "-" + Utilidades.truncarDecimal(totalFormaDePago, CANTIDAD_DECIMALES_TRUNCAMIENTO));
                } else {
                    dataSource.add(formaDePago.getNombre() + " (No afecta Caja)" + "-" + Utilidades.truncarDecimal(totalFormaDePago, CANTIDAD_DECIMALES_TRUNCAMIENTO));
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
    @Transactional
    public Caja cerrarCajaAnterior(long idEmpresa) {
        Caja cajaCerrada = this.getUltimaCaja(idEmpresa);
        if ((cajaCerrada != null) && (cajaCerrada.getEstado() == EstadoCaja.ABIERTA)) {
            Calendar fechaAperturaMasUnDia = Calendar.getInstance();
            fechaAperturaMasUnDia.setTime(cajaCerrada.getFechaApertura());
            fechaAperturaMasUnDia.add(Calendar.DATE, 1);
            if (fechaAperturaMasUnDia.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)
                    || fechaAperturaMasUnDia.before(Calendar.getInstance())) {
                cajaCerrada.setFechaCierre(new Date());
                cajaCerrada.setUsuarioCierraCaja(cajaCerrada.getUsuarioAbreCaja());
                cajaCerrada.setEstado(EstadoCaja.CERRADA);
                cajaCerrada.setSaldoReal(cajaCerrada.getSaldoFinal());
                this.actualizar(cajaCerrada);
            }
        }
        return cajaCerrada;
    }
    
    @Override
    @Transactional
    public Caja cerrarCaja(long idCaja, double monto, long idUsuario) {
        Caja cajaACerrar = this.getCajaPorId(idCaja);
        cajaACerrar.setSaldoReal(monto);
        cajaACerrar.setFechaCierre(new Date());
        cajaACerrar.setUsuarioCierraCaja(usuarioService.getUsuarioPorId(idUsuario));
        cajaACerrar.setEstado(EstadoCaja.CERRADA);
        this.actualizar(cajaACerrar);
        return cajaACerrar;
    }

}
