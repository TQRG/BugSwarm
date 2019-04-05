package sic.repository;

import java.util.List;
import sic.modelo.Empresa;
import sic.modelo.FormaDePago;

public interface IFormaDePagoRepository {

    void actualizar(FormaDePago formaDePago);

    FormaDePago getFormaDePagoPorId(long id);

    FormaDePago getFormaDePagoPorNombre(String nombre, Empresa empresa);

    FormaDePago getFormaDePagoPredeterminado(Empresa empresa);

    List<FormaDePago> getFormasDePago(Empresa empresa);

    void guardar(FormaDePago formaDePago);
    
}
