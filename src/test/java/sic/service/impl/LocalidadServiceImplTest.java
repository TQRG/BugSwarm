package sic.service.impl;

import java.util.ResourceBundle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit4.SpringRunner;
import sic.builder.ProvinciaBuilder;
import sic.modelo.Localidad;
import sic.modelo.Provincia;
import sic.repository.ILocalidadRepository;
import sic.repository.jpa.LocalidadRepositoryJPAImpl;
import sic.service.BusinessServiceException;
import sic.modelo.TipoDeOperacion;

@RunWith(SpringRunner.class)
public class LocalidadServiceImplTest {

    @InjectMocks
    private LocalidadServiceImpl localidadService;   
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldValidarOperacionWhenNombreVacio() {
        thrown.expect(BusinessServiceException.class);
        thrown.expectMessage(ResourceBundle.getBundle("Mensajes").getString("mensaje_localidad_vacio_nombre"));
        Localidad localidad = new Localidad();
        localidad.setNombre("");
        ILocalidadRepository localidadRepository = new LocalidadRepositoryJPAImpl();
        localidadService = new LocalidadServiceImpl(localidadRepository);
        localidadService.validarOperacion(TipoDeOperacion.ALTA, localidad);
    }

    @Test
    public void shouldValidarOperacionWhenProvinciaNull() {
        thrown.expect(BusinessServiceException.class);
        thrown.expectMessage(ResourceBundle.getBundle("Mensajes").getString("mensaje_localidad_provincia_vacio"));
        Localidad localidad = new Localidad();
        localidad.setNombre("Capital");
        ILocalidadRepository localidadRepository = new LocalidadRepositoryJPAImpl();
        localidadService = new LocalidadServiceImpl(localidadRepository);
        localidadService.validarOperacion(TipoDeOperacion.ALTA, localidad);
    }

    @Test
    public void shouldValidarOperacionWhenNombreDuplicadoAlta() {
        thrown.expect(BusinessServiceException.class);
        thrown.expectMessage(ResourceBundle.getBundle("Mensajes").getString("mensaje_localidad_duplicado_nombre"));
        Provincia provincia = new ProvinciaBuilder().build();
        Localidad localidad = new Localidad();
        localidad.setNombre("Capital");
        localidad.setProvincia(provincia);
        ILocalidadRepository localidadRepository = Mockito.mock(LocalidadRepositoryJPAImpl.class);
        when(localidadRepository.getLocalidadPorNombre("Capital", provincia)).thenReturn(localidad);
        localidadService = new LocalidadServiceImpl(localidadRepository);
        localidadService.validarOperacion(TipoDeOperacion.ALTA, localidad);
    }

    @Test
    public void shouldValidarOperacionWhenNombreDuplicadoActualizacion() {
        thrown.expect(BusinessServiceException.class);
        thrown.expectMessage(ResourceBundle.getBundle("Mensajes").getString("mensaje_localidad_duplicado_nombre"));
        Provincia provincia = new ProvinciaBuilder().build();
        Localidad localidad = new Localidad();
        localidad.setNombre("Capital");
        localidad.setProvincia(provincia);
        localidad.setId_Localidad(Long.MIN_VALUE);
        ILocalidadRepository localidadRepository = Mockito.mock(LocalidadRepositoryJPAImpl.class);
        when(localidadRepository.getLocalidadPorNombre("Capital", provincia)).thenReturn(localidad);
        Localidad localidadDuplicada = new Localidad();
        localidadDuplicada.setNombre("Capital");
        localidadDuplicada.setProvincia(provincia);
        localidadDuplicada.setId_Localidad(Long.MAX_VALUE);
        localidadService = new LocalidadServiceImpl(localidadRepository);
        localidadService.validarOperacion(TipoDeOperacion.ACTUALIZACION, localidadDuplicada);
    }

}
