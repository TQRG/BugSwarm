package sic.modelo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaFacturaCompraCriteria {

    private boolean buscaPorFecha;
    private Date fechaDesde;
    private Date fechaHasta;
    private boolean buscaPorProveedor;
    private Proveedor proveedor;
    private boolean buscaPorNumeroFactura;
    private int numSerie;
    private int numFactura;
    private boolean buscarSoloInpagas;
    private boolean buscaSoloPagadas;
    private Empresa empresa;
    private int cantRegistros;

}
