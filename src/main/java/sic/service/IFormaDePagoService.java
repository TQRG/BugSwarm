package sic.service;

import java.util.List;
import sic.modelo.Empresa;
import sic.modelo.FormaDePago;

public interface IFormaDePagoService {

    void eliminar(FormaDePago formaDePago);

    FormaDePago getFormaDePagoPredeterminada(Empresa empresa);

    List<FormaDePago> getFormasDePago(Empresa empresa);

    FormaDePago getFormasDePagoPorId(long id);

    void guardar(FormaDePago formaDePago);

    void setFormaDePagoPredeterminada(FormaDePago formaDePago);

}
