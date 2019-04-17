package sic.builder;

import java.util.ArrayList;
import java.util.List;
import sic.modelo.Rol;
import sic.modelo.Usuario;

public class UsuarioBuilder {
    
    private long id_Usuario = 0L;
    private String nombre = "Daenerys Targaryen";
    private String password = "LaQueNoArde";
    private String token = "yJhbGci1NiIsInR5cCI6IkpXVCJ9.eyJub21icmUiOiJjZWNpbGlvIn0.MCfaorSC7Wdc8rSW7BJizasfzsm";
    private List<Rol> rol = new ArrayList<>();
    private boolean permisosAdministrador = true;
    private boolean eliminado = false;
    
    public Usuario build() {
        return new Usuario(id_Usuario, nombre, password, token, rol, permisosAdministrador, eliminado);
    }
    
    public UsuarioBuilder withId_Usuario(long idUsuario) {
        this.id_Usuario = idUsuario;
        return this;
    }
    
    public UsuarioBuilder withNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }
    
    public UsuarioBuilder withPassword(String password) {
        this.password = password;
        return this;
    }
    
    public UsuarioBuilder withToken(String token) {
        this.token = token;
        return this;
    }
    
    public UsuarioBuilder withRol(ArrayList<Rol> rol) {
        this.rol = rol;
        return this;
    }
    
    public UsuarioBuilder withPermisosAdministrador(boolean permisosAdministrador) {
        this.permisosAdministrador = permisosAdministrador;
        return this;
    }
    
    public UsuarioBuilder withEliminado(boolean eliminado) {
        this.eliminado = eliminado;
        return this;
    }
}
