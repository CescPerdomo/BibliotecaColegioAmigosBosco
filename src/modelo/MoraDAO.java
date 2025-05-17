package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import util.ConexionBD;

// Clase encargada de gestionar las operaciones CRUD sobre la tabla 'mora'
// Permite insertar moras, listar por usuario, listar todas y calcular el total acumulado por usuario

public class MoraDAO {
    private static final Logger LOGGER = Logger.getLogger(MoraDAO.class);

    public MoraDAO() {
        // Constructor solo por defecto no realiza ninguna accion se dejo vacio por temas de errores en el codigo
    }

    // Inserta una nueva mora en la BD
    // m Objeto Mora con los datos a registrar
    // true si se inserto correctamente false si hubo error
    
    public boolean insertar(Mora m) {
        String sql = "INSERT INTO mora (prestamo_id, usuario, id_material, dias_mora, monto_mora, fecha_calculo) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, m.getPrestamoId());
            ps.setString(2, m.getUsuario());
            ps.setInt(3, m.getIdMaterial());
            ps.setInt(4, m.getDiasMora());
            ps.setDouble(5, m.getMontoMora());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Error insertando mora", e);
        }
        return false;
    }
        //Retorna una lista de moras asociadas a un usuario especifico
        // retorna lista de objetos Mora encontrados
    
    public List<Mora> listarPorUsuario(String usuario) {
        List<Mora> lista = new ArrayList<>();
        String sql = "SELECT * FROM mora WHERE usuario = ? ORDER BY fecha_calculo DESC";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mora m = new Mora();
                    m.setId(rs.getInt("id"));
                    m.setPrestamoId(rs.getInt("prestamo_id"));
                    m.setUsuario(rs.getString("usuario"));
                    m.setIdMaterial(rs.getInt("id_material"));
                    m.setDiasMora(rs.getInt("dias_mora"));
                    m.setMontoMora(rs.getDouble("monto_mora"));
                    m.setFechaCalculo(rs.getTimestamp("fecha_calculo"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error listando moras por usuario", e);
        }
        return lista;
    }
        // Retorna una lista con todas las moras registradas en la base de datos
    
    public List<Mora> listarTodas() {
        List<Mora> lista = new ArrayList<>();
        String sql = "SELECT * FROM mora ORDER BY fecha_calculo DESC";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mora m = new Mora();
                m.setId(rs.getInt("id"));
                m.setPrestamoId(rs.getInt("prestamo_id"));
                m.setUsuario(rs.getString("usuario"));
                m.setIdMaterial(rs.getInt("id_material"));
                m.setDiasMora(rs.getInt("dias_mora"));
                m.setMontoMora(rs.getDouble("monto_mora"));
                m.setFechaCalculo(rs.getTimestamp("fecha_calculo"));
                lista.add(m);
            }
        } catch (SQLException e) {
            LOGGER.error("Error listando todas las moras", e);
        }
        return lista;
    }
    
        // Calcula el total acumulado de mora para un usuario especifico
    
    public double obtenerTotalPorUsuario(String usuario) {
        String sql = "SELECT SUM(monto_mora) FROM mora WHERE usuario = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error obteniendo total de mora para " + usuario, e);
        }
        return 0.0;
    }
}

