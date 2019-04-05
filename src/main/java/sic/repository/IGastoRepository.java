package sic.repository;

import java.util.Date;
import java.util.List;
import sic.modelo.Gasto;

public interface IGastoRepository {

    void actualizar(Gasto gasto);
    
    Gasto getGastoPorId(Long id);

    Gasto getGastoPorIdYEmpresa(long id_Gasto, long id_Empresa);

    List<Gasto> getGastosPorFecha(long id_Empresa, Date desde, Date hasta);

    List<Gasto> getGastosPorFechaYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta);

    int getUltimoNumeroDeGasto(long idEmpresa);

    void guardar(Gasto gasto);

}
