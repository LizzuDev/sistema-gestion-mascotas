package gestionmascotas.md;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    private static String host;
    private static String port;
    private static String database;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("database.properties")) {
            props.load(in);
            host = props.getProperty("db.host");
            port = props.getProperty("db.port");
            database = props.getProperty("db.name");
            user = props.getProperty("db.user");
            password = props.getProperty("db.pass");
        } catch (IOException e) {
            System.err.println("Error. database.properties no encontrado.");
        }
    }

    public static Connection obtenerConexion() throws SQLException {
        if (host == null || port == null || database == null || user == null || password == null) {
            throw new SQLException("Error");
        }
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error", e);
        }
    }

    public static String getUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }

    public static String getUser() {
        return user;
    }
}
