package modelo;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


 // MaterialDAO: operaciones CRUD para la tabla 'material' de la BD
 // Esta tabla almacena informacion de Libros, Revistas y Materiales Audiovisuales
 
public class MaterialDAO {
    private static final Logger LOGGER = LogManager.getLogger(MaterialDAO.class);

    // Devuelve todos los materiales registrados desde la BD
    public List<Material> listar() {
        List<Material> lista = new ArrayList<>();
        String sql = "SELECT * FROM material";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // cada fila del resultado se convierte en un objeto Material
            while (rs.next()) {
                Material m = mapRow(rs);
                lista.add(m);
            }
        } catch (SQLException e) {
            LOGGER.error("Error al listar materiales", e);
        }
        return lista;
    }

    // Inserta un nuevo material 
    public boolean agregar(Material m) {
        String sql = "INSERT INTO material (titulo, tipo_material, autor, categoria, estado, ISBN, edicion, fecha_publicacion, " +
                     "ISSN, numero_paginas, duracion_minutos, anio) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //parametros
            ps.setString(1, m.getTitulo());
            ps.setString(2, m.getTipoMaterial());
            ps.setString(3, m.getAutor());
            ps.setString(4, m.getCategoria());
            ps.setString(5, m.getEstado());
            ps.setString(6, m.getISBN());
            ps.setString(7, m.getEdicion());
            if (m.getFechaPublicacion() != null) {
                ps.setDate(8, Date.valueOf(m.getFechaPublicacion()));
            } else {
                ps.setNull(8, Types.DATE);
            }

            ps.setString(9, m.getIssn());
            ps.setObject(10, m.getNumeroPaginas(), Types.INTEGER);
            ps.setObject(11, m.getDuracionMinutos(), Types.INTEGER);
            ps.setObject(12, m.getAnio(), Types.INTEGER);
            
            // Aqui se ejecuta la insercion 
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    m.setId(keys.getInt(1)); // asigna un ID 
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error("Error al agregar material", e);
        }
        return false;
    }

    // Actualiza un material existente
    public boolean actualizar(Material m) {
        String sql = "UPDATE material SET titulo=?, tipo_material=?, autor=?, categoria=?, estado=?, ISBN=?, edicion=?, fecha_publicacion=?, " +
                     "ISSN=?, numero_paginas=?, duracion_minutos=?, anio=? WHERE id=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getTitulo());
            ps.setString(2, m.getTipoMaterial());
            ps.setString(3, m.getAutor());
            ps.setString(4, m.getCategoria());
            ps.setString(5, m.getEstado());
            ps.setString(6, m.getISBN());
            ps.setString(7, m.getEdicion());
            if (m.getFechaPublicacion() != null) {
                ps.setDate(8, Date.valueOf(m.getFechaPublicacion()));
            } else {
                ps.setNull(8, Types.DATE);
            }

            ps.setString(9, m.getIssn());
            ps.setObject(10, m.getNumeroPaginas(), Types.INTEGER);
            ps.setObject(11, m.getDuracionMinutos(), Types.INTEGER);
            ps.setObject(12, m.getAnio(), Types.INTEGER);
            ps.setInt(13, m.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar material", e);
        }
        return false;
    }

    // Elimina un material por su ID 
    public boolean eliminar(int id) {
        String sql = "DELETE FROM material WHERE id=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al eliminar material", e);
        }
        return false;
    }

    // Devuelve un material por su ID 
    public Material findById(int id) {
        String sql = "SELECT * FROM material WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al buscar material por ID", e);
        }
        return null;
    }

    // Convierte una fila del ResultSet en un objeto Material
    
    private Material mapRow(ResultSet rs) throws SQLException {
        Material m = new Material();
        m.setId(rs.getInt("id"));
        m.setTitulo(rs.getString("titulo"));
        m.setTipoMaterial(rs.getString("tipo_material"));
        m.setAutor(rs.getString("autor"));
        m.setCategoria(rs.getString("categoria"));
        m.setEstado(rs.getString("estado"));
        m.setISBN(rs.getString("ISBN"));
        m.setEdicion(rs.getString("edicion"));

        Date fechaSql = rs.getDate("fecha_publicacion");
        m.setFechaPublicacion(fechaSql != null ? fechaSql.toLocalDate() : null);
        //campos adicionales segun rol
        String tipo = m.getTipoMaterial();
        if ("Revista".equalsIgnoreCase(tipo)) {
            m.setIssn(rs.getString("ISSN"));
            m.setNumeroPaginas(rs.getObject("numero_paginas") != null ? rs.getInt("numero_paginas") : null);
        } else if ("AV".equalsIgnoreCase(tipo)) {
            m.setDuracionMinutos(rs.getObject("duracion_minutos") != null ? rs.getInt("duracion_minutos") : null);
            m.setAnio(rs.getObject("anio") != null ? rs.getInt("anio") : null);
        }

        return m;
    }
}





