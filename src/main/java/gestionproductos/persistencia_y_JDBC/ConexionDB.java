package gestionproductos.persistencia_y_JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Configuración de la db local
    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_productos";
    private static final String USER = "postgres";
    private static final String PASSWORD = "sql";

    /**
     // Establece y retorna una nueva conexión activa hacia PostgreSQL.
     * @return Objeto Connection
     * @throws SQLException si falla la autenticación o el servidor no responde
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}