package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facturacompra")
@NamedQueries({
    @NamedQuery(name = "FacturaCompra.buscarPorTipoSerieNum",
            query = "SELECT f FROM FacturaCompra f "
                    + "WHERE f.tipoFactura= :tipo AND f.numSerie= :serie AND f.numFactura= :num")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FacturaCompra extends Factura implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_Proveedor", referencedColumnName = "id_Proveedor")
    private Proveedor proveedor;
    
    @Builder
    private FacturaCompra(Date fecha, char tipoFactura, long numSerie, long numFactura, List<Pago> pagos,Date fechaVencimiento, 
            Transportista transportista, List<RenglonFactura> renglones, double subTotal, double recargo_porcentaje, 
            double recargo_neto, double descuento_porcentaje, double descuento_neto, double subTotal_neto, double iva_105_neto, 
            double iva_21_neto, double impuestoInterno_neto, double total, String observaciones, boolean pagada, Empresa empresa,
            boolean eliminada, Pedido pedido, Proveedor proveedor) {
        
        this.setFecha(fecha);
        this.setTipoFactura(tipoFactura);
        this.setNumSerie(numSerie);
        this.setNumFactura(numFactura);
        this.setPagos(pagos);
        this.setFechaVencimiento(fechaVencimiento);
        this.setTransportista(transportista);
        this.setRenglones(renglones);
        this.setSubTotal(subTotal);
        this.setRecargo_porcentaje(recargo_porcentaje);
        this.setRecargo_neto(recargo_neto);
        this.setDescuento_porcentaje(descuento_porcentaje);
        this.setDescuento_neto(descuento_neto);
        this.setSubTotal_neto(subTotal_neto);
        this.setIva_105_neto(iva_105_neto);
        this.setIva_21_neto(iva_21_neto);
        this.setImpuestoInterno_neto(impuestoInterno_neto);
        this.setTotal(total);
        this.setObservaciones(observaciones);
        this.setPagada(pagada);
        this.setEmpresa(empresa);
        this.setEliminada(eliminada);
        this.setPedido(pedido);      
        this.setProveedor(proveedor);
    }

}
