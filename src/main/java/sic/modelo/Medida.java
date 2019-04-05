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
@Table(name = "medida")
@NamedQueries({
    @NamedQuery(name = "Medida.buscarPorId",
            query = "SELECT m FROM Medida m "
                    + "WHERE m.eliminada = false AND m.id_Medida= :id"),
    @NamedQuery(name = "Medida.buscarTodas",
            query = "SELECT m FROM Medida m "
                    + "WHERE m.eliminada = false AND m.empresa = :empresa "
                    + "ORDER BY m.nombre ASC"),
    @NamedQuery(name = "Medida.buscarPorNombre",
            query = "SELECT m FROM Medida m "
                    + "WHERE m.eliminada = false AND m.nombre LIKE :nombre AND m.empresa = :empresa "
                    + "ORDER BY m.nombre ASC")
})
@Data
@EqualsAndHashCode(of = {"nombre", "empresa"})
public class Medida implements Serializable {

    @Id
    @GeneratedValue
    private long id_Medida;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    private boolean eliminada;

    @Override
    public String toString() {
        return nombre;
    }

}
