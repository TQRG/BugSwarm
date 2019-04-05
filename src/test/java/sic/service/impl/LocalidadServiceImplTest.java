package sic.service.impl;

import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import sic.modelo.Localidad;
import sic.modelo.Provincia;
import sic.repository.ILocalidadRepository;
import sic.repository.jpa.LocalidadRepositoryJPAImpl;
import sic.service.ILocalidadService;
import sic.service.BusinessServiceException;
import sic.service.TipoDeOperacion;

public class LocalidadServiceImplTest {

    private ILocalidadService localidadService;
    private Provincia provincia;

    @Before
    public void setUp() {
        provincia = new Provincia();
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenNombreVacio() {
        Localidad localidad = new Localidad();
        localidad.setNombre("");
        ILocalidadRepository localidadRepository = new LocalidadRepositoryJPAImpl();
        localidadService = new LocalidadServiceImpl(localidadRepository);
        localidadService.validarOperacion(TipoDeOperacion.ALTA, localidad);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenProvinciaNull() {
        Localidad localidad = new Localidad();
        localidad.setNombre("Capital");
        ILocalidadRepository localidadRepository = new LocalidadRepositoryJPAImpl();
        localidadService = new LocalidadServiceImpl(localidadRepository);
        localidadService.validarOperacion(TipoDeOperacion.ALTA, localidad);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenNombreDuplicadoAlta() {
        Localidad localidad = new Localidad();
        localidad.setNombre("Capital");
        localidad.setProvincia(provincia);
        ILocalidadRepository localidadRepository = Mockito.mock(LocalidadRepositoryJPAImpl.class);
        when(localidadRepository.getLocalidadPorNombre("Capital", provincia)).thenReturn(localidad);
        localidadService = new LocalidadServiceImpl(localidadRepository);
        localidadService.validarOperacion(TipoDeOperacion.ALTA, localidad);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenNombreDuplicadoActualizacion() {
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
