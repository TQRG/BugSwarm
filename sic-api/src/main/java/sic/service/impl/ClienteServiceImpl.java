package sic.service.impl;

import com.querydsl.core.BooleanBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.BusquedaClienteCriteria;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.QCliente;
import sic.service.IClienteService;
import sic.service.BusinessServiceException;
import sic.modelo.TipoDeOperacion;
import sic.util.Validator;
import sic.repository.ClienteRepository;

@Service
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;    
    private static final Logger LOGGER = Logger.getLogger(ClienteServiceImpl.class.getPackage().getName());

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente getClientePorId(Long idCliente) {    
        Cliente cliente = clienteRepository.findOne(idCliente);
        if (cliente == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_no_existente"));
        }
        return cliente;                  
    }

    @Override
    public List<Cliente> getClientes(Empresa empresa) {        
        return clienteRepository.findAllByAndEmpresaAndEliminadoOrderByRazonSocialAsc(empresa, false);                   
    }

    @Override
    public Cliente getClientePorRazonSocial(String razonSocial, Empresa empresa) {        
        return clienteRepository.findByRazonSocialAndEmpresaAndEliminado(razonSocial, empresa, false);                   
    }

    @Override
    public Cliente getClientePorIdFiscal(String idFiscal, Empresa empresa) {        
        return clienteRepository.findByIdFiscalAndEmpresaAndEliminado(idFiscal, empresa, false);               
    }

    @Override
    public Cliente getClientePredeterminado(Empresa empresa) {   
        Cliente cliente = clienteRepository.findByAndEmpresaAndPredeterminadoAndEliminado(empresa, true, false); 
        if (cliente == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_sin_predeterminado"));
        }
        return cliente;                   
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
        Cliente clientePredeterminadoAnterior = clienteRepository.findByAndEmpresaAndPredeterminadoAndEliminado(cliente.getEmpresa(), true, false);
        if (clientePredeterminadoAnterior != null) {
            clientePredeterminadoAnterior.setPredeterminado(false);
            clienteRepository.save(clientePredeterminadoAnterior);
        }
        cliente.setPredeterminado(true);
        clienteRepository.save(cliente);        
    }

    @Override
    public List<Cliente> buscarClientes(BusquedaClienteCriteria criteria) {
        //Empresa
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        if (criteria.getRazonSocial() == null) {
            criteria.setRazonSocial("");
        }
        QCliente qcliente = QCliente.cliente;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qcliente.empresa.eq(criteria.getEmpresa()).and(qcliente.eliminado.eq(false)))
               .and(this.buildPredicadoDescripcion(criteria.getRazonSocial(), qcliente, criteria.isBuscaPorRazonSocial(), criteria.isBuscaPorRazonSocial(), criteria.isBuscaPorRazonSocial()));
        if (criteria.isBuscaPorLocalidad() == true) {
            builder.and(qcliente.localidad.eq(criteria.getLocalidad()));
        }
        if (criteria.isBuscaPorProvincia() == true) {
            builder.and(qcliente.localidad.provincia.eq(criteria.getProvincia()));
        }
        if (criteria.isBuscaPorPais() == true) {
            builder.and(qcliente.localidad.provincia.pais.eq(criteria.getPais()));
        }
        List<Cliente> list = new ArrayList<>();
        clienteRepository.findAll(builder,  new Sort(Sort.Direction.ASC, "razonSocial")).iterator().forEachRemaining(list::add);
        return list;
    }
    
    private BooleanBuilder buildPredicadoDescripcion(String stringRazonSocial, QCliente qcliente, boolean razonSocial, boolean nombreFantasia, boolean idFiscal) {
        String[] terminos = stringRazonSocial.split(" ");
        BooleanBuilder descripcionProducto = new BooleanBuilder();
        if (razonSocial && nombreFantasia && idFiscal) {
            for (String termino : terminos) {
                descripcionProducto.or(qcliente.razonSocial.containsIgnoreCase(termino)
                        .or(qcliente.nombreFantasia.containsIgnoreCase(termino))
                        .or(qcliente.idFiscal.containsIgnoreCase(termino)));
            }
        } else if (razonSocial && nombreFantasia) {
            for (String termino : terminos) {
                descripcionProducto.or(qcliente.razonSocial.containsIgnoreCase(termino)
                        .or(qcliente.nombreFantasia.containsIgnoreCase(termino)));
            }
        } else if (idFiscal) {
            for (String termino : terminos) {
                descripcionProducto.or(qcliente.idFiscal.containsIgnoreCase(termino));
            }
        }
        return descripcionProducto;
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
        if (!cliente.getIdFiscal().equals("")) {
            Cliente clienteDuplicado = this.getClientePorIdFiscal(cliente.getIdFiscal(), cliente.getEmpresa());
            if (operacion.equals(TipoDeOperacion.ACTUALIZACION)
                    && clienteDuplicado != null
                    && clienteDuplicado.getId_Cliente() != cliente.getId_Cliente()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cliente_duplicado_idFiscal"));
            }
            if (operacion.equals(TipoDeOperacion.ALTA)
                    && clienteDuplicado != null
                    && !cliente.getIdFiscal().equals("")) {
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
        cliente = clienteRepository.save(cliente);          
        LOGGER.warn("El Cliente " + cliente + " se guardó correctamente." );
        return cliente;
    }

    @Override
    @Transactional
    public void actualizar(Cliente cliente) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, cliente);        
        clienteRepository.save(cliente);                   
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
        clienteRepository.save(cliente);                   
    }
}
