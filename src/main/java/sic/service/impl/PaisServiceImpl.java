package sic.service.impl;

import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Pais;
import sic.repository.IPaisRepository;
import sic.service.IPaisService;
import sic.service.BusinessServiceException;
import sic.service.TipoDeOperacion;
import sic.util.Validator;

@Service
public class PaisServiceImpl implements IPaisService {

    private final IPaisRepository paisRepository;
    private static final Logger LOGGER = Logger.getLogger(PaisServiceImpl.class.getPackage().getName());

    @Autowired
    public PaisServiceImpl(IPaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }
    
    @Override
    public Pais getPaisPorId(Long id_Pais) {
        return paisRepository.getPaisPorId(id_Pais);
    }

    @Override
    public List<Pais> getPaises() {
        return paisRepository.getPaises();
    }

    @Override
    public Pais getPaisPorNombre(String nombre) {
        return paisRepository.getPaisPorNombre(nombre);
    }

    private void validarOperacion(TipoDeOperacion operacion, Pais pais) {
        //Obligatorios
        if (Validator.esVacio(pais.getNombre())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pais_vacio_nombre"));
        }
        //Duplicados
        //Nombre
        Pais paisDuplicado = this.getPaisPorNombre(pais.getNombre());
        if (operacion.equals(TipoDeOperacion.ALTA) && paisDuplicado != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pais_duplicado_nombre"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (paisDuplicado != null && paisDuplicado.getId_Pais() != pais.getId_Pais()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_pais_duplicado_nombre"));
            }
        }
    }

    @Override
    @Transactional
    public void eliminar(Pais pais) {
        pais.setEliminado(true);
        paisRepository.actualizar(pais);
    }

    @Override
    @Transactional
    public void actualizar(Pais pais) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, pais);
        paisRepository.actualizar(pais);
    }

    @Override
    @Transactional
    public void guardar(Pais pais) {
        this.validarOperacion(TipoDeOperacion.ALTA, pais);
        paisRepository.guardar(pais);
        LOGGER.warn("El Pais " + pais + " se guardó correctamente.");
    }
}
