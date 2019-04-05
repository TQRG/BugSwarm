package sic.service;

import java.util.Date;
import java.util.List;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Pago;

public interface IPagoService {

    List<Pago> getPagosDeLaFactura(Factura factura);

    double getSaldoAPagar(Factura factura);

    double getTotalPagado(Factura factura);

    List<Pago> getPagosEntreFechasYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta);

    List<Factura> getFacturasEntreFechasYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta);
    
    void pagarMultiplesFacturasVenta(List<FacturaVenta> facturas, double monto, FormaDePago formaDePago, String nota, Date fechaYHora);
    
    void pagarMultiplesFacturasCompra(List<FacturaCompra> facturas, double monto, FormaDePago formaDePago, String nota, Date fechaYHora);
    
    double calcularTotalAdeudadoFacturasVenta(List<FacturaVenta> facturasVenta);
    
    double calcularTotalAdeudadoFacturasCompra(List<FacturaCompra> facturasCompra);
    
    double calcularTotalAdeudadoFacturas(List<Factura> facturas);
            
    void pagarMultiplesFacturas(List<Factura> facturas, double monto, FormaDePago formaDePago, String nota, Date fechaYHora);
    
    void validarOperacion(Pago pago);

    void guardar(Pago pago);

    void eliminar(Pago pago);
    
    void setFacturaEstadoDePago(Factura factura);
}
