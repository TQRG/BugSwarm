package sic.repository.jpa;

import sic.repository.ICajaRepository;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.BusquedaCajaCriteria;
import sic.modelo.Caja;
import sic.util.FormatterFechaHora;

@Repository
public class CajaRepositoryJPAImpl implements ICajaRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void guardar(Caja caja) {
        em.persist(em.merge(caja));
    }
    
    @Override
    public Caja getCajaPorId(Long id) {
        TypedQuery<Caja> typedQuery = em.createNamedQuery("Caja.buscarCajaPorId", Caja.class);
        typedQuery.setParameter("id", id);
        List<Caja> cajas = typedQuery.getResultList();
        if (cajas.isEmpty()) {
            return null;
        } else {
            return cajas.get(0);
        }
    }

    @Override
    public Caja getUltimaCaja(long id_Empresa) {
        TypedQuery<Caja> typedQuery = em.createNamedQuery("Caja.getUltimaCaja", Caja.class);
        typedQuery.setParameter("id_Empresa", id_Empresa);
        List<Caja> ControlesCaja = typedQuery.getResultList();
        if (ControlesCaja.isEmpty()) {
            return null;
        } else {
            return ControlesCaja.get(0);
        }
    }

    @Override
    public List<Caja> getCajas(long id_Empresa, Date desde, Date hasta) {
        TypedQuery<Caja> typedQuery = em.createNamedQuery("Caja.getCajas", Caja.class);
        typedQuery.setParameter("id_Empresa", id_Empresa);
        typedQuery.setParameter("desde", desde);
        typedQuery.setParameter("hasta", hasta);
        List<Caja> ControlesCaja = typedQuery.getResultList();
        if (ControlesCaja.isEmpty()) {
            return null;
        } else {
            return ControlesCaja;
        }
    }

    @Override
    public void actualizar(Caja caja) {
        em.merge(caja);
    }

    @Override
    public Caja getCajaPorIdYEmpresa(long id_Caja, long id_Empresa) {
        TypedQuery<Caja> typedQuery = em.createNamedQuery("Caja.buscarCajaPorIdYEmpresa", Caja.class);
        typedQuery.setParameter("id_caja", id_Caja);
        typedQuery.setParameter("id_Empresa", id_Empresa);
        List<Caja> cajas = typedQuery.getResultList();
        if (cajas.isEmpty()) {
            return null;
        } else {
            return cajas.get(0);
        }
    }

    @Override
    public Caja getCajaPorFormaDePago(long idEmpresa, long idFormaDePago) {
        TypedQuery<Caja> typedQuery = em.createNamedQuery("Caja.cajaSinArqueoPorFormaDepago", Caja.class);
        typedQuery.setParameter("id_Empresa", idEmpresa);
        typedQuery.setParameter("id_FormaDePago", idFormaDePago);
        List<Caja> cajas = typedQuery.getResultList();
        if (cajas.isEmpty()) {
            return null;
        } else {
            return cajas.get(0);
        }
    }

    @Override
    public int getUltimoNumeroDeCaja(long idEmpresa) {
        TypedQuery<Integer> typedQuery = em.createNamedQuery("Caja.getUltimoNumeroDeCaja", Integer.class);
        typedQuery.setParameter("id_Empresa", idEmpresa);
        Integer ultimoNumeroDeCaja = typedQuery.getSingleResult();
        if (ultimoNumeroDeCaja == null) {
            return 0;
        } else {
            return ultimoNumeroDeCaja;
        }
    }

    @Override
    public List<Caja> getCajasCriteria(BusquedaCajaCriteria criteria) {
        String query = "SELECT c FROM Caja c WHERE c.empresa = :empresa AND c.eliminada = false";
        if (criteria.isBuscaPorUsuario() == true) {
            query += " AND c.usuarioCierraCaja.id_Usuario = " + criteria.getUsuario().getId_Usuario();
        }
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND c.fechaApertura BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde()) + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        query += " ORDER BY c.fechaApertura DESC";
        TypedQuery<Caja> typedQuery = em.createQuery(query, Caja.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        if (criteria.getCantidadDeRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantidadDeRegistros());
        }
        List<Caja> cajas = typedQuery.getResultList();
        return cajas;
    }
}
