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
@Table(name = "localidad")
@NamedQueries({
    @NamedQuery(name = "Localidad.buscarTodas",
            query = "SELECT l FROM Localidad l "
                    + "WHERE l.eliminada = false "
                    + "ORDER BY l.nombre ASC"),
    @NamedQuery(name = "Localidad.buscarLocalidadesDeLaProvincia",
            query = "SELECT l FROM Localidad l "
                    + "WHERE l.provincia = :provincia AND l.eliminada = false "
                    + "ORDER BY l.nombre ASC"),
    @NamedQuery(name = "Localidad.buscarPorNombre",
            query = "SELECT l FROM Localidad l "
                    + "WHERE l.provincia = :provincia AND l.eliminada = false AND l.nombre = :nombre "
                    + "ORDER BY l.nombre ASC")
})
@Data
@EqualsAndHashCode(of = {"nombre"})
public class Localidad implements Serializable {

    @Id
    @GeneratedValue
    private long id_Localidad;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String codigoPostal;

    @ManyToOne
    @JoinColumn(name = "id_Provincia", referencedColumnName = "id_Provincia")
    private Provincia provincia;

    private boolean eliminada = false;

    @Override
    public String toString() {
        return nombre;
    }
}
