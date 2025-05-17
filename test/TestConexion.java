

import modelo.Material;
import modelo.MaterialDAO;
import util.ConexionBD;

import java.sql.Connection;
import java.util.List;

public class TestConexion {
    public static void main(String[] args) {
        // 1) Probar la conexión
        try (Connection conn = ConexionBD.getConnection()) {
            System.out.println("✅ Conexión exitosa: " + conn.getMetaData().getURL());
        } catch (Exception e) {
            System.err.println("❌ Error al conectar:");
            e.printStackTrace();
            return;
        }

        // 2) Probar el DAO de Material
        MaterialDAO dao = new MaterialDAO();
        List<Material> lista = dao.listar();
        System.out.println("Materiales recuperados: " + lista.size());
        for (Material m : lista) {
            System.out.println(" - [" + m.getId() + "] " + m.getTitulo()
                             + " | " + m.getAutor()
                             + " | " + m.getCategoria()
                             + " | " + m.getEstado()
                             + " | " + m.getFechaRegistro());
        }
    }
}


