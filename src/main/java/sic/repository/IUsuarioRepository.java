package sic.repository;

import java.util.List;
import sic.modelo.Usuario;

public interface IUsuarioRepository {

    Usuario getUsuarioPorId(Long idUsuario);
    
    void actualizar(Usuario usuario);

    Usuario getUsuarioPorNombre(String nombre);

    Usuario getUsuarioPorNombreContrasenia(String nombre, String contrasenia);

    List<Usuario> getUsuarios();

    List<Usuario> getUsuariosAdministradores();

    Usuario guardar(Usuario usuario);
    
}
