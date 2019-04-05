package sic.repository;

import java.util.List;
import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;

public interface FacturaRepositoryCustom {

    double calcularTotalFacturadoVenta(BusquedaFacturaVentaCriteria criteria);

    double calcularTotalFacturadoCompra(BusquedaFacturaCompraCriteria criteria);
    
    double calcularIVA_Venta(BusquedaFacturaVentaCriteria criteria, char[] tipoFactura);
    
    double calcularIVA_Compra(BusquedaFacturaCompraCriteria criteria, char[] tipoFactura);
    
    double calcularGananciaTotal(BusquedaFacturaVentaCriteria criteria);

    List<FacturaVenta> buscarFacturasVenta(BusquedaFacturaVentaCriteria criteria);

    List<FacturaCompra> buscarFacturasCompra(BusquedaFacturaCompraCriteria criteria);

}
