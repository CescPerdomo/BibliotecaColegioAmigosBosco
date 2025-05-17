
package modelo;

import util.ConexionBD;
import java.sql.*;
import modelo.Role; // se importa el enum Role que define los tipos de usuario
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.SQLException;


public class AuthDAO {
    private static final Logger LOGGER = LogManager.getLogger(AuthDAO.class);


    // Se hace Comprobacion de usuario+contraseña //
    public boolean autenticarUsuario(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // parametros para evitar inyecciones SQL
            stmt.setString(1, usuario);
            stmt.setString(2, contrasena); // en un sistema real se debe almacenar la contraseña como hash en la base de datos
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true si hay resultados es decir si las credenciales coinciden
            }
        } catch (SQLException e) {
            LOGGER.error("Error autenticando usuario=" + usuario, e);
            return false;
        }
    }

    //Recuperar el rol del usuario como Role desde la BD
    public Role obtenerRol(String usuario) {
        String sql = "SELECT tipo_usuario FROM usuarios WHERE usuario = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // En la BD debe retornar como 'tipo_usuario' contiene "ADMIN", "PROFESOR" o "ALUMNO"
                    return Role.valueOf(rs.getString("tipo_usuario"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error obteniendo rol para usuario=" + usuario, e);
        }
        // Devuelve null si no se encuentra el usuario o si ocurre un error
        return null;
    }
}
