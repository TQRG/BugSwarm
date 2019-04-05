package sic.repository;

import java.util.List;
import sic.modelo.BusquedaProveedorCriteria;
import sic.modelo.Empresa;
import sic.modelo.Proveedor;

public interface IProveedorRepository {

    Proveedor getProveedorPorId(Long id_Proveedor);
    
    void actualizar(Proveedor proveedor);

    List<Proveedor> buscarProveedores(BusquedaProveedorCriteria criteria);

    Proveedor getProveedorPorCodigo(String codigo, Empresa empresa);

    Proveedor getProveedorPorId_Fiscal(String id_Fiscal, Empresa empresa);

    Proveedor getProveedorPorRazonSocial(String razonSocial, Empresa empresa);

    List<Proveedor> getProveedores(Empresa empresa);

    void guardar(Proveedor proveedor);
    
}
