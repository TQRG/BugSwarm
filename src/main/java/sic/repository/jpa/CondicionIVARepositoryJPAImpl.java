package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.CondicionIVA;
import sic.repository.ICondicionIVARepository;

@Repository
public class CondicionIVARepositoryJPAImpl implements ICondicionIVARepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CondicionIVA> getCondicionesIVA() {
        TypedQuery<CondicionIVA> typedQuery = em.createNamedQuery("CondicionIVA.buscarTodas", CondicionIVA.class);
        List<CondicionIVA> condicionesIVA = typedQuery.getResultList();
        return condicionesIVA;
    }

    @Override
    public CondicionIVA getCondicionIVAPorNombre(String nombre) {
        TypedQuery<CondicionIVA> typedQuery = em.createNamedQuery("CondicionIVA.buscarPorNombre", CondicionIVA.class);
        typedQuery.setParameter("nombre", nombre);
        List<CondicionIVA> condicionesIVA = typedQuery.getResultList();
        if (condicionesIVA.isEmpty()) {
            return null;
        } else {
            return condicionesIVA.get(0);
        }
    }

    @Override
    public void actualizar(CondicionIVA condicionIVA) {
        em.merge(condicionIVA);
    }

    @Override
    public void guardar(CondicionIVA condicionIVA) {
        em.persist(condicionIVA);
    }
}
