package sic.service;

import java.util.Date;
import java.util.List;
import sic.modelo.BusquedaCajaCriteria;
import sic.modelo.Caja;
import sic.modelo.Empresa;
import sic.modelo.Gasto;
import sic.modelo.Pago;
import sic.modelo.Usuario;

public interface ICajaService {

    void actualizar(Caja caja);
    
    void eliminar(Long idCaja);
    
    Caja getCajaPorId(Long id);
  
    double calcularTotalPagos(List<Pago> movimientos);
    
    double calcularTotalGastos(List<Gasto> movimientos);

    Caja getCajaPorIdYEmpresa(long id_Caja, long id_Empresa);
    
    Caja getCajaPorNroYEmpresa(int nroCaja, long id_Empresa);

    List<Caja> getCajas(long id_Empresa, Date desde, Date hasta);

    List<Caja> getCajasCriteria(BusquedaCajaCriteria criteria);

    Caja getUltimaCaja(long id_Empresa);

    int getUltimoNumeroDeCaja(long id_Empresa);

    Caja guardar(Caja caja);

    void validarCaja(Caja caja);

    byte[] getReporteCaja(Caja caja, Long idEmpresa);

    Caja cerrarCajaAnterior(long idEmpresa);
    
    Caja cerrarCaja(long idCaja, double monto, long idUsuario);

}
