package sic.repository;

import java.util.Date;
import java.util.List;
import sic.modelo.Pago;

public interface IPagoRepository {
    
    Pago getPagoPorId(long id_Pago);

    List<Pago> getPagosDeLaFactura(long idFactura);
    
    List<Pago> getPagosEntreFechasYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta);

    long getMayorNroPago(long idEmpresa);
    
    Pago guardar(Pago pago);
    
    void actualizar(Pago pago);
}
