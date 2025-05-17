package modelo;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Maneja la logica de acceso a datos de la tabla 'prestamo'
// Se encarga de registrar, actualizar, eliminar y consultar préstamos

public class PrestamoDAO {
    private static final Logger LOGGER = LogManager.getLogger(PrestamoDAO.class);

    // Retorna una lista con todos los prestamos ordenados por fecha de prestamo (mas recientes primero)
    
    public List<Prestamo> listar() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamo ORDER BY fecha_prestamo DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setIdMaterial(rs.getInt("id_material"));
                p.setUsuarioPresta(rs.getString("usuario_presta"));
                p.setFechaPrestamo(rs.getTimestamp("fecha_prestamo"));
                p.setFechaDevolucion(rs.getTimestamp("fecha_devolucion"));
                p.setEstado(rs.getString("estado"));
                lista.add(p);
            }
        } catch (SQLException e) {
            LOGGER.error("Error listando préstamos", e);
        }
        return lista;
    }
        // Actualiza la fecha de devolucion y estado de un prestamo ya registrado
    
    public boolean actualizar(Prestamo p) {
        String sql = "UPDATE prestamo SET fecha_devolucion = ?, estado = ?, fecha_prestamo = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, p.getFechaDevolucion());
            ps.setString(2, p.getEstado());
            ps.setTimestamp(3, p.getFechaPrestamo());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Error actualizando préstamo", e);
        }
        return false;
    }
            // Elimina un prestamo de la BD
    public boolean eliminar(int id) {
        String sql = "DELETE FROM prestamo WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Error eliminando préstamo", e);
        }
        return false;
    }

            // Inserta un nuevo prestamo en la BD
                // p Objeto Prestamo con los datos a registrar
                    // true si fue insertado correctamente
    
    public boolean agregar(Prestamo p) {
        String sql = "INSERT INTO prestamo (id_material, usuario_presta, fecha_prestamo, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdMaterial());
            ps.setString(2, p.getUsuarioPresta());
            ps.setTimestamp(3, p.getFechaPrestamo());
            ps.setString(4, p.getEstado());
            int filas = ps.executeUpdate();

            if (filas == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            LOGGER.error("Error agregando préstamo", e);
        }
        return false;
    }
            // Contador que lleva un recuento de prestamos activos que tiene un usuario actualmente
    
    public int countActiveByUser(String usuario) {
        String sql = "SELECT COUNT(*) FROM prestamo WHERE usuario_presta = ? AND estado = 'ACTIVO'";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            LOGGER.error("Error contando préstamos activos", e);
        }
        return 0;
    }

    public Prestamo buscarPorId(int id) {
        String sql = "SELECT * FROM prestamo WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setIdMaterial(rs.getInt("id_material"));
                p.setUsuarioPresta(rs.getString("usuario_presta"));
                p.setFechaPrestamo(rs.getTimestamp("fecha_prestamo"));
                p.setFechaDevolucion(rs.getTimestamp("fecha_devolucion"));
                p.setEstado(rs.getString("estado"));
                return p;
            }
        } catch (SQLException e) {
            LOGGER.error("Error buscando préstamo por ID", e);
        }
        return null;
    }
        // Cuenta todos los prestamos activos o renovados de un usuario (util para mostrar en panelmora)
    
    public int contarEjemplaresPrestadosPorUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM prestamo WHERE usuario_presta = ? AND estado IN ('ACTIVO', 'RENOVADO')";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error contando ejemplares prestados para el usuario " + usuario, e);
        }
        return 0;
    }

    public List<Prestamo> buscar(String term) {
        throw new UnsupportedOperationException("");
    }
}




