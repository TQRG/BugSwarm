package sic.service.impl;

import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Empresa;
import sic.modelo.FormaDePago;
import sic.repository.IFormaDePagoRepository;
import sic.service.IFormaDePagoService;
import sic.service.BusinessServiceException;
import sic.util.Validator;

@Service
public class FormaDePagoServiceImpl implements IFormaDePagoService {

    private final IFormaDePagoRepository formaDePagoRepository;
    private static final Logger LOGGER = Logger.getLogger(FormaDePagoServiceImpl.class.getPackage().getName());

    @Autowired
    public FormaDePagoServiceImpl(IFormaDePagoRepository formaDePagoRepository) {
        this.formaDePagoRepository = formaDePagoRepository;
    }

    @Override
    public List<FormaDePago> getFormasDePago(Empresa empresa) {
        return formaDePagoRepository.getFormasDePago(empresa);
    }

    @Override
    public FormaDePago getFormasDePagoPorId(long idFormaDePago) {
        FormaDePago formaDePago = formaDePagoRepository.getFormaDePagoPorId(idFormaDePago);
        if (formaDePago == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_formaDePago_no_existente"));
        }
        return formaDePago;
    }

    @Override
    public FormaDePago getFormaDePagoPredeterminada(Empresa empresa) {
        return formaDePagoRepository.getFormaDePagoPredeterminado(empresa);
    }

    @Override
    @Transactional
    public void setFormaDePagoPredeterminada(FormaDePago formaDePago) {
        //antes de setear como predeterminado, busca si ya existe
        //otro como predeterminado y cambia su estado.
        FormaDePago formaPredeterminadaAnterior = formaDePagoRepository.getFormaDePagoPredeterminado(formaDePago.getEmpresa());
        if (formaPredeterminadaAnterior != null) {
            formaPredeterminadaAnterior.setPredeterminado(false);
            formaDePagoRepository.actualizar(formaPredeterminadaAnterior);
        }
        formaDePago.setPredeterminado(true);
        formaDePagoRepository.actualizar(formaDePago);
    }

    private void validarOperacion(FormaDePago formaDePago) {
        //Requeridos
        if (Validator.esVacio(formaDePago.getNombre())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_formaDePago_vacio_nombre"));
        }
        if (formaDePago.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_formaDePago_empresa_vacio"));
        }
        //Duplicados
        //Nombre
        if (formaDePagoRepository.getFormaDePagoPorNombreYEmpresa(formaDePago.getNombre(), formaDePago.getEmpresa().getId_Empresa()) != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_formaDePago_duplicado_nombre"));
        }
        //Predeterminado 
        if(formaDePago.isPredeterminado() == this.getFormaDePagoPredeterminada(formaDePago.getEmpresa()).isPredeterminado()) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_formaDePago_predeterminada_existente"));
        }
    }

    @Override
    @Transactional
    public FormaDePago guardar(FormaDePago formaDePago) {
        this.validarOperacion(formaDePago);
        formaDePago = formaDePagoRepository.guardar(formaDePago);
        LOGGER.warn("La Forma de Pago " + formaDePago + " se guardó correctamente." );
        return formaDePago;
    }

    @Override
    @Transactional
    public void eliminar(long idFormaDePago) {
        FormaDePago formaDePago = this.getFormasDePagoPorId(idFormaDePago);
        if (formaDePago == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_formaDePago_no_existente"));
        }
        formaDePago.setEliminada(true);
        formaDePagoRepository.actualizar(formaDePago);
    }
    
    @Override
    @Transactional
    public FormaDePago getFormaDePagoPorNombreYEmpresa(String nombre, Long idEmpresa) {
        return formaDePagoRepository.getFormaDePagoPorNombreYEmpresa(nombre, idEmpresa);
    }
    
}
