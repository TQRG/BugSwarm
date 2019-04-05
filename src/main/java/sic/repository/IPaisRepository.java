package sic.repository;

import java.util.List;
import sic.modelo.Pais;

public interface IPaisRepository {
    
    Pais getPaisPorId(Long id_Pais);

    void actualizar(Pais pais);

    Pais getPaisPorNombre(String nombre);

    List<Pais> getPaises();

    Pais guardar(Pais pais);
    
}
