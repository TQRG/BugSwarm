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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rubro")
@NamedQueries({
    @NamedQuery(name = "Rubro.buscarPorId",
            query = "SELECT r FROM Rubro r "
                    + "WHERE r.eliminado = false AND r.id_Rubro = :id"),
    @NamedQuery(name = "Rubro.buscarTodos",
            query = "SELECT r FROM Rubro r "
                    + "WHERE r.eliminado = false AND r.empresa = :empresa "
                    + "ORDER BY r.nombre ASC"),
    @NamedQuery(name = "Rubro.buscarPorNombre",
            query = "SELECT r FROM Rubro r "
                    + "WHERE r.eliminado = false AND r.nombre LIKE :nombre AND r.empresa = :empresa "
                    + "ORDER BY r.nombre ASC")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"nombre", "empresa"})
public class Rubro implements Serializable {

    @Id
    @GeneratedValue
    private long id_Rubro;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }

}
