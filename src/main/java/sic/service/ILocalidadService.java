package sic.service;

import java.util.List;
import sic.modelo.Localidad;
import sic.modelo.Provincia;

public interface ILocalidadService {

    void actualizar(Localidad localidad);

    void eliminar(Localidad localidad);

    Localidad getLocalidadPorNombre(String nombre, Provincia provincia);

    List<Localidad> getLocalidadesDeLaProvincia(Provincia provincia);

    void guardar(Localidad localidad);

    void validarOperacion(TipoDeOperacion operacion, Localidad localidad);

}
