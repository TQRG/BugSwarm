package sic.service.impl;

import sic.modelo.BusquedaTransportistaCriteria;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Empresa;
import sic.modelo.Transportista;
import sic.repository.ITransportistaRepository;
import sic.service.ITransportistaService;
import sic.service.BusinessServiceException;
import sic.service.TipoDeOperacion;
import sic.util.Validator;

@Service
public class TransportistaServiceImpl implements ITransportistaService {

    private final ITransportistaRepository transportistaRepository;
    private static final Logger LOGGER = Logger.getLogger(TransportistaServiceImpl.class.getPackage().getName());

    @Autowired
    public TransportistaServiceImpl(ITransportistaRepository transportistaRepository) {
        this.transportistaRepository = transportistaRepository;
    }

    @Override
    public List<Transportista> getTransportistas(Empresa empresa) {
        return transportistaRepository.getTransportistas(empresa);
    }

    @Override
    public List<Transportista> buscarTransportistas(BusquedaTransportistaCriteria criteria) {
        //@Todo No debe verificar contra la palabra "Todos/as". Usar el boolean asociado a ese campo
        //Pais
        if (criteria.getPais().getNombre().equals("Todos")) {
            criteria.setBuscarPorPais(false);
        }
        //Provincia
        if (criteria.getProvincia().getNombre().equals("Todas")) {
            criteria.setBuscarPorProvincia(false);
        }
        //Localidad
        if (criteria.getLocalidad().getNombre().equals("Todas")) {
            criteria.setBuscarPorLocalidad(false);
        }
        return transportistaRepository.busquedaPersonalizada(criteria);
    }

    @Override
    public Transportista getTransportistaPorNombre(String nombre, Empresa empresa) {
        return transportistaRepository.getTransportistaPorNombre(nombre, empresa);
    }

    private void validarOperacion(TipoDeOperacion operacion, Transportista transportista) {
        //Requeridos
        if (Validator.esVacio(transportista.getNombre())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_transportista_nombre_vacio"));
        }
        if (transportista.getLocalidad() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_transportista_localidad_vacia"));
        }
        if (transportista.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_transportista_empresa_vacia"));
        }
        //Duplicados
        //Nombre
        Transportista transportistaDuplicado = this.getTransportistaPorNombre(transportista.getNombre(), transportista.getEmpresa());
        if (operacion.equals(TipoDeOperacion.ALTA) && transportistaDuplicado != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_transportista_duplicado_nombre"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (transportistaDuplicado != null && transportistaDuplicado.getId_Transportista() != transportista.getId_Transportista()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_transportista_duplicado_nombre"));
            }
        }
    }

    @Override
    @Transactional
    public void guardar(Transportista transportista) {
        this.validarOperacion(TipoDeOperacion.ALTA, transportista);
        transportistaRepository.guardar(transportista);
        LOGGER.warn("El Transportista " + transportista + " se guardó correctamente.");
    }

    @Override
    @Transactional
    public void actualizar(Transportista transportista) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, transportista);
        transportistaRepository.actualizar(transportista);        
    }

    @Override
    @Transactional
    public void eliminar(Transportista transportista) {
        transportista.setEliminado(true);
        transportistaRepository.actualizar(transportista);
    }
}
