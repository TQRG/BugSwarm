package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.Empresa;
import sic.modelo.FormaDePago;
import sic.repository.IFormaDePagoRepository;

@Repository
public class FormaDePagoRepositoryJPAImpl implements IFormaDePagoRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<FormaDePago> getFormasDePago(Empresa empresa) {
        TypedQuery<FormaDePago> typedQuery = em.createNamedQuery("FormaDePago.buscarTodas", FormaDePago.class);
        typedQuery.setParameter("empresa", empresa);
        List<FormaDePago> formasDePago = typedQuery.getResultList();
        return formasDePago;
    }

    @Override
    public FormaDePago getFormaDePagoPorId(long id) {
        TypedQuery<FormaDePago> typedQuery = em.createNamedQuery("FormaDePago.buscarPorId", FormaDePago.class);
        typedQuery.setParameter("id", id);
        List<FormaDePago> formasDePago = typedQuery.getResultList();
        if (formasDePago.isEmpty()) {
            return null;
        } else {
            return formasDePago.get(0);
        }
    }

    @Override
    public FormaDePago getFormaDePagoPredeterminado(Empresa empresa) {
        TypedQuery<FormaDePago> typedQuery = em.createNamedQuery("FormaDePago.buscarPredeterminada", FormaDePago.class);
        typedQuery.setParameter("empresa", empresa);
        List<FormaDePago> formasDePago = typedQuery.getResultList();
        if (formasDePago.isEmpty()) {
            return null;
        } else {
            return formasDePago.get(0);
        }
    }

    @Override
    public FormaDePago getFormaDePagoPorNombreYEmpresa(String nombre, Long idEmpresa) {
        TypedQuery<FormaDePago> typedQuery = em.createNamedQuery("FormaDePago.buscarPorNombre", FormaDePago.class);
        typedQuery.setParameter("nombre", nombre);
        typedQuery.setParameter("idEmpresa", idEmpresa);
        List<FormaDePago> formasDePago = typedQuery.getResultList();
        if (formasDePago.isEmpty()) {
            return null;
        } else {
            return formasDePago.get(0);
        }
    }

    @Override
    public FormaDePago guardar(FormaDePago formaDePago) {
        formaDePago = em.merge(formaDePago);
        em.persist(formaDePago);
        return formaDePago;
    }

    @Override    
    public void actualizar(FormaDePago formaDePago) {
        em.merge(formaDePago);
    }
}
