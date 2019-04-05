package sic.modelo;

import lombok.Data;

@Data
public class EmpresaActiva {

    private static final EmpresaActiva INSTANCE = new EmpresaActiva();
    private Empresa empresa;

    public static EmpresaActiva getInstance() {
        return INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
