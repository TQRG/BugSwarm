package sic.repository;

import java.util.Date;
import java.util.List;
import sic.modelo.BusquedaCajaCriteria;
import sic.modelo.Caja;

public interface ICajaRepository {

    void actualizar(Caja caja);

    Caja getCajaPorFormaDePago(long idEmpresa, long idFormaDePago);
    
    Caja getCajaPorId(Long id);

    Caja getCajaPorIdYEmpresa(long id_Caja, long id_Empresa);

    List<Caja> getCajas(long id_Empresa, Date desde, Date hasta);

    List<Caja> getCajasCriteria(BusquedaCajaCriteria criteria);

    Caja getUltimaCaja(long id_Empresa);

    int getUltimoNumeroDeCaja(long idEmpresa);

    void guardar(Caja caja);

}
