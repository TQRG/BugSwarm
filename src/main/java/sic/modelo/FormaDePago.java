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
@Table(name = "formadepago")
@NamedQueries({
    @NamedQuery(name = "FormaDePago.buscarTodas",
            query = "SELECT f FROM FormaDePago f "
                    + "WHERE f.empresa = :empresa AND f.eliminada = false "
                    + "ORDER BY f.nombre ASC"),
    @NamedQuery(name = "FormaDePago.buscarPorId",
            query = "SELECT f FROM FormaDePago f "
                    + "WHERE f.eliminada = false AND f.id_FormaDePago = :id"),
    @NamedQuery(name = "FormaDePago.buscarPorNombre",
            query = "SELECT f FROM FormaDePago f "
                    + "WHERE f.eliminada = false AND f.empresa = :empresa AND f.nombre = :nombre"),
    @NamedQuery(name = "FormaDePago.buscarPredeterminada",
            query = "SELECT f FROM FormaDePago f "
                    + "WHERE f.predeterminado = true and f.empresa = :empresa and f.eliminada = false")
})
@Data
@EqualsAndHashCode(of = {"nombre", "empresa"})
public class FormaDePago implements Serializable {

    @Id
    @GeneratedValue
    private long id_FormaDePago;

    @Column(nullable = false)
    private String nombre;

    private boolean afectaCaja;

    private boolean predeterminado;
    
    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    private boolean eliminada;

    @Override
    public String toString() {
        return nombre;
    }

}
