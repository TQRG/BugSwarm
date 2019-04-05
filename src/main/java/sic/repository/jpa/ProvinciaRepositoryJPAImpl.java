package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.Pais;
import sic.modelo.Provincia;
import sic.repository.IProvinciaRepository;

@Repository
public class ProvinciaRepositoryJPAImpl implements IProvinciaRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Provincia> getProvinciasDelPais(Pais pais) {
        TypedQuery<Provincia> typedQuery = em.createNamedQuery("Provincia.buscarProvinciasDelPais", Provincia.class);
        typedQuery.setParameter("pais", pais);
        List<Provincia> provincias = typedQuery.getResultList();
        return provincias;
    }

    @Override
    public Provincia getProvinciaPorId(Long id_Provincia) {
        TypedQuery<Provincia> typedQuery = em.createNamedQuery("Provincia.buscarPorId", Provincia.class);
        typedQuery.setParameter("id", id_Provincia);
        List<Provincia> provincias = typedQuery.getResultList();
        if (provincias.isEmpty()) {
            return null;
        } else {
            return provincias.get(0);
        }
    }
    
    @Override
    public Provincia getProvinciaPorNombre(String nombreProvincia, Pais paisRelacionado) {
        TypedQuery<Provincia> typedQuery = em.createNamedQuery("Provincia.buscarPorNombre", Provincia.class);
        typedQuery.setParameter("nombre", nombreProvincia);
        typedQuery.setParameter("pais", paisRelacionado);
        List<Provincia> provincias = typedQuery.getResultList();
        if (provincias.isEmpty()) {
            return null;
        } else {
            return provincias.get(0);
        }
    }

    @Override
    public void actualizar(Provincia provincia) {
        em.merge(provincia);
    }

    @Override
    public Provincia guardar(Provincia provincia) {
        provincia = em.merge(provincia);
        em.persist(provincia);
        return provincia;
    }
}
