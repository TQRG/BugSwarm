package sic.service;

import java.util.List;
import sic.modelo.CondicionIVA;

public interface ICondicionIVAService {

    void actualizar(CondicionIVA condicionIVA);

    void eliminar(CondicionIVA condicionIVA);

    CondicionIVA getCondicionIVAPorNombre(String nombre);

    List<CondicionIVA> getCondicionesIVA();

    void guardar(CondicionIVA condicionIVA);

    void validarOperacion(TipoDeOperacion operacion, CondicionIVA condicionIVA);

}
