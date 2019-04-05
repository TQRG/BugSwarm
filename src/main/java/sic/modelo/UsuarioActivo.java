package sic.modelo;

import java.util.Date;
import lombok.Data;

@Data
public class UsuarioActivo {

    private static final UsuarioActivo INSTANCE = new UsuarioActivo();
    private Usuario usuario;
    private Date fechaHoraIngreso;

    public static UsuarioActivo getInstance() {
        return INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
