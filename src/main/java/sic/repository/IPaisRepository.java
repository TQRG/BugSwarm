package sic.repository;

import java.util.List;
import sic.modelo.Pais;

public interface IPaisRepository {

    void actualizar(Pais pais);

    Pais getPaisPorNombre(String nombre);

    List<Pais> getPaises();

    void guardar(Pais pais);
    
}
