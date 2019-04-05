package sic.service;

import java.util.Date;
import java.util.List;
import sic.modelo.Gasto;

public interface IGastoService {

    void actualizar(Gasto gasto);

    List<Object> getGastosPorFecha(Long id_Empresa, Date desde, Date hasta);

    List<Object> getGastosPorFechaYFormaDePago(Long id_Empresa, Long id_FormaDePago, Date desde, Date hasta);

    long getUltimoNumeroDeCaja(long id_Empresa);

    void guardar(Gasto gasto);

    void validarGasto(Gasto gasto);

    int getUltimoNumeroDeGasto(long id_empresa);

}
