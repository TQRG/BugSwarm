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
@Table(name = "provincia")
@NamedQueries({
    @NamedQuery(name = "Provincia.buscarPorId",
            query = "SELECT p FROM Provincia p "
                    + "WHERE p.eliminada = false AND p.id_Provincia = :id"),
    @NamedQuery(name = "Provincia.buscarProvinciasDelPais",
            query = "SELECT p FROM Provincia p "
                    + "WHERE p.pais = :pais AND p.eliminada = false "
                    + "ORDER BY p.nombre ASC"),
    @NamedQuery(name = "Provincia.buscarPorNombre",
            query = "SELECT p FROM Provincia p W"
                    + "HERE p.pais = :pais AND p.eliminada = false AND p.nombre = :nombre "
                    + "ORDER BY p.nombre ASC")
})
@Data
@EqualsAndHashCode(of = {"nombre"})
public class Provincia implements Serializable {

    @Id
    @GeneratedValue
    private long id_Provincia;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_Pais", referencedColumnName = "id_Pais")
    private Pais pais;
    
    private boolean eliminada = false;

    @Override
    public String toString() {
        return nombre;
    }

}
