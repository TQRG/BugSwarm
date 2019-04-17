package sic.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaClienteCriteria {

    private boolean buscaPorRazonSocial;
    private String razonSocial;
    private boolean buscaPorNombreFantasia;
    private String nombreFantasia;
    private boolean buscaPorId_Fiscal;
    private String idFiscal;
    private boolean buscaPorPais;
    private Pais pais;
    private boolean buscaPorProvincia;
    private Provincia provincia;
    private boolean buscaPorLocalidad;
    private Localidad localidad;
    private Empresa empresa;

}
