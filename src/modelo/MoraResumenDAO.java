package modelo;

import util.ConexionBD;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoraResumenDAO {
    private static final Logger LOGGER = LogManager.getLogger(MoraResumenDAO.class);

    public boolean insertarOActualizar(String usuario, double totalMora) {
        String sql = "INSERT INTO mora_resumen (usuario, total_mora) VALUES (?, ?) ON DUPLICATE KEY UPDATE total_mora = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setDouble(2, totalMora);
            ps.setDouble(3, totalMora);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error actualizando mora_resumen para " + usuario, e);
        }
        return false;
    }

    public boolean eliminar(String usuario) {
        String sql = "DELETE FROM mora_resumen WHERE usuario = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error eliminando mora_resumen para " + usuario, e);
        }
        return false;
    }

    public double obtenerTotal(String usuario) {
        String sql = "SELECT total_mora FROM mora_resumen WHERE usuario = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total_mora");
        } catch (SQLException e) {
            LOGGER.error("Error consultando total_mora de " + usuario, e);
        }
        return 0.0;
    }
}
