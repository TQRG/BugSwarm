package sic.service;

import java.util.List;
import sic.modelo.Usuario;
import sic.modelo.UsuarioActivo;

public interface IUsuarioService {

    void actualizar(Usuario usuario);

    void eliminar(Usuario usuario);

    UsuarioActivo getUsuarioActivo();

    Usuario getUsuarioPorNombre(String nombre);

    Usuario getUsuarioPorNombreContrasenia(String nombre, String contrasenia);

    List<Usuario> getUsuarios();

    List<Usuario> getUsuariosAdministradores();

    void guardar(Usuario usuario);

    void setUsuarioActivo(Usuario usuario);

    Usuario validarUsuario(String nombre, String password);
    
}
