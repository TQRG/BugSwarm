package sic.repository.jpa;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Factura;
import sic.modelo.Pago;
import sic.repository.IPagoRepository;

@Repository
public class PagoRepositoryJPAImpl implements IPagoRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Pago> getPagosDeLaFactura(Factura factura) {
        TypedQuery<Pago> typedQuery = em.createNamedQuery("Pago.buscarPorFactura", Pago.class);
        typedQuery.setParameter("factura", factura);
        List<Pago> pagosFacturaCompra = typedQuery.getResultList();
        return pagosFacturaCompra;
    }

    @Override
    public Pago getPagoPorId(long id_Pago) {
        TypedQuery<Pago> typedQuery = em.createNamedQuery("Pago.buscarPorId", Pago.class);
        typedQuery.setParameter("id", id_Pago);
        List<Pago> pagos = typedQuery.getResultList();
         if (pagos.isEmpty()) {
            return null;
        } else {
            return pagos.get(0);
        }
    }
    
    @Override
    public List<Pago> getPagosEntreFechasYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta) {
        TypedQuery<Pago> typedQuery = em.createNamedQuery("Pago.buscarPagosEntreFechasYFormaDePago", Pago.class);
        typedQuery.setParameter("id_Empresa", id_Empresa);
        typedQuery.setParameter("id_FormaDePago", id_FormaDePago);
        typedQuery.setParameter("desde", desde);
        typedQuery.setParameter("hasta", hasta);
        List<Pago> Pagos = typedQuery.getResultList();
        return Pagos;
    }
    @Override
    @Transactional
    public void guardar(Pago pago) {
        em.persist(em.merge(pago));
    }

    @Override
    @Transactional
    public void actualizar(Pago pago) {
        em.merge(pago);
    }
}
