package sic.service.impl;

import sic.service.IGastoService;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Gasto;
import sic.repository.IGastoRepository;
import sic.service.BusinessServiceException;

@Service
public class GastoServiceImpl implements IGastoService {

    private final IGastoRepository gastoRepository;
    private static final Logger LOGGER = Logger.getLogger(GastoServiceImpl.class.getPackage().getName());

    @Autowired
    public GastoServiceImpl(IGastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }
    
    @Override
    public Gasto getGastoPorId(Long idGasto) {
        Gasto gasto = gastoRepository.getGastoPorId(idGasto);
        if (gasto == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_no_existente"));
        }
        return gasto;
    }

    @Override
    public void validarGasto(Gasto gasto) {
        //Entrada de Datos
        //Requeridos
        if (gasto.getFecha() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_fecha_vacia"));
        }
        if (gasto.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_empresa_vacia"));
        }
        if (gasto.getUsuario() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_usuario_vacio"));
        }
        //Duplicados
        if (gastoRepository.getGastoPorIdYEmpresa(gasto.getId_Gasto(), gasto.getEmpresa().getId_Empresa()) != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_duplicada"));
        }
    }

    @Override
    @Transactional
    public Gasto guardar(Gasto gasto) {
        this.validarGasto(gasto);
        gasto.setNroGasto(this.getUltimoNumeroDeGasto(gasto.getEmpresa().getId_Empresa()) + 1);
        gasto = gastoRepository.guardar(gasto);
        LOGGER.warn("El Gasto " + gasto + " se guardó correctamente." );
        return gasto;
    }

    @Override
    public List<Gasto> getGastosPorFecha(Long idEmpresa, Date desde, Date hasta) {
        return gastoRepository.getGastosPorFecha(idEmpresa, desde, hasta);
    }

    @Override
    public Gasto getGastosPorNroYEmpreas(Long nroPago, Long id_Empresa) {
        return gastoRepository.getGastoPorNroYEmpresa(nroPago, id_Empresa);
    }

    @Override
    public List<Gasto> getGastosPorFechaYFormaDePago(Long idEmpresa, Long idFormaDePago, Date desde, Date hasta) {
        return gastoRepository.getGastosPorFechaYFormaDePago(idEmpresa, idFormaDePago, desde, hasta);
    }

    @Override
    @Transactional
    public void actualizar(Gasto gasto) {
        gastoRepository.actualizar(gasto);
    }
    
    @Override
    @Transactional
    public void eliminar(long idGasto) {
        Gasto gastoParaEliminar = this.getGastoPorId(idGasto);
        gastoParaEliminar.setEliminado(true);
        gastoRepository.actualizar(gastoParaEliminar);
    }

    @Override
    public long getUltimoNumeroDeCaja(long idEmpresa) {
        return gastoRepository.getUltimoNumeroDeGasto(idEmpresa);
    }

    @Override
    public long getUltimoNumeroDeGasto(long idEmpresa) {
        return gastoRepository.getUltimoNumeroDeGasto(idEmpresa);
    }

}
