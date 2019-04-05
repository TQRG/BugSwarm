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
@Table(name = "proveedor")
@NamedQueries({
    @NamedQuery(name = "Proveedor.buscarPorId",
            query = "SELECT p FROM Proveedor p "
                    + "WHERE p.eliminado = false AND p.id_Proveedor = :id"),
    @NamedQuery(name = "Proveedor.buscarTodos",
            query = "SELECT p FROM Proveedor p "
                    + "WHERE p.empresa = :empresa AND p.eliminado = false "
                    + "ORDER BY p.razonSocial ASC"),
    @NamedQuery(name = "Proveedor.buscarPorCodigo",
            query = "SELECT p FROM Proveedor p "
                    + "WHERE p.codigo = :codigo AND p.empresa = :empresa AND p.eliminado = false"),
    @NamedQuery(name = "Proveedor.buscarPorRazonSocial",
            query = "SELECT p FROM Proveedor p "
                    + "WHERE p.razonSocial = :razonSocial AND p.empresa = :empresa AND p.eliminado = false"),
    @NamedQuery(name = "Proveedor.buscarPorIdFiscal",
            query = "SELECT p FROM Proveedor p "
                    + "WHERE p.id_Fiscal = :idFiscal AND p.empresa = :empresa AND p.eliminado = false")
})
@Data
@EqualsAndHashCode(of = {"razonSocial", "empresa"})
public class Proveedor implements Serializable {

    @Id
    @GeneratedValue
    private long id_Proveedor;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String razonSocial;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "id_CondicionIVA", referencedColumnName = "id_CondicionIVA")
    private CondicionIVA condicionIVA;

    @Column(nullable = false)
    private String id_Fiscal;

    @Column(nullable = false)
    private String telPrimario;

    @Column(nullable = false)
    private String telSecundario;

    @Column(nullable = false)
    private String contacto;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String web;

    @ManyToOne
    @JoinColumn(name = "id_Localidad", referencedColumnName = "id_Localidad")
    private Localidad localidad;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    private boolean eliminado;

    @Override
    public String toString() {
        return razonSocial;
    }
}
