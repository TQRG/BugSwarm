package sic.repository;

import java.util.Date;
import java.util.List;
import sic.modelo.Factura;
import sic.modelo.Pago;

public interface IPagoRepository {
    
    Pago getPagoPorId(long id_Pago);

    List<Pago> getPagosDeLaFactura(Factura factura);
    
    List<Pago> getPagosEntreFechasYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta);

    void guardar(Pago pago);
    
    void actualizar(Pago pago);
}
