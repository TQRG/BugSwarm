package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"razonSocial", "idFiscal", "empresa"})
public class Cliente implements Serializable {

    @Id
    @GeneratedValue
    private long id_Cliente;

    @Column(nullable = false)
    private String razonSocial;

    private String nombreFantasia;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "id_CondicionIVA", referencedColumnName = "id_CondicionIVA")
    private CondicionIVA condicionIVA;

    @Column(nullable = false)
    private String idFiscal;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telPrimario;

    @Column(nullable = false)
    private String telSecundario;

    @ManyToOne
    @JoinColumn(name = "id_Localidad", referencedColumnName = "id_Localidad")
    private Localidad localidad;

    @Column(nullable = false)
    private String contacto;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    private boolean eliminado;

    private boolean predeterminado;

    @Override
    public String toString() {
        return razonSocial;
    }
}
