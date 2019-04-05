package sic.service;

import java.util.List;
import sic.modelo.Empresa;

public interface IEmpresaService {
    
    Empresa getEmpresaPorId(Long id_Empresa);

    void actualizar(Empresa empresa);

    void eliminar(Empresa empresa);    

    Empresa getEmpresaPorCUIP(long cuip);

    Empresa getEmpresaPorNombre(String nombre);

    List<Empresa> getEmpresas();

    void guardar(Empresa empresa);    
}
