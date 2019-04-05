package sic.service;

import java.util.List;
import sic.modelo.BusquedaClienteCriteria;
import sic.modelo.Cliente;
import sic.modelo.Empresa;

public interface IClienteService {

    void actualizar(Cliente cliente);
        
    List<Cliente> buscarClientes(BusquedaClienteCriteria criteria);

    void eliminar(Cliente cliente);

    Cliente getClientePorId(Long id_Cliente);

    Cliente getClientePorIdFiscal(String idFiscal, Empresa empresa);

    Cliente getClientePorRazonSocial(String razonSocial, Empresa empresa);

    Cliente getClientePredeterminado(Empresa empresa);

    List<Cliente> getClientes(Empresa empresa);

    List<Cliente> getClientesQueContengaRazonSocialNombreFantasiaIdFiscal(String criteria, Empresa empresa);

    void guardar(Cliente cliente);

    void setClientePredeterminado(Cliente cliente);

    void validarOperacion(TipoDeOperacion operacion, Cliente cliente);

}
