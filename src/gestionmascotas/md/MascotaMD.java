package gestionmascotas.md;
import gestionmascotas.dp.modelos.MascotaDP;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MascotaMD {
    public List<MascotaDP> obtenerTodas() throws SQLException {
        List<MascotaDP> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ID_MASCOTA AS \"idMascota\", ID_CENTRO AS \"idCentro\", nombre, especie, fechaNacimiento, raza, estado FROM Mascota WHERE 1=1 AND estado_registro = 'ACT'");
        sql.append(" ORDER BY ID_MASCOTA DESC");
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new MascotaDP(rs.getInt("idMascota"), rs.getInt("idCentro"), rs.getString("nombre"), rs.getString("especie"), rs.getDate("fechaNacimiento"), rs.getString("raza"), rs.getString("estado")));
        }
        return lista;
    }
    public boolean verificarCapacidadSede(int idCentro) throws SQLException {
        String sql = "SELECT capacidadMaxima, capacidadActual FROM CentroAdopcion WHERE ID_CENTRO = ?";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCentro);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt("capacidadActual") < rs.getInt("capacidadMaxima"); }
        }
        return false;
    }
    public void insertar(MascotaDP m, int ejecutor) throws SQLException {
        String sql = "INSERT INTO Mascota (ID_CENTRO, nombre, especie, fechaNacimiento, raza, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, m.getIdCentro()); ps.setString(2, m.getNombre()); ps.setString(3, m.getEspecie()); ps.setDate(4, m.getFechaNacimiento()); ps.setString(5, m.getRaza()); ps.setString(6, m.getEstadoAdopcion());
                ps.executeUpdate();
                try (ResultSet rsGen = ps.getGeneratedKeys()) { if (rsGen.next()) m.setIdMascota(rsGen.getInt(1)); }
                AuditoriaMD.registrarAuditoria(conn, "Mascota", "INSERT", "Mascota insertada ID: " + m.getIdMascota());
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
    public void actualizar(MascotaDP m, int ejecutor) throws SQLException {
        String sql = "UPDATE Mascota SET nombre = ?, especie = ?, fechaNacimiento = ?, raza = ?, estado = ?, ID_CENTRO = ? WHERE ID_MASCOTA = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, m.getNombre()); ps.setString(2, m.getEspecie()); ps.setDate(3, m.getFechaNacimiento()); ps.setString(4, m.getRaza()); ps.setString(5, m.getEstadoAdopcion()); ps.setInt(6, m.getIdCentro()); ps.setInt(7, m.getIdMascota());
                ps.executeUpdate();
                AuditoriaMD.registrarAuditoria(conn, "Mascota", "UPDATE", "Mascota modificada ID: " + m.getIdMascota());
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
    public void eliminar(int idMascota, int idCentro, int ejecutor) throws SQLException {
        String sql = "UPDATE Mascota SET estado_registro = 'INA' WHERE ID_MASCOTA = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idMascota);
                ps.executeUpdate();
                AuditoriaMD.registrarAuditoria(conn, "Mascota", "DELETE (LOGICO)", "Mascota inactiva ID: " + idMascota);
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }
}
