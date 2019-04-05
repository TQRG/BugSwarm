package sic.service.impl;

import java.io.InputStream;
import sic.service.ICajaService;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
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
import sic.modelo.Gasto;
import sic.modelo.Pago;
import sic.modelo.Usuario;
import sic.repository.ICajaRepository;
import sic.service.EstadoCaja;
import sic.service.BusinessServiceException;
import sic.util.Utilidades;

@Service
public class CajaServiceImpl implements ICajaService {

    private final ICajaRepository cajaRepository;    
    private static final Logger LOGGER = Logger.getLogger(CajaServiceImpl.class.getPackage().getName());

    @Autowired
    public CajaServiceImpl(ICajaRepository cajaRepository) {
        this.cajaRepository = cajaRepository;
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
        if (cajaRepository.getCajaPorID(caja.getId_Caja(), caja.getEmpresa().getId_Empresa()) != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_duplicada"));
        }        
    }

    @Override
    @Transactional
    public void guardar(Caja caja) {
        this.validarCaja(caja);
        cajaRepository.guardar(caja);
        LOGGER.warn("La Caja " + caja + " se guardó correctamente." );
    }

    @Override
    @Transactional
    public void actualizar(Caja caja) {        
        cajaRepository.actualizar(caja);
    }

    @Override
    public Caja getUltimaCaja(long id_Empresa) {        
        return cajaRepository.getUltimaCaja(id_Empresa);        
    }

    @Override
    public Caja getCajaPorId(long id_Caja, long id_Empresa) {        
        return cajaRepository.getCajaPorID(id_Caja, id_Empresa);        
    }

    @Override
    public int getUltimoNumeroDeCaja(long id_Empresa) {
        return cajaRepository.getUltimoNumeroDeCaja(id_Empresa);        
    }

    @Override
    public double calcularTotalPorMovimiento(List<Object> movimientos) {
        double total = 0.0;
        for (Object movimiento : movimientos) {
            if (movimiento instanceof Pago) {
                Pago pago = (Pago) movimiento;
                if (pago.getFactura() instanceof FacturaVenta) {
                    total += pago.getMonto();
                }
                if (pago.getFactura() instanceof FacturaCompra) {
                    total -= pago.getMonto();
                }
            }
            if (movimiento instanceof Gasto) {
                total -= ((Gasto) movimiento).getMonto();
            }
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
    public JasperPrint getReporteCaja(Caja caja, List<String> dataSource, Usuario usuario) throws JRException {
        ClassLoader classLoader = PedidoServiceImpl.class.getClassLoader();
        InputStream isFileReport = classLoader.getResourceAsStream("sic/vista/reportes/Caja.jasper");
        Map params = new HashMap();
        params.put("empresa", caja.getEmpresa());
        params.put("caja", caja);
        params.put("usuario", usuario);
        params.put("logo", Utilidades.convertirByteArrayIntoImage(caja.getEmpresa().getLogo()));
        JRBeanCollectionDataSource listaDS = new JRBeanCollectionDataSource(dataSource);
        return JasperFillManager.fillReport(isFileReport, params, listaDS);
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
