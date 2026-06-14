package gestionmascotas.md;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditoriaMD {
    public static void registrarAuditoria(Connection conn, String tabla, String accion, String detalle) throws SQLException {
        String cedula = "SISTEMA";
        String rol = "N/A";
        if (gestionmascotas.dp.controladores.SesionUsuario.getUsuarioActual() != null) {
            cedula = gestionmascotas.dp.controladores.SesionUsuario.getUsuarioActual().getCedula();
            rol = gestionmascotas.dp.controladores.SesionUsuario.getUsuarioActual().getCargo();
        }
        String sql = "INSERT INTO LogAuditoria (usuario_identificacion, rol_usuario, tabla_afectada, accion, detalle) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setString(2, rol);
            ps.setString(3, tabla);
            ps.setString(4, accion);
            ps.setString(5, detalle);
            ps.executeUpdate();
        }
    }
}
