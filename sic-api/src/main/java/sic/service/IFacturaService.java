package sic.service;

import sic.modelo.Movimiento;
import java.util.List;
import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.Pedido;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.RenglonFactura;

public interface IFacturaService {
    
    Factura getFacturaPorId(Long id_Factura);
    
    List<Factura> getFacturasDelPedido(Long idPedido);

    String[] getTipoFacturaCompra(Empresa empresa, Proveedor proveedor);

    String[] getTipoFacturaVenta(Empresa empresa, Cliente cliente);

    char[] getTiposFacturaSegunEmpresa(Empresa empresa);

    List<RenglonFactura> getRenglonesDeLaFactura(Long id_Factura);

    String getTipoFactura(Factura factura);

    Movimiento getTipoMovimiento(Factura factura);

    List<FacturaCompra> buscarFacturaCompra(BusquedaFacturaCompraCriteria criteria);

    List<FacturaVenta> buscarFacturaVenta(BusquedaFacturaVentaCriteria criteria);
    
    List<Factura> guardar(List<Factura> facturas, Long idPedido);
    
    void actualizar(Factura factura);

    void eliminar(long[] idFactura);
    
    Factura actualizarFacturaEstadoPagada(Factura factura);
    
    double getTotalPagado(Factura factura);

    List<Factura> ordenarFacturasPorFechaAsc(List<Factura> facturas);

    boolean validarFacturasParaPagoMultiple(List<Factura> facturas, Movimiento movimiento);

    boolean validarClienteProveedorParaPagosMultiples(List<Factura> facturas, Movimiento movimiento);

    boolean validarFacturasImpagasParaPagoMultiple(List<Factura> facturas);

    boolean validarCantidadMaximaDeRenglones(int cantidad, Empresa empresa);

    double calcularSubTotal(double[] importes);

    double calcularDescuento_neto(double subtotal, double descuento_porcentaje);

    double calcularRecargo_neto(double subtotal, double recargo_porcentaje);

    double calcularSubTotal_neto(double subtotal, double recargo_neto, double descuento_neto);

    double calcularIva_neto(String tipoDeFactura, double descuento_porcentaje, double recargo_porcentaje, double[] importe, double[] ivaPorcentaje, double iva_porcentaje);

    double calcularImpInterno_neto(String tipoDeFactura, double descuento_porcentaje, double recargo_porcentaje, double[] importes, double [] impuestoPorcentajes);

    double calcularImpInterno_neto(Movimiento movimiento, Producto producto, double descuento_neto);

    double calcularTotal(double subTotal, double descuento_neto, double recargo_neto, double iva105_neto, double iva21_neto, double impInterno_neto);

    double calcularTotalFacturadoVenta(BusquedaFacturaVentaCriteria criteria);

    double calcularTotalFacturadoCompra(BusquedaFacturaCompraCriteria criteria);

    double calcularIVA_Venta(BusquedaFacturaVentaCriteria criteria);

    double calcularIVA_Compra(BusquedaFacturaCompraCriteria criteria);

    double calcularGananciaTotal(BusquedaFacturaVentaCriteria criteria);

    double calcularIVA_neto(Movimiento movimiento, Producto producto, double descuento_neto);

    double calcularPrecioUnitario(Movimiento movimiento, String tipoDeFactura, Producto producto);

    long calcularNumeroFactura(char tipoDeFactura, long serie, long idEmpresa);

    double calcularVuelto(double importeAPagar, double importeAbonado);

    double calcularImporte(double cantidad, double precioUnitario, double descuento_neto);    

    byte[] getReporteFacturaVenta(Factura factura);

    List<Factura> dividirFactura(FacturaVenta factura, int[] indices);

    List<RenglonFactura> convertirRenglonesPedidoARenglonesFactura(Pedido pedido, String tipoDeComprobante);

    RenglonFactura calcularRenglon(String tipoDeFactura, Movimiento movimiento, double cantidad, Long idProducto, double descuento_porcentaje);

}
