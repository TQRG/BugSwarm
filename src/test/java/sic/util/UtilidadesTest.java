package sic.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class UtilidadesTest {

    @Test
    public void shouldTruncarDecimal() {
        double resultadoEsperado = 22.22;
        double resultadoObtenido = Utilidades.truncarDecimal(22.22959446846487, 2);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

}
