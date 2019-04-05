package sic.service.impl;

import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Empresa;
import sic.modelo.Rubro;
import sic.repository.IRubroRepository;
import sic.service.IRubroService;
import sic.service.BusinessServiceException;
import sic.modelo.TipoDeOperacion;
import sic.util.Validator;

@Service
public class RubroServiceImpl implements IRubroService {

    private final IRubroRepository rubroRepository;
    private static final Logger LOGGER = Logger.getLogger(RubroServiceImpl.class.getPackage().getName());

    @Autowired
    public RubroServiceImpl(IRubroRepository rubroRepository) {
        this.rubroRepository = rubroRepository;
    }
    
    @Override
    public Rubro getRubroPorId(Long idRubro){
        Rubro rubro = rubroRepository.getRubroPorId(idRubro);
        if (rubro == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_rubro_no_existente"));
        }
        return rubro;
    }

    @Override
    public List<Rubro> getRubros(Empresa empresa) {
        return rubroRepository.getRubros(empresa);
    }

    @Override
    public Rubro getRubroPorNombre(String nombre, Empresa empresa) {
        return rubroRepository.getRubroPorNombre(nombre, empresa);
    }

    private void validarOperacion(TipoDeOperacion operacion, Rubro rubro) {
        //Requeridos
        if (Validator.esVacio(rubro.getNombre())) {
            throw new BusinessServiceException(ResourceBundle.getBundle(
                    "Mensajes").getString("mensaje_rubro_nombre_vacio"));
        }
        //Duplicados
        //Nombre
        Rubro rubroDuplicado = this.getRubroPorNombre(rubro.getNombre(), rubro.getEmpresa());
        if (operacion.equals(TipoDeOperacion.ALTA) && rubroDuplicado != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_rubro_nombre_duplicado"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (rubroDuplicado != null && rubroDuplicado.getId_Rubro() != rubro.getId_Rubro()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_rubro_nombre_duplicado"));
            }
        }
    }

    @Override
    @Transactional
    public void actualizar(Rubro rubro) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, rubro);
        rubroRepository.actualizar(rubro);
    }

    @Override
    @Transactional
    public Rubro guardar(Rubro rubro) {
        this.validarOperacion(TipoDeOperacion.ALTA, rubro);
        rubro = rubroRepository.guardar(rubro);
        LOGGER.warn("El Rubro " + rubro + " se guardó correctamente.");
        return rubro;
    }

    @Override
    @Transactional
    public void eliminar(long idRubro) {
        Rubro rubro = this.getRubroPorId(idRubro);
        if (rubro == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pedido_no_existente"));
        }
        rubro.setEliminado(true);
        rubroRepository.actualizar(rubro);
    }
}
