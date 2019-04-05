package sic.modelo;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gasto")
@NamedQueries({
    @NamedQuery(name = "Gasto.getGastoPorId",
            query = "SELECT g FROM Gasto g "
                    + "WHERE g.id_Gasto = :id_Gasto"),
    @NamedQuery(name = "Gasto.getGastoSinArqueoPorFormaDePago",
            query = "SELECT g FROM Gasto g "
                    + "WHERE g.eliminado = false AND g.empresa.id_Empresa = :id_Empresa "
                    + "AND g.formaDePago.id_FormaDePago = :id_FormaDePago"),
    @NamedQuery(name = "Gasto.getGastosSinArqueoPorFormaDePagoYFecha",
            query = "SELECT g FROM Gasto g "
                    + "WHERE g.eliminado = false AND g.empresa.id_Empresa = :id_Empresa "
                    + "AND g.formaDePago.id_FormaDePago = :id_FormaDePago "
                    + "AND g.fecha BETWEEN :desde AND :hasta"),
    @NamedQuery(name = "Gasto.getGastosSinArqueoPorFecha",
            query = "SELECT g FROM Gasto g "
                    + "WHERE g.eliminado = false AND g.empresa.id_Empresa = :id_Empresa "
                    + "AND g.fecha BETWEEN :desde AND :hasta"),
    @NamedQuery(name = "Gasto.getGastoPorIdYEmpresa",
            query = "SELECT g FROM Gasto g "
                    + "WHERE g.id_Gasto = :id_Gasto AND g.empresa.id_Empresa = :id_Empresa "
                    + "ORDER BY g.fecha ASC"),
    @NamedQuery(name = "Gasto.getGastoPorNroYEmpresa",
            query = "SELECT g FROM Gasto g "
                    + "WHERE g.nroGasto = :nroGasto AND g.empresa.id_Empresa = :id_Empresa "
                    + "ORDER BY g.fecha ASC"),
    @NamedQuery(name = "Gasto.getUltimoNumeroDeGasto",
            query = "SELECT max(g.nroGasto) FROM Gasto g "
                    + "WHERE g.empresa.id_Empresa = :id_Empresa")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"nroGasto", "empresa"})
public class Gasto implements Serializable {

    @Id
    @GeneratedValue
    private long id_Gasto;

    private long nroGasto;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(nullable = false)
    private String concepto;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    @OneToOne
    @JoinColumn(name = "id_Usuario", referencedColumnName = "id_Usuario")
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "id_FormaDePago", referencedColumnName = "id_FormaDePago")
    private FormaDePago formaDePago;

    private double monto;

    private boolean eliminado;

}
