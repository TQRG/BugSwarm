package sic.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import sic.service.ICajaService;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
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
import sic.modelo.EstadoCaja;
import sic.modelo.QCaja;
import sic.service.BusinessServiceException;
import sic.service.IEmpresaService;
import sic.service.IFormaDePagoService;
import sic.service.IGastoService;
import sic.service.IPagoService;
import sic.service.IUsuarioService;
import sic.service.ServiceException;
import sic.util.FormatterFechaHora;
import sic.util.FormatterNumero;
import sic.util.Utilidades;
import sic.util.Validator;
import sic.repository.CajaRepository;

@Service
public class CajaServiceImpl implements ICajaService {

    private final CajaRepository cajaRepository;
    private final IFormaDePagoService formaDePagoService;
    private final IPagoService pagoService;
    private final IGastoService gastoService;
    private final IEmpresaService empresaService;
    private final IUsuarioService usuarioService;
    private final FormatterFechaHora formatoHora = new FormatterFechaHora(FormatterFechaHora.FORMATO_HORA_INTERNACIONAL);
    private static final Logger LOGGER = Logger.getLogger(CajaServiceImpl.class.getPackage().getName());
    private static final int CANTIDAD_DECIMALES_TRUNCAMIENTO = 2;

    @Autowired
    public CajaServiceImpl(CajaRepository cajaRepository, IFormaDePagoService formaDePagoService,
                           IPagoService pagoService, IGastoService gastoService,
                           IEmpresaService empresaService, IUsuarioService usuarioService) {
        this.cajaRepository = cajaRepository;
        this.formaDePagoService = formaDePagoService;
        this.pagoService = pagoService;
        this.gastoService = gastoService;
        this.empresaService = empresaService;
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
        if (cajaRepository.findById(caja.getId_Caja()) != null) {
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
        caja = cajaRepository.save(caja);
        LOGGER.warn("La Caja " + caja + " se guardó correctamente." );
        return caja;
    }

    @Override
    @Transactional
    public void actualizar(Caja caja) {        
        cajaRepository.save(caja);
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
        return cajaRepository.findTopByEmpresaAndEliminadaOrderByFechaAperturaDesc(empresaService.getEmpresaPorId(id_Empresa), false);        
    }
    
    @Override
    public Caja getCajaPorId(Long id) {
        Caja caja = cajaRepository.findOne(id);
        if (caja == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_no_existente"));
        }
        return caja;
    }
    
    @Override
    public Caja getCajaPorNroYEmpresa(int nroCaja, long idEmpresa) {        
        return cajaRepository.findByNroCajaAndEmpresaAndEliminada(nroCaja, empresaService.getEmpresaPorId(idEmpresa), false);        
    }

    @Override
    public int getUltimoNumeroDeCaja(long idEmpresa) {
        return this.getUltimaCaja(idEmpresa).getNroCaja();        
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
    public List<Caja> getCajas(long idEmpresa, Date desde, Date hasta) {        
        return cajaRepository.findAllByFechaAperturaBetweenAndEmpresaAndEliminada(desde, hasta, empresaService.getEmpresaPorId(idEmpresa), false);        
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
        QCaja qcaja = QCaja.caja;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qcaja.empresa.eq(criteria.getEmpresa()).and(qcaja.eliminada.eq(false)));
        if (criteria.isBuscaPorUsuario() == true) {
            builder.and(qcaja.usuarioCierraCaja.eq(criteria.getUsuario()));
        }
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            DateExpression<Date> fDesde = Expressions.dateTemplate(Date.class, "convert({0}, datetime)", formateadorFecha.format(criteria.getFechaDesde()));
            DateExpression<Date> fHasta = Expressions.dateTemplate(Date.class, "convert({0}, datetime)", formateadorFecha.format(criteria.getFechaHasta()));
            builder.and(qcaja.fechaApertura.between(fDesde, fHasta));
        }
        List<Caja> cajas = new ArrayList<>();
        cajaRepository.findAll(builder, new Sort(Sort.Direction.DESC, "fechaApertura")).iterator().forEachRemaining(cajas::add);
        return cajas;        
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
                totalPorCorteFormaDePago += pago.getMonto();
            }
            for (Gasto gasto : gastos) {
                totalPorCorteFormaDePago -= gasto.getMonto();
            }
            if (totalPorCorteFormaDePago != 0) {
                dataSource.add(formaDePago.getNombre() + "-" + FormatterNumero.formatConRedondeo(totalPorCorteFormaDePago));
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
    public Caja cerrarCaja(long idCaja, double monto, long idUsuario) {
        Caja cajaACerrar = this.getCajaPorId(idCaja);
        cajaACerrar.setSaldoReal(monto);
        cajaACerrar.setFechaCierre(new Date());
        cajaACerrar.setUsuarioCierraCaja(usuarioService.getUsuarioPorId(idUsuario));
        cajaACerrar.setEstado(EstadoCaja.CERRADA);
        this.actualizar(cajaACerrar);
        return cajaACerrar;
    }
    
    @PostConstruct                      // Ejecutar al iniciar el contexto
    @Scheduled(cron = "00 00 00 * * *") // Todos los dias a las 00:00:00
    public void cerrarCajas() {
        List<Empresa> empresas = this.empresaService.getEmpresas();
        for (Empresa empresa : empresas) {
            Caja ultimaCajaDeEmpresa = this.getUltimaCaja(empresa.getId_Empresa());
            if ((ultimaCajaDeEmpresa != null) && (ultimaCajaDeEmpresa.getEstado() == EstadoCaja.ABIERTA)) {
                LocalDate fechaActual = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
                Calendar fechaHoraCaja = new GregorianCalendar();
                fechaHoraCaja.setTime(ultimaCajaDeEmpresa.getFechaApertura());
                LocalDate fechaCaja = LocalDate.of(fechaHoraCaja.get(Calendar.YEAR), fechaHoraCaja.get(Calendar.MONTH) + 1, fechaHoraCaja.get(Calendar.DAY_OF_MONTH));
                if (fechaCaja.compareTo(fechaActual) < 0) {
                    ultimaCajaDeEmpresa.setFechaCierre(new Date());
                    ultimaCajaDeEmpresa.setUsuarioCierraCaja(ultimaCajaDeEmpresa.getUsuarioAbreCaja());
                    ultimaCajaDeEmpresa.setEstado(EstadoCaja.CERRADA);
                    ultimaCajaDeEmpresa.setSaldoReal(ultimaCajaDeEmpresa.getSaldoFinal());
                    this.actualizar(ultimaCajaDeEmpresa);
                }
            }
        }
    }
}
