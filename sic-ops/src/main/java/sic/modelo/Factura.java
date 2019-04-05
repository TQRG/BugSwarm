package sic.modelo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"fecha", "tipoFactura", "numSerie", "numFactura", "empresa"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id_Factura", scope = Factura.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
   @Type(value = FacturaCompra.class),
   @Type(value = FacturaVenta.class),    
})
public abstract class Factura implements Serializable {

    private long id_Factura;
    private Date fecha;
    private char tipoFactura;
    private long numSerie;
    private long numFactura;
    private Date fechaVencimiento;      
    private Pedido pedido;    
    private Transportista transportista;        
    private List<RenglonFactura> renglones;      
    private List<Pago> pagos;
    private double subTotal;
    private double recargo_porcentaje;
    private double recargo_neto;
    private double descuento_porcentaje;
    private double descuento_neto;
    private double subTotal_neto;
    private double iva_105_neto;
    private double iva_21_neto;
    private double impuestoInterno_neto;
    private double total;    
    private String observaciones;
    private boolean pagada;    
    private Empresa empresa;
    private boolean eliminada;   
}
