package sic.repository;

import java.util.List;
import sic.modelo.BusquedaTransportistaCriteria;
import sic.modelo.Empresa;
import sic.modelo.Transportista;

public interface ITransportistaRepository {

    void actualizar(Transportista transportista);

    List<Transportista> busquedaPersonalizada(BusquedaTransportistaCriteria criteria);

    Transportista getTransportistaPorNombre(String nombre, Empresa empresa);

    List<Transportista> getTransportistas(Empresa empresa);

    void guardar(Transportista transportista);
    
}
