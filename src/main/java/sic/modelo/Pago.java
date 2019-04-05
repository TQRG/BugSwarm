package sic.modelo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pago")
@NamedQueries({
    @NamedQuery(name = "Pago.buscarPorId",
            query = "SELECT p FROM Pago p "
                + "WHERE p.eliminado = false AND p.id_Pago= :id"),
    @NamedQuery(name = "Pago.buscarPorNroYEmpresa",
            query = "SELECT p FROM Pago p "
                + "WHERE p.eliminado = false AND p.nroPago= :nroPago "
                + "AND p.empresa.id_Empresa = :idEmpresa"),
    @NamedQuery(name = "Pago.buscarPagosEntreFechasYFormaDePago",
            query = "SELECT p FROM Pago p "
                + "WHERE p.empresa.id_Empresa = :id_Empresa "
                + "AND p.formaDePago.id_FormaDePago = :id_FormaDePago "
                + "AND p.eliminado = false AND p.fecha BETWEEN :desde AND :hasta"),
    @NamedQuery(name = "Pago.buscarMayorNroPago",
            query = "SELECT MAX(p.nroPago) FROM Pago p "
                + "WHERE p.empresa.id_Empresa = :idEmpresa"),
    @NamedQuery(name = "Pago.buscarPorFactura",
            query = "SELECT p FROM Pago p "
                + "WHERE p.factura.id_Factura = :idFactura AND p.eliminado = false "
                + "ORDER BY p.fecha ASC")
})
@Data
@EqualsAndHashCode(of = {"nroPago", "empresa"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id_Pago", scope = Pago.class)
public class Pago implements Serializable {

    @Id
    @GeneratedValue
    private Long id_Pago;
    
    private long nroPago;

    @ManyToOne
    @JoinColumn(name = "id_FormaDePago", referencedColumnName = "id_FormaDePago")
    private FormaDePago formaDePago;

    @ManyToOne
    @JoinColumn(name = "id_Factura", referencedColumnName = "id_Factura")
    private Factura factura;

    @Column(nullable = false)
    private double monto;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(nullable = false)
    private String nota;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    private boolean eliminado;
        
    public Pago() {}

    public Pago(Long id_Pago, long nroPago, FormaDePago formaDePago, Factura factura,
            double monto, Date fecha, String nota, Empresa empresa, boolean eliminado) {
        this.id_Pago = id_Pago;
        this.nroPago = nroPago;
        this.formaDePago = formaDePago;
        this.factura = factura;
        this.monto = monto;
        this.fecha = fecha;
        this.nota = nota;
        this.empresa = empresa;
        this.eliminado = eliminado;
    }

}
