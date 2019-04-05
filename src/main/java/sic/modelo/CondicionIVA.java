package sic.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "condicioniva")
@NamedQueries({
    @NamedQuery(name = "CondicionIVA.buscarPorId",
            query = "SELECT c FROM CondicionIVA c "
                    + "WHERE c.eliminada = false AND c.id_CondicionIVA = :id"),
    @NamedQuery(name = "CondicionIVA.buscarTodas",
            query = "SELECT c FROM CondicionIVA c "
                    + "WHERE c.eliminada = false "
                    + "ORDER BY c.nombre ASC"),
    @NamedQuery(name = "CondicionIVA.buscarPorNombre",
            query = "SELECT c FROM CondicionIVA c "
                    + "WHERE c.nombre LIKE :nombre AND c.eliminada = false")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"nombre"})
public class CondicionIVA implements Serializable {

    @Id
    @GeneratedValue
    private long id_CondicionIVA;

    @Column(nullable = false)
    private String nombre;

    private boolean discriminaIVA;

    private boolean eliminada;

    @Override
    public String toString() {
        if (discriminaIVA) {
            return nombre + " (discrimina IVA)";
        } else {
            return nombre + " (no discrimina IVA)";
        }
    }

}
