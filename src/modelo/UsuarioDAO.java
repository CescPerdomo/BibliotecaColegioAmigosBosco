package modelo;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Encargado del acceso a datos para Usuario
// Maneja todas las operaciones CRUD sobre la tabla 'usuarios'

public class UsuarioDAO {
    private static final Logger LOGGER = LogManager.getLogger(UsuarioDAO.class);

    // Lista todos los usuarios registrados en la BD
    
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, usuario, contrasena, tipo_usuario, ejemplares_prestados FROM usuarios";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(Role.valueOf(rs.getString("tipo_usuario")));
                u.setEjemplares_prestados(rs.getInt("ejemplares_prestados"));
                lista.add(u);
            }
        } catch (SQLException e) {
            LOGGER.error("Error listando usuarios", e);
        }
        return lista;
    }
            // Agrega un nuevo usuario al sistema (rol ADMIN)
            // u objeto Usuario con los datos
            // true si se inserto correctamente
    
    public boolean agregar(Usuario u) {
        String sql = "INSERT INTO usuarios (usuario, contrasena, tipo_usuario, ejemplares_prestados) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getRol().name());
            ps.setInt(4, u.getEjemplares_prestados());
            int filas = ps.executeUpdate();
            if (filas == 1) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) u.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error("Error agregar usuarios", e);
        }
        return false;
    }
        // Actualiza los datos de un usuario existente
            // u objeto Usuario actualizado
                   // true si se actualizo correctamente
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET contrasena = ?, tipo_usuario = ?, ejemplares_prestados = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getContrasena());
            ps.setString(2, u.getRol().name());
            ps.setInt(3, u.getEjemplares_prestados());
            ps.setInt(4, u.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar usuario", e);
        }
        return false;
    }
            // Elimina un usuario por su ID
                // id identificador del usuario
                    // true si fue eliminado correctamente
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error eliminar usuario", e);
        }
        return false;
    }
        // Restablece la contraseña de un usuario (misma logica)
    public boolean resetearContrasena(int id, String nuevaContrasena) {
        String sql = "UPDATE usuarios SET contrasena = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevaContrasena);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.error("Error reseteando contraseña para usuario id=" + id, e);
            return false;
        }
    }
        // Busca un usuario por su nombre
    public Usuario buscarPorNombre(String nombre) {
        Usuario u = null;
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(Role.valueOf(rs.getString("tipo_usuario")));
                u.setEjemplares_prestados(rs.getInt("ejemplares_prestados"));
            }
        } catch (SQLException e) {
            LOGGER.error("Error buscando usuario por nombre: " + nombre, e);
        }
        return u;
    }
        // Obtiene el rol de un usuario por su nombre
    public Role obtenerRol(String usuario) {
        String sql = "SELECT tipo_usuario FROM usuarios WHERE usuario = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Role.valueOf(rs.getString("tipo_usuario"));
            }
        } catch (SQLException e) {
            LOGGER.error("Error obteniendo rol del usuario " + usuario, e);
        }
        return null;
    }
}






