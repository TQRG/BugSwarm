package sic.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaProductoCriteria {

    private boolean buscarPorCodigo;
    private String codigo;
    private boolean buscarPorDescripcion;
    private String descripcion;
    private boolean buscarPorRubro;
    private Rubro rubro;
    private boolean buscarPorProveedor;
    private Proveedor proveedor;
    private Empresa empresa;
    private int cantRegistros;
    private boolean listarSoloFaltantes;

}
