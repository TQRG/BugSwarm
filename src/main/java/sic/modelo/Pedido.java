package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import sic.service.EstadoPedido;

@Entity
@Table(name = "pedido")
@NamedQueries({
    @NamedQuery(name = "Pedido.buscarMayorNroPedido",
            query = "SELECT MAX(p.nroPedido) FROM Pedido p "
                    + "WHERE p.empresa.id_Empresa = :idEmpresa"),
    @NamedQuery(name = "Pedido.buscarRenglonesDelPedido",
            query = "SELECT p FROM Pedido p LEFT JOIN FETCH p.renglones "
                    + "WHERE p.nroPedido = :nroPedido"),
    @NamedQuery(name = "Pedido.buscarPorId",
            query = "SELECT p FROM Pedido p "
                    + "WHERE p.id_Pedido = :id"),
    @NamedQuery(name = "Pedido.buscarPorNumero",
            query = "SELECT p FROM Pedido p "
                    + "WHERE p.nroPedido = :nroPedido AND p.empresa.id_Empresa = :idEmpresa"),
    @NamedQuery(name = "Pedido.buscarPorNumeroConFacturas",
            query = "SELECT p FROM Pedido p LEFT JOIN FETCH p.facturas "
                    + "WHERE p.nroPedido = :nroPedido"),
    @NamedQuery(name = "Pedido.buscarPorNumeroConRenglones",
            query = "SELECT p FROM Pedido p LEFT JOIN FETCH p.renglones "
                    + "WHERE p.nroPedido = :nroPedido AND p.empresa.id_Empresa = :idEmpresa")
})
@Data
@ToString(exclude= "renglones")
@EqualsAndHashCode(of = {"nroPedido", "empresa"})
public class Pedido implements Serializable {

    @Id
    @GeneratedValue
    private long id_Pedido;

    private long nroPedido;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaVencimiento;

    @Column(nullable = false)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    private boolean eliminado;

    @ManyToOne
    @JoinColumn(name = "id_Cliente", referencedColumnName = "id_Cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_Usuario", referencedColumnName = "id_Usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido")
    private List<Factura> facturas;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "pedido", orphanRemoval = true)
    private List<RenglonPedido> renglones;

    private double totalEstimado;

    private double totalActual;
    
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

}
