package sic.service.impl;

import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.BusquedaClienteCriteria;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.repository.IClienteRepository;
import sic.service.IClienteService;
import sic.service.BusinessServiceException;
import sic.modelo.TipoDeOperacion;
import sic.util.Validator;

@Service
public class ClienteServiceImpl implements IClienteService {

    private final IClienteRepository clienteRepository;    
    private static final Logger LOGGER = Logger.getLogger(ClienteServiceImpl.class.getPackage().getName());

    @Autowired
    public ClienteServiceImpl(IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente getClientePorId(Long idCliente) {    
        Cliente cliente = clienteRepository.getClientePorId(idCliente);
        if (cliente == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_no_existente"));
        }
        return cliente;                  
    }

    @Override
    public List<Cliente> getClientes(Empresa empresa) {        
        return clienteRepository.getClientes(empresa);                   
    }

    @Override
    public List<Cliente> getClientesQueContengaRazonSocialNombreFantasiaIdFiscal(String criteria, Empresa empresa) {        
        return clienteRepository.getClientesQueContengaRazonSocialNombreFantasiaIdFiscal(criteria, empresa);                   
    }

    @Override
    public Cliente getClientePorRazonSocial(String razonSocial, Empresa empresa) {        
        return clienteRepository.getClientePorRazonSocial(razonSocial, empresa);                   
    }

    @Override
    public Cliente getClientePorIdFiscal(String idFiscal, Empresa empresa) {        
        return clienteRepository.getClientePorId_Fiscal(idFiscal, empresa);               
    }

    @Override
    public Cliente getClientePredeterminado(Empresa empresa) {        
        return clienteRepository.getClientePredeterminado(empresa);                   
    }

    /**
     * Establece el @cliente pasado como parametro como predeterminado. Antes de
     * establecer el cliente como predeterminado, verifica si ya existe otro como
     * predeterminado y cambia su estado.
     *
     * @param cliente Cliente candidato a predeterminado.
     */
    @Override
    @Transactional
    public void setClientePredeterminado(Cliente cliente) {        
        Cliente clientePredeterminadoAnterior = clienteRepository.getClientePredeterminado(cliente.getEmpresa());
        if (clientePredeterminadoAnterior != null) {
            clientePredeterminadoAnterior.setPredeterminado(false);
            clienteRepository.actualizar(clientePredeterminadoAnterior);
        }
        cliente.setPredeterminado(true);
        clienteRepository.actualizar(cliente);        
    }

    @Override
    public List<Cliente> buscarClientes(BusquedaClienteCriteria criteria) {
        //Empresa
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        return clienteRepository.buscarClientes(criteria);
    }

    @Override
    public void validarOperacion(TipoDeOperacion operacion, Cliente cliente) {
        //Entrada de Datos
        if (!"".equals(cliente.getEmail())) {
            if (!Validator.esEmailValido(cliente.getEmail())) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cliente_email_invalido"));
            }
        }
        //Requeridos        
        if (Validator.esVacio(cliente.getRazonSocial())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_vacio_razonSocial"));
        }
        if (cliente.getCondicionIVA() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_vacio_condicionIVA"));
        }
        if (cliente.getLocalidad() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_vacio_localidad"));
        }
        if (cliente.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_vacio_empresa"));
        }
        //Duplicados
        //ID Fiscal
        if (!cliente.getId_Fiscal().equals("")) {
            Cliente clienteDuplicado = this.getClientePorIdFiscal(cliente.getId_Fiscal(), cliente.getEmpresa());
            if (operacion.equals(TipoDeOperacion.ACTUALIZACION)
                    && clienteDuplicado != null
                    && clienteDuplicado.getId_Cliente() != cliente.getId_Cliente()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cliente_duplicado_idFiscal"));
            }
            if (operacion.equals(TipoDeOperacion.ALTA)
                    && clienteDuplicado != null
                    && !cliente.getId_Fiscal().equals("")) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cliente_duplicado_idFiscal"));
            }
        }
        //Razon Social
        Cliente clienteDuplicado = this.getClientePorRazonSocial(cliente.getRazonSocial(), cliente.getEmpresa());
        if (operacion.equals(TipoDeOperacion.ALTA) && clienteDuplicado != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_duplicado_razonSocial"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (clienteDuplicado != null && clienteDuplicado.getId_Cliente() != cliente.getId_Cliente()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cliente_duplicado_razonSocial"));
            }
        }
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {        
        this.validarOperacion(TipoDeOperacion.ALTA, cliente);
        cliente = clienteRepository.guardar(cliente);          
        LOGGER.warn("El Cliente " + cliente + " se guardó correctamente." );
        return cliente;
    }

    @Override
    @Transactional
    public void actualizar(Cliente cliente) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, cliente);        
        clienteRepository.actualizar(cliente);                   
    }

    @Override
    @Transactional
    public void eliminar(Long idCliente) {
        Cliente cliente = this.getClientePorId(idCliente);
        if (cliente == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_no_existente"));
        }
        cliente.setEliminado(true);        
        clienteRepository.actualizar(cliente);                   
    }
}
