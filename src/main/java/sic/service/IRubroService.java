package sic.service;

import java.util.List;
import sic.modelo.Empresa;
import sic.modelo.Rubro;

public interface IRubroService {

    Rubro getRubroPorId(Long id_Rubro);
    
    void actualizar(Rubro rubro);

    void eliminar(Rubro rubro);

    Rubro getRubroPorNombre(String nombre, Empresa empresa);

    List<Rubro> getRubros(Empresa empresa);

    void guardar(Rubro rubro);
    
}
