package gestionmascotas.md;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Acceso a Datos de Auditoría (Manejo de Datos - MD).
 * Sigue el estándar STD-06: Registrar el ID del personal ejecutor y la marca de tiempo.
 */
public class AuditoriaMD {

    /**
     * Registra una acción en la tabla transversal Auditoria.
     */
    public static void registrarAuditoria(Connection conn, int idPersonal, String accion, String detalle) throws SQLException {
        String sql = "INSERT INTO Auditoria (idPersonal, accion, detalle, fechaRegistro) VALUES (?, ?, ?, ?)";
        boolean esConexionExterna = (conn != null);
        Connection localConn = esConexionExterna ? conn : ConexionBD.obtenerConexion();
        
        try (PreparedStatement ps = localConn.prepareStatement(sql)) {
            ps.setInt(1, idPersonal);
            ps.setString(2, accion);
            ps.setString(3, detalle);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } finally {
            if (!esConexionExterna) {
                localConn.close(); // Liberar si fue abierta localmente
            }
        }
    }
}
