package sic.service;

import java.util.List;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import sic.modelo.BusquedaProductoCriteria;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.Medida;
import sic.modelo.PreciosProducto;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rubro;

public interface IProductoService {

    void actualizar(Producto producto);

    void actualizarStock(Factura factura, TipoDeOperacion operacion);

    List<Producto> buscarProductos(BusquedaProductoCriteria criteria);

    double calcularGanancia_Neto(double precioCosto, double ganancia_porcentaje);

    boolean existeStockDisponible(long idProducto, double cantidad);

    //**************************************************************************
    //Calculos
    double calcularGanancia_Porcentaje(double precioCosto, double PVP);

    double calcularIVA_Neto(double precioCosto, double iva_porcentaje);

    double calcularImpInterno_Neto(double precioCosto, double impInterno_porcentaje);

    double calcularPVP(double precioCosto, double ganancia_porcentaje);

    double calcularPrecioLista(double PVP, double iva_porcentaje, double impInterno_porcentaje);

    void eliminarMultiplesProductos(List<Producto> productos);

    Producto getProductoPorCodigo(String codigo, Empresa empresa);

    Producto getProductoPorDescripcion(String descripcion, Empresa empresa);

    Producto getProductoPorId(long id_Producto);

    List<Producto> getProductosPorDescripcionQueContenga(String criteria, int cantRegistros, Empresa empresa);

    //**************************************************************************
    //Reportes
    JasperPrint getReporteListaDePrecios(List<Producto> productos) throws JRException;

    void guardar(Producto producto);

    void modificarMultiplesProductos(List<Producto> productos, boolean checkPrecios, PreciosProducto preciosProducto, boolean checkMedida, Medida medida, boolean checkRubro, Rubro rubro, boolean checkProveedor, Proveedor proveedor);

}
