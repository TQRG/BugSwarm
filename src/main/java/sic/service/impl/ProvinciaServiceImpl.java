package sic.service.impl;

import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Pais;
import sic.modelo.Provincia;
import sic.repository.IProvinciaRepository;
import sic.service.IProvinciaService;
import sic.service.BusinessServiceException;
import sic.modelo.TipoDeOperacion;
import sic.util.Validator;

@Service
public class ProvinciaServiceImpl implements IProvinciaService {

    private final IProvinciaRepository provinciaRepository;
    private static final Logger LOGGER = Logger.getLogger(ProvinciaServiceImpl.class.getPackage().getName());

    @Autowired
    public ProvinciaServiceImpl(IProvinciaRepository provinciaRepository) {
        this.provinciaRepository = provinciaRepository;
    }
    
    @Override
    public Provincia getProvinciaPorId(Long idProvincia) {
        Provincia provincia = provinciaRepository.getProvinciaPorId(idProvincia);
        if (provincia == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_provincia_no_existente"));
        }
        return provincia;
    }

    @Override
    public List<Provincia> getProvinciasDelPais(Pais pais) {
        return provinciaRepository.getProvinciasDelPais(pais);
    }

    @Override
    public Provincia getProvinciaPorNombre(String nombre, Pais pais) {
        return provinciaRepository.getProvinciaPorNombre(nombre, pais);
    }

    private void validarOperacion(TipoDeOperacion operacion, Provincia provincia) {
        //Requeridos
        if (Validator.esVacio(provincia.getNombre())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_provincia_vacio_nombre"));
        }
        if (provincia.getPais() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_provincia_pais_vacio"));
        }
        //Duplicados
        //Nombre
        Provincia provinciaDuplicada = this.getProvinciaPorNombre(provincia.getNombre(), provincia.getPais());
        if (operacion.equals(TipoDeOperacion.ALTA) && provinciaDuplicada != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_provincia_duplicado_nombre"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (provinciaDuplicada != null && provinciaDuplicada.getId_Provincia() != provincia.getId_Provincia()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_provincia_duplicado_nombre"));
            }
        }
    }

    @Override
    @Transactional
    public void eliminar(long idProvincia) {
        Provincia provincia = this.getProvinciaPorId(idProvincia);
        if (provincia == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_provincia_no_existente"));
        }
        provincia.setEliminada(true);
        provinciaRepository.actualizar(provincia);
    }

    @Override
    @Transactional
    public void actualizar(Provincia provincia) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, provincia);
        provinciaRepository.actualizar(provincia);
    }

    @Override
    @Transactional
    public Provincia guardar(Provincia provincia) {
        this.validarOperacion(TipoDeOperacion.ALTA, provincia);
        provincia = provinciaRepository.guardar(provincia);
        LOGGER.warn("La Provincia " + provincia + " se guardó correctamente.");
        return provincia;
    }
}
