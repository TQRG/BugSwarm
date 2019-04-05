package sic.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "renglonfactura")
@NamedQueries({
    @NamedQuery(name = "RenglonFactura.getRenglonesDeLaFactura",
            query = "SELECT r FROM RenglonFactura r "
                    + "WHERE r.factura = :factura")
})
@Data
@EqualsAndHashCode(of = {"id_ProductoItem", "codigoItem"})
public class RenglonFactura implements Serializable {

    @Id
    @GeneratedValue
    private long id_RenglonFactura;

    @ManyToOne
    @JoinColumn(name = "id_Factura", referencedColumnName = "id_Factura")
    private Factura factura;

    private long id_ProductoItem;

    @Column(nullable = false)
    private String codigoItem;

    @Column(nullable = false)
    private String descripcionItem;

    @Column(nullable = false)
    private String medidaItem;

    private double cantidad;
    private double precioUnitario;
    private double descuento_porcentaje;
    private double descuento_neto;
    private double iva_porcentaje;
    private double iva_neto;
    private double impuesto_porcentaje;
    private double impuesto_neto;
    private double ganancia_porcentaje;
    private double ganancia_neto;
    private double importe;

}
