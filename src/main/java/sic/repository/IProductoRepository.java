package sic.repository;

import java.util.List;
import sic.modelo.BusquedaProductoCriteria;
import sic.modelo.Empresa;
import sic.modelo.Producto;

public interface IProductoRepository {

    List<Producto> buscarProductos(BusquedaProductoCriteria criteria);

    void actualizar(Producto producto);

    void actualizarMultiplesProductos(List<Producto> productos);

    Producto getProductoPorCodigo(String codigo, Empresa empresa);

    Producto getProductoPorDescripcion(String descripcion, Empresa empresa);

    Producto getProductoPorId(long id_Producto);

    List<Producto> getProductosQueContengaCodigoDescripcion(String criteria, int cantRegistros, Empresa empresa);

    void guardar(Producto producto);
    
}
