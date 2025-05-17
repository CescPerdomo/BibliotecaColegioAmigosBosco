package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConexionBD {
    private static final Logger LOGGER = LogManager.getLogger(ConexionBD.class);

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USUARIO = "root";
    private static final String CLAVE = "1234";
    
    public static Connection getConnection() throws SQLException {
    try {
        LOGGER.debug("Cargando driver JDBC MySQL");
        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection conn = DriverManager.getConnection(URL, USUARIO, CLAVE);
        LOGGER.info("Conexión establecida a la BD: " + URL);
        return conn;
    } catch (ClassNotFoundException e) {
        LOGGER.error("Driver JDBC no encontrado", e);
        throw new SQLException("Driver JDBC no encontrado.", e);
    } catch (SQLException e) {
        LOGGER.error("Error al conectarse a la BD: " + URL, e);
        throw e;
    }
}
    
    public static void closeConnection(Connection connection) {
    if (connection != null) {
        try {
            connection.close();
            LOGGER.info("Conexión cerrada exitosamente.");
        } catch (SQLException e) {
            LOGGER.error("Error al cerrar la conexión.", e);
        }
    }
}
    
    
}
