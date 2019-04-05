package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.BusquedaProveedorCriteria;
import sic.modelo.Empresa;
import sic.modelo.Proveedor;
import sic.repository.IProveedorRepository;

@Repository
public class ProveedorRepositoryJPAImpl implements IProveedorRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Proveedor> getProveedores(Empresa empresa) {
        TypedQuery<Proveedor> typedQuery = em.createNamedQuery("Proveedor.buscarTodos", Proveedor.class);
        typedQuery.setParameter("empresa", empresa);
        List<Proveedor> proveedores = typedQuery.getResultList();
        return proveedores;
    }

    @Override
    public Proveedor getProveedorPorId(Long id_Proveedor) {
        TypedQuery<Proveedor> typedQuery = em.createNamedQuery("Proveedor.buscarPorId", Proveedor.class);
        typedQuery.setParameter("id", id_Proveedor);
        List<Proveedor> proveedores = typedQuery.getResultList();
        if (proveedores.isEmpty()) {
            return null;
        } else {
            return proveedores.get(0);
        }
    }
    
    @Override
    public Proveedor getProveedorPorCodigo(String codigo, Empresa empresa) {
        TypedQuery<Proveedor> typedQuery = em.createNamedQuery("Proveedor.buscarPorCodigo", Proveedor.class);
        typedQuery.setParameter("codigo", codigo);
        typedQuery.setParameter("empresa", empresa);
        List<Proveedor> proveedores = typedQuery.getResultList();
        if (proveedores.isEmpty()) {
            return null;
        } else {
            return proveedores.get(0);
        }
    }

    @Override
    public Proveedor getProveedorPorRazonSocial(String razonSocial, Empresa empresa) {
        TypedQuery<Proveedor> typedQuery = em.createNamedQuery("Proveedor.buscarPorRazonSocial", Proveedor.class);
        typedQuery.setParameter("razonSocial", razonSocial);
        typedQuery.setParameter("empresa", empresa);
        List<Proveedor> proveedores = typedQuery.getResultList();
        if (proveedores.isEmpty()) {
            return null;
        } else {
            return proveedores.get(0);
        }
    }

    @Override
    public Proveedor getProveedorPorId_Fiscal(String id_Fiscal, Empresa empresa) {
        TypedQuery<Proveedor> typedQuery = em.createNamedQuery("Proveedor.buscarPorIdFiscal", Proveedor.class);
        typedQuery.setParameter("idFiscal", id_Fiscal);
        typedQuery.setParameter("empresa", empresa);
        List<Proveedor> proveedores = typedQuery.getResultList();
        if (proveedores.isEmpty()) {
            return null;
        } else {
            return proveedores.get(0);
        }
    }

    @Override
    public List<Proveedor> buscarProveedores(BusquedaProveedorCriteria criteria) {
        String query = "SELECT p FROM Proveedor p WHERE p.empresa = :empresa AND p.eliminado = false";
        //Razon Social
        if (criteria.isBuscaPorRazonSocial() == true) {
            String[] terminos = criteria.getRazonSocial().split(" ");
            for (int i = 0; i < terminos.length; i++) {
                query += " AND p.razonSocial LIKE '%" + terminos[i] + "%'";
            }
        }
        //Id_Fiscal
        if (criteria.isBuscaPorId_Fiscal() == true) {
            query = query + " AND p.id_Fiscal = '" + criteria.getId_Fiscal() + "'";
        }
        //Codigo
        if (criteria.isBuscaPorCodigo() == true) {
            query = query + " AND p.codigo = '" + criteria.getCodigo() + "'";
        }
        //Localidad
        if (criteria.isBuscaPorLocalidad() == true) {
            query = query + " AND p.localidad = " + criteria.getLocalidad().getId_Localidad();
        }
        //Provincia
        if (criteria.isBuscaPorProvincia() == true) {
            query = query + " AND p.localidad.provincia = " + criteria.getProvincia().getId_Provincia();
        }
        //Pais
        if (criteria.isBuscaPorPais() == true) {
            query = query + " AND p.localidad.provincia.pais = " + criteria.getPais().getId_Pais();
        }
        query = query + " ORDER BY p.razonSocial ASC";
        TypedQuery<Proveedor> typedQuery = em.createQuery(query, Proveedor.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        List<Proveedor> proveedores = typedQuery.getResultList();
        return proveedores;
    }

    @Override
    public void guardar(Proveedor proveedor) {
        em.persist(em.merge(proveedor));
    }

    @Override
    public void actualizar(Proveedor proveedor) {
        em.merge(proveedor);
    }
}
