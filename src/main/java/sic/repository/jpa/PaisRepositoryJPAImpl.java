package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.Pais;
import sic.repository.IPaisRepository;

@Repository
public class PaisRepositoryJPAImpl implements IPaisRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Pais> getPaises() {
        TypedQuery<Pais> typedQuery = em.createNamedQuery("Pais.buscarTodos", Pais.class);
        List<Pais> paises = typedQuery.getResultList();
        return paises;
    }

    @Override
    public Pais getPaisPorId(Long id_Pais) {
        TypedQuery<Pais> typedQuery = em.createNamedQuery("Pais.buscarPorId", Pais.class);
        typedQuery.setParameter("id", id_Pais);
        List<Pais> paises = typedQuery.getResultList();
        if (paises.isEmpty()) {
            return null;
        } else {
            return paises.get(0);
        }
    }

    @Override
    public Pais getPaisPorNombre(String nombre) {
        TypedQuery<Pais> typedQuery = em.createNamedQuery("Pais.buscarPorNombre", Pais.class);
        typedQuery.setParameter("nombre", nombre);
        List<Pais> paises = typedQuery.getResultList();
        if (paises.isEmpty()) {
            return null;
        } else {
            return paises.get(0);
        }
    }

    @Override
    public void actualizar(Pais pais) {
        em.merge(pais);
    }

    @Override
    public void guardar(Pais pais) {
        em.persist(pais);
    }
}
