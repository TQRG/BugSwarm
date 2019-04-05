package sic.repository;

import java.util.List;
import sic.modelo.CondicionIVA;

public interface ICondicionIVARepository {

    void actualizar(CondicionIVA condicionIVA);

    CondicionIVA getCondicionIVAPorNombre(String nombre);

    List<CondicionIVA> getCondicionesIVA();

    void guardar(CondicionIVA condicionIVA);
    
}
