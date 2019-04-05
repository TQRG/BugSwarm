package sic.modelo;

import java.io.Serializable;
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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuraciondelsistema")
@NamedQueries({
    @NamedQuery(name = "ConfiguracionDelSistema.buscarPorId",
            query = "SELECT cds FROM ConfiguracionDelSistema cds "
                    + "WHERE cds.id_ConfiguracionDelSistema = :id"),
    @NamedQuery(name = "ConfiguracionDelSistema.buscarPorEmpresa",
            query = "SELECT cds FROM ConfiguracionDelSistema cds "
                    + "WHERE cds.empresa = :empresa")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracionDelSistema implements Serializable {

    @Id
    @GeneratedValue
    private long id_ConfiguracionDelSistema;

    private boolean usarFacturaVentaPreImpresa;

    private int cantidadMaximaDeRenglonesEnFactura;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

}
