package gestionmascotas.md;
import gestionmascotas.dp.modelos.VeterinarioDP;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioMD {
    public List<VeterinarioDP> obtenerTodos() throws SQLException {
        List<VeterinarioDP> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ID_VETERINARIO AS \"idVeterinario\", cedula, nombres, apellidos, especialidad, licencia, telefono FROM Veterinario WHERE 1=1 AND estado_registro = 'ACT'");
        sql.append(" ORDER BY nombres, apellidos");
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new VeterinarioDP(rs.getInt("idVeterinario"), rs.getString("cedula"), rs.getString("nombres"), rs.getString("apellidos"), rs.getString("especialidad"), rs.getString("licencia"), rs.getString("telefono")));
        }
        return lista;
    }
    private boolean existeVeterinario(String cedula, String licencia) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Veterinario WHERE cedula = ? OR licencia = ?";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula); ps.setString(2, licencia);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        }
        return false;
    }
    public void insertar(VeterinarioDP v, int ejecutor) throws SQLException {
        if (existeVeterinario(v.getCedula(), v.getLicencia())) throw new SQLException("La Cédula o Licencia ya están registradas.");
        String sql = "INSERT INTO Veterinario (cedula, nombres, apellidos, especialidad, licencia, telefono) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, v.getCedula()); ps.setString(2, v.getNombres()); ps.setString(3, v.getApellidos()); ps.setString(4, v.getEspecialidad()); ps.setString(5, v.getLicencia()); ps.setString(6, v.getTelefono());
                ps.executeUpdate();
                try (ResultSet rsGen = ps.getGeneratedKeys()) { if (rsGen.next()) v.setIdVeterinario(rsGen.getInt(1)); }
                AuditoriaMD.registrarAuditoria(conn, "Veterinario", "INSERT", "Veterinario insertado ID: " + v.getIdVeterinario());
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
    private boolean existeLicencia(String licencia, String cedula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Veterinario WHERE licencia = ? AND cedula != ?";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licencia); ps.setString(2, cedula);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        }
        return false;
    }
    public void actualizar(VeterinarioDP v, int ejecutor) throws SQLException {
        if (existeLicencia(v.getLicencia(), v.getCedula())) throw new SQLException("La Licencia ya está en uso.");
        String sql = "UPDATE Veterinario SET cedula = ?, nombres = ?, apellidos = ?, especialidad = ?, licencia = ?, telefono = ? WHERE ID_VETERINARIO = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, v.getCedula()); ps.setString(2, v.getNombres()); ps.setString(3, v.getApellidos()); ps.setString(4, v.getEspecialidad()); ps.setString(5, v.getLicencia()); ps.setString(6, v.getTelefono()); ps.setInt(7, v.getIdVeterinario());
                ps.executeUpdate();
                AuditoriaMD.registrarAuditoria(conn, "Veterinario", "UPDATE", "Veterinario modificado ID: " + v.getIdVeterinario());
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
    private boolean tieneDependencias(int idVeterinario) throws SQLException { return false; }
    public void eliminar(int idVeterinario, int ejecutor) throws SQLException {
        if (tieneDependencias(idVeterinario)) throw new SQLException("El veterinario tiene dependencias.");
        String sql = "UPDATE Veterinario SET estado_registro = 'INA' WHERE ID_VETERINARIO = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idVeterinario);
                ps.executeUpdate();
                AuditoriaMD.registrarAuditoria(conn, "Veterinario", "DELETE (LOGICO)", "Veterinario inactivo ID: " + idVeterinario);
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
}
