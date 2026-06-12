package gestionmascotas.md;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor de Conexión a la Base de Datos PostgreSQL.
 * Sigue el estándar de la Capa de Manejo de Datos (MD).
 */
public class ConexionBD {
    // Valores por defecto para desarrollo local (editables por el equipo)
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "5432";
    private static final String DEFAULT_DATABASE = "sistema_mascotas";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";

    private static String host = DEFAULT_HOST;
    private static String port = DEFAULT_PORT;
    private static String database = DEFAULT_DATABASE;
    private static String user = DEFAULT_USER;
    private static String password = DEFAULT_PASSWORD;

    static {
        // Permitir configurar mediante variables de entorno si el equipo lo requiere
        if (System.getenv("DB_HOST") != null) host = System.getenv("DB_HOST");
        if (System.getenv("DB_PORT") != null) port = System.getenv("DB_PORT");
        if (System.getenv("DB_NAME") != null) database = System.getenv("DB_NAME");
        if (System.getenv("DB_USER") != null) user = System.getenv("DB_USER");
        if (System.getenv("DB_PASS") != null) password = System.getenv("DB_PASS");
    }

    /**
     * Obtiene una nueva conexión a la base de datos PostgreSQL.
     * @return Connection objeto de conexión JDBC.
     * @throws SQLException si la conexión falla (capturada según STD-09).
     */
    public static Connection obtenerConexion() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        try {
            // Cargar el driver JDBC de PostgreSQL
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de PostgreSQL en el Classpath.", e);
        }
    }

    // Métodos para cambiar credenciales en tiempo de ejecución (para pruebas de la UI)
    public static void configurarCredenciales(String h, String p, String db, String u, String pass) {
        host = h;
        port = p;
        database = db;
        user = u;
        password = pass;
    }

    public static String getUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }

    public static String getUser() {
        return user;
    }
}
