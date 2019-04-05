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
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "facturaventa")
@NamedQueries({    
    @NamedQuery(name = "Factura.buscarMayorNumFacturaSegunTipo",
            query = "SELECT max(fv.numFactura) FROM FacturaVenta fv "
                    + "WHERE fv.tipoFactura = :tipo AND fv.numSerie = :serie"),
    @NamedQuery(name = "Factura.buscarTopProductosMasVendidosPorAnio",
            query = "SELECT renglones.descripcionItem, sum(renglones.cantidad) as suma "
            + "FROM FacturaVenta factura INNER JOIN factura.renglones renglones "
            + "WHERE year(factura.fecha) = :anio AND factura.eliminada = false "
            + "GROUP BY renglones.descripcionItem ORDER BY sum(renglones.cantidad) DESC")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class FacturaVenta extends Factura implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_Cliente", referencedColumnName = "id_Cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_Usuario", referencedColumnName = "id_Usuario")
    private Usuario usuario;

    public FacturaVenta() {}

    public FacturaVenta(Cliente cliente, Usuario usuario, long id_Factura, Date fecha,
            char tipoFactura, long numSerie, long numFactura, Date fechaVencimiento,
            Pedido pedido, Transportista transportista, List<RenglonFactura> renglones,
            List<Pago> pagos, double subTotal, double recargo_porcentaje, double recargo_neto,
            double descuento_porcentaje, double descuento_neto, double subTotal_neto, double iva_105_neto,
            double iva_21_neto, double impuestoInterno_neto, double total, String observaciones, boolean pagada,
            Empresa empresa, boolean eliminada) {
        super(id_Factura, fecha, tipoFactura, numSerie, numFactura, fechaVencimiento, 
                pedido, transportista, renglones, pagos, subTotal, recargo_porcentaje, 
                recargo_neto, descuento_porcentaje, descuento_neto, subTotal_neto, 
                iva_105_neto, iva_21_neto, impuestoInterno_neto, total, observaciones,
                pagada, empresa, eliminada);
        this.cliente = cliente;
        this.usuario = usuario;
    }
    
    

}
