package sic.service;

import java.util.List;
import sic.modelo.Empresa;
import sic.modelo.Medida;

public interface IMedidaService {
    
    Medida getMedidaPorId(Long id_Medida);

    void actualizar(Medida medida);

    void eliminar(Medida medida);

    Medida getMedidaPorNombre(String nombre, Empresa empresa);

    List<Medida> getUnidadMedidas(Empresa empresa);

    void guardar(Medida medida);

    void validarOperacion(TipoDeOperacion operacion, Medida medida);

}
