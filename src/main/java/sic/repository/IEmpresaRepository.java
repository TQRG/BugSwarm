package sic.repository;

import java.util.List;
import sic.modelo.Empresa;

public interface IEmpresaRepository {

    void actualizar(Empresa empresa);

    Empresa getEmpresaPorCUIP(long cuip);

    Empresa getEmpresaPorNombre(String nombre);

    List<Empresa> getEmpresas();

    void guardar(Empresa empresa);
    
}
