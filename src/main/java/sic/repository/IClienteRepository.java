package sic.repository;

import java.util.List;
import sic.modelo.BusquedaClienteCriteria;
import sic.modelo.Cliente;
import sic.modelo.Empresa;

public interface IClienteRepository {

    void actualizar(Cliente cliente);

    List<Cliente> buscarClientes(BusquedaClienteCriteria criteria);

    Cliente getClientePorId(Long id_Cliente);

    Cliente getClientePorId_Fiscal(String id_Fiscal, Empresa empresa);

    Cliente getClientePorRazonSocial(String razonSocial, Empresa empresa);

    Cliente getClientePredeterminado(Empresa empresa);

    List<Cliente> getClientes(Empresa empresa);

    List<Cliente> getClientesQueContengaRazonSocialNombreFantasiaIdFiscal(String criteria, Empresa empresa);

    void guardar(Cliente cliente);
    
}
