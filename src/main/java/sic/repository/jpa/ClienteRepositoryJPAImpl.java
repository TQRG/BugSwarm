package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.BusquedaClienteCriteria;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.repository.IClienteRepository;

@Repository
public class ClienteRepositoryJPAImpl implements IClienteRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Cliente> getClientes(Empresa empresa) {
        TypedQuery<Cliente> typedQuery = em.createNamedQuery("Cliente.buscarTodos", Cliente.class);
        typedQuery.setParameter("empresa", empresa);
        List<Cliente> clientes = typedQuery.getResultList();        
        return clientes;
    }

    @Override
    public Cliente getClientePorId(long id_Cliente) {        
        TypedQuery<Cliente> typedQuery = em.createNamedQuery("Cliente.buscarPorId", Cliente.class);
        typedQuery.setParameter("id", id_Cliente);
        List<Cliente> clientes = typedQuery.getResultList();
        if (clientes.isEmpty()) {
            return null;
        } else {
            return clientes.get(0);
        }
    }

    @Override
    public Cliente getClientePorRazonSocial(String razonSocial, Empresa empresa) {
        TypedQuery<Cliente> typedQuery = em.createNamedQuery("Cliente.buscarPorRazonSocial", Cliente.class);
        typedQuery.setParameter("razonSocial", razonSocial);
        typedQuery.setParameter("empresa", empresa);
        List<Cliente> clientes = typedQuery.getResultList();
        if (clientes.isEmpty()) {
            return null;
        } else {
            return clientes.get(0);
        }
    }

    @Override
    public List<Cliente> getClientesQueContengaRazonSocialNombreFantasiaIdFiscal(String criteria, Empresa empresa) {
        TypedQuery<Cliente> typedQuery = em.createNamedQuery("Cliente.buscarQueContengaRazonSocialNombreFantasiaIdFiscal", Cliente.class);
        typedQuery.setParameter("empresa", empresa);
        typedQuery.setParameter("criteria", "%" + criteria + "%");
        List<Cliente> clientes = typedQuery.getResultList();
        return clientes;
    }

    @Override
    public Cliente getClientePorId_Fiscal(String id_Fiscal, Empresa empresa) {
        TypedQuery<Cliente> typedQuery = em.createNamedQuery("Cliente.buscarPorIdFiscal", Cliente.class);
        typedQuery.setParameter("id_Fiscal", id_Fiscal);
        typedQuery.setParameter("empresa", empresa);
        List<Cliente> clientes = typedQuery.getResultList();
        if (clientes.isEmpty()) {
            return null;
        } else {
            return clientes.get(0);
        }
    }

    @Override
    public Cliente getClientePredeterminado(Empresa empresa) {
        TypedQuery<Cliente> typedQuery = em.createNamedQuery("Cliente.buscarPredeterminado", Cliente.class);
        typedQuery.setParameter("empresa", empresa);
        List<Cliente> clientes = typedQuery.getResultList();
        if (clientes.isEmpty()) {
            return null;
        } else {
            return clientes.get(0);
        }
    }

    @Override
    public List<Cliente> buscarClientes(BusquedaClienteCriteria criteria) {
        String query = "SELECT c FROM Cliente c WHERE c.empresa = :empresa AND c.eliminado = false";

        //OR entre razonSocial y nombreFantasia
        if (criteria.isBuscaPorRazonSocial() && criteria.isBuscaPorNombreFantasia()) {
            String[] terminos = criteria.getRazonSocial().split(" ");
            for (String termino : terminos) {
                query += " AND (c.razonSocial LIKE '%" + termino + "%'" + " OR c.nombreFantasia LIKE '%" + termino + "%')";
            }
        } else {
            if (criteria.isBuscaPorRazonSocial() == true) {
                String[] terminos = criteria.getRazonSocial().split(" ");
                for (String termino : terminos) {
                    query += " AND c.razonSocial LIKE '%" + termino + "%'";
                }
            }
            if (criteria.isBuscaPorNombreFantasia() == true) {
                String[] terminos = criteria.getNombreFantasia().split(" ");
                for (String termino : terminos) {
                    query += " AND c.nombreFantasia LIKE '%" + termino + "%'";
                }
            }
        }
        if (criteria.isBuscaPorId_Fiscal() == true) {
            query = query + " AND c.id_Fiscal = '" + criteria.getId_Fiscal() + "'";
        }
        if (criteria.isBuscaPorLocalidad() == true) {
            query = query + " AND c.localidad = " + criteria.getLocalidad().getId_Localidad();
        }
        if (criteria.isBuscaPorProvincia() == true) {
            query = query + " AND c.localidad.provincia = " + criteria.getProvincia().getId_Provincia();
        }
        if (criteria.isBuscaPorPais() == true) {
            query = query + " AND c.localidad.provincia.pais = " + criteria.getPais().getId_Pais();
        }
        TypedQuery<Cliente> typedQuery = em.createQuery(query, Cliente.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        List<Cliente> clientes = typedQuery.getResultList();
        return clientes;
    }

    @Override
    public void guardar(Cliente cliente) {
        em.persist(em.merge(cliente));
    }

    @Override
    public void actualizar(Cliente cliente) {
        em.merge(cliente);
    }
}
