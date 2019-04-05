package sic.repository;

import java.util.List;
import sic.modelo.Localidad;
import sic.modelo.Provincia;

public interface ILocalidadRepository {
    
    Localidad getLocalidadPorId(Long id_Localidad);

    void actualizar(Localidad localidad);

    Localidad getLocalidadPorNombre(String nombre, Provincia provincia);

    List<Localidad> getLocalidadesDeLaProvincia(Provincia provincia);

    Localidad guardar(Localidad localidad);
    
}
