package sic.service.impl;

import sic.modelo.BusquedaProveedorCriteria;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Empresa;
import sic.modelo.Proveedor;
import sic.repository.IProveedorRepository;
import sic.service.IProveedorService;
import sic.service.BusinessServiceException;
import sic.service.TipoDeOperacion;
import sic.util.Validator;

@Service
public class ProveedorServiceImpl implements IProveedorService {

    private final IProveedorRepository proveedorRepository;
    private static final Logger LOGGER = Logger.getLogger(ProveedorServiceImpl.class.getPackage().getName());

    @Autowired
    public ProveedorServiceImpl(IProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<Proveedor> getProveedores(Empresa empresa) {
        return proveedorRepository.getProveedores(empresa);
    }

    @Override
    public List<Proveedor> buscarProveedores(BusquedaProveedorCriteria criteria) {
        //@Todo No debe verificar contra la palabra "Todos/as". Usar el boolean asociado a ese campo
        //Pais
        if (criteria.getPais().getNombre().equals("Todos")) {
            criteria.setBuscaPorPais(false);
        }
        //Provincia
        if (criteria.getProvincia().getNombre().equals("Todas")) {
            criteria.setBuscaPorProvincia(false);
        }
        //Localidad
        if (criteria.getLocalidad().getNombre().equals("Todas")) {
            criteria.setBuscaPorLocalidad(false);
        }
        return proveedorRepository.buscarProveedores(criteria);
    }

    @Override
    public Proveedor getProveedorPorCodigo(String codigo, Empresa empresa) {
        return proveedorRepository.getProveedorPorCodigo(codigo, empresa);
    }

    @Override
    public Proveedor getProveedorPorId_Fiscal(String id_Fiscal, Empresa empresa) {
        return proveedorRepository.getProveedorPorId_Fiscal(id_Fiscal, empresa);
    }

    @Override
    public Proveedor getProveedorPorRazonSocial(String razonSocial, Empresa empresa) {
        return proveedorRepository.getProveedorPorRazonSocial(razonSocial, empresa);
    }

    private void validarOperacion(TipoDeOperacion operacion, Proveedor proveedor) {
        //Entrada de Datos
        if (!Validator.esEmailValido(proveedor.getEmail())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_proveedor_email_invalido"));
        }
        //Requeridos
        if (Validator.esVacio(proveedor.getRazonSocial())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_proveedor_razonSocial_vacia"));
        }
        if (proveedor.getCondicionIVA() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_proveedor_condicionIVA_vacia"));
        }
        if (proveedor.getLocalidad() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_proveedor_localidad_vacia"));
        }
        if (proveedor.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_proveedor_empresa_vacia"));
        }
        //Duplicados
        //Codigo
        if (!proveedor.getCodigo().equals("")) {
            Proveedor proveedorDuplicado = this.getProveedorPorCodigo(proveedor.getCodigo(), proveedor.getEmpresa());
            if (operacion.equals(TipoDeOperacion.ACTUALIZACION)
                    && proveedorDuplicado != null
                    && proveedorDuplicado.getId_Proveedor() != proveedor.getId_Proveedor()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_proveedor_duplicado_codigo"));
            }
            if (operacion.equals(TipoDeOperacion.ALTA)
                    && proveedorDuplicado != null
                    && !proveedor.getCodigo().equals("")) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_proveedor_duplicado_codigo"));
            }
        }
        //ID Fiscal
        if (!proveedor.getId_Fiscal().equals("")) {
            Proveedor proveedorDuplicado = this.getProveedorPorId_Fiscal(proveedor.getId_Fiscal(), proveedor.getEmpresa());
            if (operacion.equals(TipoDeOperacion.ACTUALIZACION)
                    && proveedorDuplicado != null
                    && proveedorDuplicado.getId_Proveedor() != proveedor.getId_Proveedor()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_proveedor_duplicado_idFiscal"));
            }
            if (operacion.equals(TipoDeOperacion.ALTA)
                    && proveedorDuplicado != null
                    && !proveedor.getId_Fiscal().equals("")) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_proveedor_duplicado_idFiscal"));
            }
        }
        //Razon social
        Proveedor proveedorDuplicado = this.getProveedorPorRazonSocial(proveedor.getRazonSocial(), proveedor.getEmpresa());
        if (operacion.equals(TipoDeOperacion.ALTA) && proveedorDuplicado != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_proveedor_duplicado_razonSocial"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (proveedorDuplicado != null && proveedorDuplicado.getId_Proveedor() != proveedor.getId_Proveedor()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_proveedor_duplicado_razonSocial"));
            }
        }
    }

    @Override
    @Transactional
    public void guardar(Proveedor proveedor) {
        this.validarOperacion(TipoDeOperacion.ALTA, proveedor);
        proveedorRepository.guardar(proveedor);
        LOGGER.warn("El Proveedor " + proveedor + " se guardó correctamente.");
    }

    @Override
    @Transactional
    public void actualizar(Proveedor proveedor) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, proveedor);
        proveedorRepository.actualizar(proveedor);
    }

    @Override
    @Transactional
    public void eliminar(Proveedor proveedor) {
        proveedor.setEliminado(true);
        proveedorRepository.actualizar(proveedor);
    }
}
