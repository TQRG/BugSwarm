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
@Table(name = "pais")
@NamedQueries({
    @NamedQuery(name = "Pais.buscarPorId",
            query = "SELECT p FROM Pais p "
                    + "WHERE p.eliminado = false AND p.id_Pais= :id"),
    @NamedQuery(name = "Pais.buscarTodos",
            query = "SELECT p FROM Pais p "
                    + "WHERE p.eliminado = false ORDER BY p.nombre ASC"),
    @NamedQuery(name = "Pais.buscarPorNombre",
            query = "SELECT p FROM Pais p "
                    + "WHERE p.eliminado = false AND p.nombre LIKE :nombre "
                    + "ORDER BY p.nombre ASC")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"nombre"})
public class Pais implements Serializable {

    @Id
    @GeneratedValue
    private long id_Pais;

    @Column(nullable = false)
    private String nombre;
    
    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }

}
