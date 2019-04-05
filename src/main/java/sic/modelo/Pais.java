package sic.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pais")
@NamedQueries({
    @NamedQuery(name = "Pais.buscarTodos",
            query = "SELECT p FROM Pais p "
                    + "WHERE p.eliminado = false ORDER BY p.nombre ASC"),
    @NamedQuery(name = "Pais.buscarPorNombre",
            query = "SELECT p FROM Pais p "
                    + "WHERE p.eliminado = false AND p.nombre LIKE :nombre "
                    + "ORDER BY p.nombre ASC")
})
@Data
@EqualsAndHashCode(of = {"nombre"})
public class Pais implements Serializable {

    @Id
    @GeneratedValue
    private long id_Pais;

    @Column(nullable = false)
    private String nombre;
    
    private boolean eliminado = false;

    @Override
    public String toString() {
        return nombre;
    }

}
