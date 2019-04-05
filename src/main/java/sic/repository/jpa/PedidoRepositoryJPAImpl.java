package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.BusquedaPedidoCriteria;
import sic.modelo.Pedido;
import sic.modelo.RenglonPedido;
import sic.repository.IPedidoRepository;
import sic.util.FormatterFechaHora;

@Repository
public class PedidoRepositoryJPAImpl implements IPedidoRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<RenglonPedido> getRenglonesDelPedido(long nroPedido) {
        TypedQuery<Pedido> typedQuery = em.createNamedQuery("Pedido.buscarRenglonesDelPedido", Pedido.class);
        typedQuery.setParameter("nroPedido", nroPedido);
        List<Pedido> paraObtenerRenglones = typedQuery.getResultList();
        em.close();
        return paraObtenerRenglones.get(0).getRenglones();
    }

    @Override
    public List<Pedido> buscarPedidosPorCriteria(BusquedaPedidoCriteria criteria) {
        String query = "SELECT p FROM Pedido p WHERE p.empresa = :empresa AND p.eliminado = false";
        //Fecha del pedido
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND p.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde()) + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Cliente
        if (criteria.isBuscaCliente() == true) {
            query += " AND p.cliente = " + criteria.getCliente().getId_Cliente();
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true) {
            query += " AND p.usuario = " + criteria.getUsuario().getId_Usuario();
        }
        //Nro de Pedido
        if (criteria.isBuscaPorNroPedido() == true) {
            query += " AND p.nroPedido = " + criteria.getNroPedido();
        }
        query += " ORDER BY p.fecha DESC";
        
        TypedQuery<Pedido> typedQuery = em.createQuery(query, Pedido.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        List<Pedido> pedidos = typedQuery.getResultList();
        em.close();
        return pedidos;
    }

    @Override
    public long buscarMayorNroPedido(long idEmpresa) {
        TypedQuery<Long> typedQuery = em.createNamedQuery("Pedido.buscarMayorNroPedido", Long.class);
        typedQuery.setParameter("idEmpresa", idEmpresa);
        Long resultado = typedQuery.getSingleResult();
        em.close();
        if (resultado == null) {
            return 0;
        } else {
            return resultado;
        }
    }

    @Override
    public void guardar(Pedido pedido) {
        em.persist(em.merge(pedido));
    }

    @Override
    public Pedido getPedidoPorNro(long nroPedido, long idEmpresa) {
        TypedQuery<Pedido> typedQuery = em.createNamedQuery("Pedido.buscarPorNumero", Pedido.class);
        typedQuery.setParameter("idEmpresa", idEmpresa);
        typedQuery.setParameter("nroPedido", nroPedido);
        List<Pedido> pedidos = typedQuery.getResultList();
        em.close();
        if (pedidos.isEmpty()) {
            return null;
        } else {
            return pedidos.get(0);
        }
    }

    @Override
    public void actualizar(Pedido pedido) {
        em.merge(pedido);
    }

    @Override
    public Pedido getPedidoPorNumeroConFacturas(long nroPedido) {
        TypedQuery<Pedido> typedQuery = em.createNamedQuery("Pedido.buscarPorNumeroConFacturas", Pedido.class);
        typedQuery.setParameter("nroPedido", nroPedido);
        List<Pedido> pedidos = typedQuery.getResultList();
        em.close();
        if (pedidos.isEmpty()) {
            return null;
        } else {
            return pedidos.get(0);
        }
    }

    @Override
    public Pedido getPedidoPorNumeroConRenglones(long nroPedido, long idEmpresa) {
        TypedQuery<Pedido> typedQuery = em.createNamedQuery("Pedido.buscarPorNumeroConRenglones", Pedido.class);
        typedQuery.setParameter("nroPedido", nroPedido);
        typedQuery.setParameter("idEmpresa", idEmpresa);
        List<Pedido> pedidos = typedQuery.getResultList();
        em.close();
        if (pedidos.isEmpty()) {
            return null;
        } else {
            return pedidos.get(0);
        }
    }
}
