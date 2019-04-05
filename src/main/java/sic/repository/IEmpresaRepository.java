package sic.repository;

import java.util.List;
import sic.modelo.Empresa;

public interface IEmpresaRepository {
    
    Empresa getEmpresaPorId(long id_Empresa);

    void actualizar(Empresa empresa);

    Empresa getEmpresaPorCUIP(long cuip);

    Empresa getEmpresaPorNombre(String nombre);

    List<Empresa> getEmpresas();

    void guardar(Empresa empresa);
    
}
