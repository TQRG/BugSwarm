package sic.repository.jpa;

import sic.repository.IGastoRepository;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.Gasto;

@Repository
public class GastoRepositoryJPAImpl implements IGastoRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(Gasto gasto) {
        em.persist(em.merge(gasto));
    }

    @Override
    public List<Object> getGastosPorFecha(long id_Empresa, Date desde, Date hasta) {
        TypedQuery<Object> typedQuery = (TypedQuery<Object>) em.createNamedQuery("Gasto.getGastosSinArqueoPorFecha");
        typedQuery.setParameter("id_Empresa", id_Empresa);
        typedQuery.setParameter("desde", desde);
        typedQuery.setParameter("hasta", hasta);
        List<Object> gastos = typedQuery.getResultList();
        if (gastos.isEmpty()) {
            return null;
        } else {
            return gastos;
        }
    }

    @Override
    public List<Object> getGastosPorFechaYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta) {
        TypedQuery<Object> typedQuery = (TypedQuery<Object>) em.createNamedQuery("Gasto.getGastosSinArqueoPorFormaDePagoYFecha");
        typedQuery.setParameter("id_Empresa", id_Empresa);
        typedQuery.setParameter("id_FormaDePago", id_FormaDePago);
        typedQuery.setParameter("desde", desde);
        typedQuery.setParameter("hasta", hasta);
        List<Object> gastos = typedQuery.getResultList();
        return gastos;
    }

    @Override
    public void actualizar(Gasto gasto) {
        em.merge(gasto);
    }

    @Override
    public Gasto getCajaPorID(long id_Gasto, long id_Empresa) {
        TypedQuery<Gasto> typedQuery = em.createNamedQuery("Gasto.getGastoPorId", Gasto.class);
        typedQuery.setParameter("id_Gasto", id_Gasto);
        typedQuery.setParameter("id_Empresa", id_Empresa);
        List<Gasto> gastos = typedQuery.getResultList();
        if (gastos.isEmpty()) {
            return null;
        } else {
            return gastos.get(0);
        }
    }

    @Override
    public int getUltimoNumeroDeGasto(long idEmpresa) {
        TypedQuery<Integer> typedQuery = em.createNamedQuery("Gasto.getUltimoNumeroDeGasto", Integer.class);
        typedQuery.setParameter("id_Empresa", idEmpresa);
        Integer ultimoNumeroDeGasto = typedQuery.getSingleResult();
        if (ultimoNumeroDeGasto == null) {
            return 0;
        } else {
            return ultimoNumeroDeGasto;
        }
    }

}
