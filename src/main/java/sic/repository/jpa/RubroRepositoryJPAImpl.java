package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.Empresa;
import sic.modelo.Rubro;
import sic.repository.IRubroRepository;

@Repository
public class RubroRepositoryJPAImpl implements IRubroRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Rubro getRubroPorId(Long id_Rubro) {
        TypedQuery<Rubro> typedQuery = em.createNamedQuery("Rubro.buscarPorId", Rubro.class);
        typedQuery.setParameter("id", id_Rubro);
        List<Rubro> rubros = typedQuery.getResultList();
        if (rubros.isEmpty()) {
            return null;
        } else {
            return rubros.get(0);
        }
    }
    
    @Override
    public List<Rubro> getRubros(Empresa empresa) {
        TypedQuery<Rubro> typedQuery = em.createNamedQuery("Rubro.buscarTodos", Rubro.class);
        typedQuery.setParameter("empresa", empresa);
        List<Rubro> rubros = typedQuery.getResultList();
        return rubros;
    }

    @Override
    public Rubro getRubroPorNombre(String nombre, Empresa empresa) {
        TypedQuery<Rubro> typedQuery = em.createNamedQuery("Rubro.buscarPorNombre", Rubro.class);
        typedQuery.setParameter("nombre", nombre);
        typedQuery.setParameter("empresa", empresa);
        List<Rubro> rubros = typedQuery.getResultList();
        if (rubros.isEmpty()) {
            return null;
        } else {
            return rubros.get(0);
        }
    }

    @Override
    public void guardar(Rubro rubro) {
        em.persist(em.merge(rubro));
    }

    @Override
    public void actualizar(Rubro rubro) {
        em.merge(rubro);
    }
}
