package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.BusquedaTransportistaCriteria;
import sic.modelo.Empresa;
import sic.modelo.Transportista;
import sic.repository.ITransportistaRepository;

@Repository
public class TransportistaRepositoryJPAImpl implements ITransportistaRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Transportista> getTransportistas(Empresa empresa) {
        TypedQuery<Transportista> typedQuery = em.createNamedQuery("Transportista.buscarTodos", Transportista.class);
        typedQuery.setParameter("empresa", empresa);
        List<Transportista> transportistas = typedQuery.getResultList();
        return transportistas;
    }
    
    @Override
    public Transportista getTransportistaPorId(long id_Transportista) {
        TypedQuery<Transportista> typedQuery = em.createNamedQuery("Transportista.buscarPorId", Transportista.class);
        typedQuery.setParameter("id", id_Transportista);
        List<Transportista> transportistas = typedQuery.getResultList();
        if (transportistas.isEmpty()) {
            return null;
        } else {
            return transportistas.get(0);
        }
    }

    @Override
    public Transportista getTransportistaPorNombre(String nombre, Empresa empresa) {
        TypedQuery<Transportista> typedQuery = em.createNamedQuery("Transportista.buscarPorNombre", Transportista.class);
        typedQuery.setParameter("nombre", nombre);
        typedQuery.setParameter("empresa", empresa);
        List<Transportista> transportistas = typedQuery.getResultList();
        if (transportistas.isEmpty()) {
            return null;
        } else {
            return transportistas.get(0);
        }
    }

    @Override
    public List<Transportista> busquedaPersonalizada(BusquedaTransportistaCriteria criteria) {
        String query = "SELECT t FROM Transportista t WHERE t.empresa = :empresa AND t.eliminado = false";
        //Nombre
        if (criteria.isBuscarPorNombre() == true) {
            String[] terminos = criteria.getNombre().split(" ");
            for (int i = 0; i < terminos.length; i++) {
                query += " AND t.nombre LIKE '%" + terminos[i] + "%'";
            }
        }
        //Localidad
        if (criteria.isBuscarPorLocalidad() == true) {
            query = query + " AND t.localidad = " + criteria.getLocalidad().getId_Localidad();
        }
        //Provincia
        if (criteria.isBuscarPorProvincia() == true) {
            query = query + " AND t.localidad.provincia = " + criteria.getProvincia().getId_Provincia();
        }
        //Pais
        if (criteria.isBuscarPorPais() == true) {
            query = query + " AND t.localidad.provincia.pais = " + criteria.getPais().getId_Pais();
        }
        query = query + " ORDER BY t.nombre ASC";
        TypedQuery<Transportista> typedQuery = em.createQuery(query, Transportista.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        List<Transportista> transportistas = typedQuery.getResultList();
        return transportistas;
    }

    @Override
    public void guardar(Transportista transportista) {
        em.persist(em.merge(transportista));
    }

    @Override
    public void actualizar(Transportista transportista) {
        em.merge(transportista);
    }
}
