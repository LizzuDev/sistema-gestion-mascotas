package gestionmascotas.md;
import gestionmascotas.dp.modelos.PersonalDP;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalMD {
    public List<PersonalDP> obtenerTodos() throws SQLException {
        List<PersonalDP> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ID_PERSONAL AS \"idPersonal\", cedula, nombres, apellidos, cargo, telefono, correo, clave, estado FROM Personal WHERE 1=1 AND estado_registro = 'ACT'");
        sql.append(" ORDER BY nombres, apellidos");
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new PersonalDP(rs.getInt("idPersonal"), rs.getString("cedula"), rs.getString("nombres"), rs.getString("apellidos"), rs.getString("cargo"), rs.getString("telefono"), rs.getString("correo"), rs.getString("clave"), rs.getString("estado")));
            }
        }
        return lista;
    }
    private boolean existePersonal(String cedula, String correo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Personal WHERE cedula = ? OR correo = ?";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula); ps.setString(2, correo);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        }
        return false;
    }
    public void insertar(PersonalDP p, int ejecutor) throws SQLException {
        if (existePersonal(p.getCedula(), p.getCorreo())) throw new SQLException("La Cédula o Correo ya están registrados.");
        String sql = "INSERT INTO Personal (cedula, nombres, apellidos, cargo, telefono, correo, clave, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getCedula()); ps.setString(2, p.getNombres()); ps.setString(3, p.getApellidos()); ps.setString(4, p.getCargo()); ps.setString(5, p.getTelefono()); ps.setString(6, p.getCorreo()); ps.setString(7, p.getClave()); ps.setString(8, p.getEstado());
                ps.executeUpdate();
                try (ResultSet rsGen = ps.getGeneratedKeys()) { if (rsGen.next()) p.setIdPersonal(rsGen.getInt(1)); }
                AuditoriaMD.registrarAuditoria(conn, "Personal", "INSERT", "Personal insertado ID: " + p.getIdPersonal());
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
    private boolean existeCorreo(String correo, String cedula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Personal WHERE correo = ? AND cedula != ?";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo); ps.setString(2, cedula);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        }
        return false;
    }
    public void actualizar(PersonalDP p, int ejecutor) throws SQLException {
        if (existeCorreo(p.getCorreo(), p.getCedula())) throw new SQLException("El Correo ya está en uso por otro usuario.");
        String sql = "UPDATE Personal SET cedula = ?, nombres = ?, apellidos = ?, cargo = ?, telefono = ?, correo = ?, clave = ?, estado = ? WHERE ID_PERSONAL = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getCedula()); ps.setString(2, p.getNombres()); ps.setString(3, p.getApellidos()); ps.setString(4, p.getCargo()); ps.setString(5, p.getTelefono()); ps.setString(6, p.getCorreo()); ps.setString(7, p.getClave()); ps.setString(8, p.getEstado()); ps.setInt(9, p.getIdPersonal());
                ps.executeUpdate();
                AuditoriaMD.registrarAuditoria(conn, "Personal", "UPDATE", "Personal modificado ID: " + p.getIdPersonal());
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
    private boolean tieneDependencias(int idPersonal) throws SQLException { return false; }
    public void eliminar(int idPersonal, int ejecutor) throws SQLException {
        if (tieneDependencias(idPersonal)) throw new SQLException("El personal tiene dependencias y no puede eliminarse.");
        String sql = "UPDATE Personal SET estado_registro = 'INA' WHERE ID_PERSONAL = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idPersonal);
                ps.executeUpdate();
                AuditoriaMD.registrarAuditoria(conn, "Personal", "DELETE (LOGICO)", "Personal inactivo ID: " + idPersonal);
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
    public PersonalDP autenticar(String cedula, String clave) throws SQLException {
        String sql = "SELECT ID_PERSONAL AS \"idPersonal\", cedula, nombres, apellidos, cargo, telefono, correo, clave, estado FROM Personal WHERE cedula = ? AND clave = ? AND estado = 'ACTIVO' AND estado_registro = 'ACT'";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula); ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new PersonalDP(rs.getInt("idPersonal"), rs.getString("cedula"), rs.getString("nombres"), rs.getString("apellidos"), rs.getString("cargo"), rs.getString("telefono"), rs.getString("correo"), rs.getString("clave"), rs.getString("estado"));
            }
        }
        return null;
    }
}
