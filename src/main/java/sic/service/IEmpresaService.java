package sic.service;

import java.util.List;
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;

public interface IEmpresaService {

    void actualizar(Empresa empresa);

    void eliminar(Empresa empresa);

    EmpresaActiva getEmpresaActiva();

    Empresa getEmpresaPorCUIP(long cuip);

    Empresa getEmpresaPorNombre(String nombre);

    List<Empresa> getEmpresas();

    void guardar(Empresa empresa);

    void setEmpresaActiva(Empresa empresa);
    
}
