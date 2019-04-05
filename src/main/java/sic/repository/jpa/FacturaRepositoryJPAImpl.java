package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.repository.IFacturaRepository;
import sic.util.FormatterFechaHora;

@Repository
public class FacturaRepositoryJPAImpl implements IFacturaRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Factura getFacturaPorId(Long id_Factura) {
        TypedQuery<Factura> typedQuery = em.createNamedQuery("Factura.buscarPorId", Factura.class);
        typedQuery.setParameter("id", id_Factura);
        List<Factura> facturas = typedQuery.getResultList();
        if (facturas.isEmpty()) {
            return null;
        } else {
            return facturas.get(0);
        }
    }

    @Override
    public Factura getFacturaPorTipoSerieNum(char tipo, long serie, long num, long idEmpresa) {
        TypedQuery<Factura> typedQuery = em.createNamedQuery("Factura.buscarPorTipoSerieNum", Factura.class);
        typedQuery.setParameter("tipo", tipo);
        typedQuery.setParameter("serie", serie);
        typedQuery.setParameter("num", num);
        typedQuery.setParameter("idEmpresa", idEmpresa);
        List<Factura> facturas = typedQuery.getResultList();
        if (facturas.isEmpty()) {
            return null;
        } else {
            return facturas.get(0);
        }
    }

    @Override
    public List<Factura> getFacturasDelPedido(Long idPedido) {
        TypedQuery<Factura> typedQuery = em.createNamedQuery("Factura.relacionadasConPedido", Factura.class);
        typedQuery.setParameter("id", idPedido);
        List<Factura> facturas = typedQuery.getResultList();
        if (facturas.isEmpty()) {
            return null;
        } else {
            return facturas;
        }
    }
    
    @Override
    public List<FacturaCompra> buscarFacturasCompra(BusquedaFacturaCompraCriteria criteria) {
        String query = "SELECT f FROM FacturaCompra f WHERE f.empresa = :empresa AND f.eliminada = false";
        //Fecha Factura
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde()) + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Proveedor
        if (criteria.isBuscaPorProveedor() == true) {
            query += " AND f.proveedor = " + criteria.getProveedor().getId_Proveedor();
        }
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Inpagas
        if (criteria.isBuscarSoloInpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<FacturaCompra> typedQuery = em.createQuery(query, FacturaCompra.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        List<FacturaCompra> facturasCompra = typedQuery.getResultList();
        return facturasCompra;
    }

    @Override
    public List<FacturaVenta> buscarFacturasVenta(BusquedaFacturaVentaCriteria criteria) {
        String query = "SELECT f FROM FacturaVenta f WHERE f.empresa = :empresa AND f.eliminada = false";
        //Fecha
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde()) + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Cliente
        if (criteria.isBuscaCliente() == true) {
            query += " AND f.cliente = " + criteria.getCliente().getId_Cliente();
        }
        //Tipo de Factura
        if (criteria.isBuscaPorTipoFactura() == true) {
            query += " AND f.tipoFactura = '" + criteria.getTipoFactura() + "'";
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true) {
            query += " AND f.usuario = " + criteria.getUsuario().getId_Usuario();
        }
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Pedido
        if (criteria.isBuscarPorPedido() == true) {
            query += " AND f.pedido.nroPedido = " + criteria.getNroPedido();
        }    
        //Inpagas
        if (criteria.isBuscaSoloImpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<FacturaVenta> typedQuery = em.createQuery(query, FacturaVenta.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        List<FacturaVenta> facturasVenta = typedQuery.getResultList();
        return facturasVenta;
    }

    @Override
    public long getMayorNumFacturaSegunTipo(String tipoDeFactura, long serie) {
        TypedQuery<Long> typedQuery = em.createNamedQuery("Factura.buscarMayorNumFacturaSegunTipo", Long.class);
        typedQuery.setParameter("tipo", tipoDeFactura.charAt(tipoDeFactura.length() - 1));
        typedQuery.setParameter("serie", serie);
        Long resultado = typedQuery.getSingleResult();
        if (resultado == null) {
            return 0;
        } else {
            return resultado;
        }
    }

    @Override
    public Factura guardar(Factura factura) {
        factura = em.merge(factura);
        em.persist(factura);
        return factura;
    }

    @Override
    public void actualizar(Factura factura) {
        em.merge(factura);
    }
}
