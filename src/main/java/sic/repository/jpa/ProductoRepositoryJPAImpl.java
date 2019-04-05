package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.BusquedaProductoCriteria;
import sic.modelo.Empresa;
import sic.modelo.Producto;
import sic.repository.IProductoRepository;

@Repository
public class ProductoRepositoryJPAImpl implements IProductoRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Producto> buscarProductos(BusquedaProductoCriteria criteria) {
        String query = "SELECT p FROM Producto p WHERE p.empresa = :empresa AND p.eliminado = false";
        //Codigo        
        if (criteria.isBuscarPorCodigo() == true) {
            query += " AND p.codigo LIKE '%" + criteria.getCodigo() + "%'";
        }
        //Descripcion
        if (criteria.isBuscarPorDescripcion() == true) {
            String[] terminos = criteria.getDescripcion().split(" ");
            for (String termino : terminos) {
                query += " AND p.descripcion LIKE '%" + termino + "%'";
            }
        }
        //Rubro
        if (criteria.isBuscarPorRubro() == true) {
            query += " AND p.rubro = " + criteria.getRubro().getId_Rubro();
        }
        //Proveedor
        if (criteria.isBuscarPorProveedor()) {
            query += " AND p.proveedor = " + criteria.getProveedor().getId_Proveedor();
        }
        //Faltantes
        if (criteria.isListarSoloFaltantes() == true) {
            query += " AND p.cantidad <= p.cantMinima AND p.ilimitado = 0";
        }
        query += " ORDER BY p.descripcion ASC";
        TypedQuery<Producto> typedQuery = em.createQuery(query, Producto.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        List<Producto> productos = typedQuery.getResultList();
        return productos;
    }

    @Override
    public Producto getProductoPorId(long id_Producto) {
        TypedQuery<Producto> typedQuery = em.createNamedQuery("Producto.buscarPorId", Producto.class);
        typedQuery.setParameter("id", id_Producto);
        List<Producto> productos = typedQuery.getResultList();
        if (productos.isEmpty()) {
            return null;
        } else {
            return productos.get(0);
        }
    }

    @Override
    public Producto getProductoPorDescripcion(String descripcion, Empresa empresa) {
        TypedQuery<Producto> typedQuery = em.createNamedQuery("Producto.buscarPorDescripcion", Producto.class);
        typedQuery.setParameter("descripcion", descripcion);
        typedQuery.setParameter("empresa", empresa);
        List<Producto> productos = typedQuery.getResultList();
        if (productos.isEmpty()) {
            return null;
        } else {
            return productos.get(0);
        }
    }

    @Override
    public List<Producto> getProductosQueContengaCodigoDescripcion(String criteria, int cantRegistros, Empresa empresa) {
        String query = "SELECT p FROM Producto p WHERE p.empresa = :empresa AND p.eliminado = false";
        query += " AND (p.codigo LIKE '%" + criteria + "%' OR (";
        String[] terminos = criteria.split(" ");
        for (int i = 0; i < terminos.length; i++) {
            query += "p.descripcion LIKE '%" + terminos[i] + "%'";
            //si no es el ultimo, agregar un AND
            if (i != (terminos.length - 1)) {
                query += " AND ";
            }
        }
        query += ")) ORDER BY p.descripcion ASC";
        TypedQuery<Producto> typedQuery = em.createQuery(query, Producto.class);
        typedQuery.setParameter("empresa", empresa);
        //si es 0, recupera TODOS los registros
        if (cantRegistros != 0) {
            typedQuery.setMaxResults(cantRegistros);
        }
        List<Producto> productos = typedQuery.getResultList();
        return productos;
    }

    @Override
    public Producto getProductoPorCodigo(String codigo, Empresa empresa) {
        TypedQuery<Producto> typedQuery = em.createNamedQuery("Producto.buscarPorCodigo", Producto.class);
        typedQuery.setParameter("codigo", codigo);
        typedQuery.setParameter("empresa", empresa);
        List<Producto> productos = typedQuery.getResultList();
        if (productos.isEmpty()) {
            return null;
        } else {
            return productos.get(0);
        }
    }

    @Override
    public void guardar(Producto producto) {
        em.persist(em.merge(producto));
    }

    @Override
    public void actualizar(Producto producto) {
        em.merge(producto);
    }

    @Override
    public void actualizarMultiplesProductos(List<Producto> productos) {
        for (Producto producto : productos) {
            em.merge(producto);
        }

    }
}
