package sic.builder;

import sic.modelo.Usuario;

public class UsuarioBuilder {
    
    private long id_Usuario = 0L;
    private String nombre = "Daenerys Targaryen";
    private String password = "LaQueNoArde";
    private boolean permisosAdministrador = true;
    private boolean eliminado = false;
    
    public Usuario build() {
        return new Usuario(id_Usuario, nombre, password, permisosAdministrador, eliminado);
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
    
    public UsuarioBuilder withPermisosAdministrador(boolean permisosAdministrador) {
        this.permisosAdministrador = permisosAdministrador;
        return this;
    }
    
    public UsuarioBuilder withEliminado(boolean eliminado) {
        this.eliminado = eliminado;
        return this;
    }
}
