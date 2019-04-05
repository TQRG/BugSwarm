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
    public Gasto getGastoPorId(Long id_Gasto) {
        TypedQuery<Gasto> typedQuery = em.createNamedQuery("Gasto.getGastoPorId", Gasto.class);
        typedQuery.setParameter("id_Gasto", id_Gasto);
        List<Gasto> gastos = typedQuery.getResultList();
        if (gastos.isEmpty()) {
            return null;
        } else {
            return gastos.get(0);
        }
    }

    @Override
    public Gasto guardar(Gasto gasto) {
        gasto = em.merge(gasto);
        em.persist(gasto);
        return gasto;
    }

    @Override
    public List<Gasto> getGastosPorFecha(long id_Empresa, Date desde, Date hasta) {
        TypedQuery<Gasto> typedQuery = em.createNamedQuery("Gasto.getGastosSinArqueoPorFecha", Gasto.class);
        typedQuery.setParameter("id_Empresa", id_Empresa);
        typedQuery.setParameter("desde", desde);
        typedQuery.setParameter("hasta", hasta);
        List<Gasto> gastos = typedQuery.getResultList();
        if (gastos.isEmpty()) {
            return null;
        } else {
            return gastos;
        }
    }

    @Override
    public List<Gasto> getGastosPorFechaYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta) {
        TypedQuery<Gasto> typedQuery = em.createNamedQuery("Gasto.getGastosSinArqueoPorFormaDePagoYFecha", Gasto.class);
        typedQuery.setParameter("id_Empresa", id_Empresa);
        typedQuery.setParameter("id_FormaDePago", id_FormaDePago);
        typedQuery.setParameter("desde", desde);
        typedQuery.setParameter("hasta", hasta);
        List<Gasto> gastos = typedQuery.getResultList();
        return gastos;
    }

    @Override
    public void actualizar(Gasto gasto) {
        em.merge(gasto);
    }

    @Override
    public Gasto getGastoPorIdYEmpresa(long id_Gasto, long id_Empresa) {
        TypedQuery<Gasto> typedQuery = em.createNamedQuery("Gasto.getGastoPorIdYEmpresa", Gasto.class);
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
    public Gasto getGastoPorNroYEmpresa(long nroGasto, long id_Empresa) {
        TypedQuery<Gasto> typedQuery = em.createNamedQuery("Gasto.getGastoPorNroYEmpresa", Gasto.class);
        typedQuery.setParameter("nroGasto", nroGasto);
        typedQuery.setParameter("id_Empresa", id_Empresa);
        List<Gasto> gastos = typedQuery.getResultList();
        if (gastos.isEmpty()) {
            return null;
        } else {
            return gastos.get(0);
        }
    }

    @Override
    public long getUltimoNumeroDeGasto(long idEmpresa) {
        TypedQuery<Long> typedQuery = em.createNamedQuery("Gasto.getUltimoNumeroDeGasto", Long.class);
        typedQuery.setParameter("id_Empresa", idEmpresa);
        Long ultimoNumeroDeGasto = typedQuery.getSingleResult();
        if (ultimoNumeroDeGasto == null) {
            return 0;
        } else {
            return ultimoNumeroDeGasto;
        }
    }

}
