package sic.service.impl;

import sic.service.IGastoService;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
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
        if (gastoRepository.getCajaPorID(gasto.getId_Gasto(), gasto.getEmpresa().getId_Empresa()) != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_duplicada"));
        }
    }

    @Override
    @Transactional
    public void guardar(Gasto gasto) {
        this.validarGasto(gasto);
        gastoRepository.guardar(gasto);
        LOGGER.warn("El Gasto " + gasto + " se guardó correctamente." );
    }

    @Override
    public List<Object> getGastosPorFecha(Long id_Empresa, Date desde, Date hasta) {
        return gastoRepository.getGastosPorFecha(id_Empresa, desde, hasta);
    }

    @Override
    public List<Object> getGastosPorFechaYFormaDePago(Long id_Empresa, Long id_FormaDePago, Date desde, Date hasta) {
        return gastoRepository.getGastosPorFechaYFormaDePago(id_Empresa, id_FormaDePago, desde, hasta);
    }

    @Override
    @Transactional
    public void actualizar(Gasto gasto) {
        gastoRepository.actualizar(gasto);
    }

    @Override
    public long getUltimoNumeroDeCaja(long id_Empresa) {
        return gastoRepository.getUltimoNumeroDeGasto(id_Empresa);
    }

    @Override
    public int getUltimoNumeroDeGasto(long id_empresa) {
        return gastoRepository.getUltimoNumeroDeGasto(id_empresa);
    }

}
