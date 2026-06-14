package gestionmascotas.md;
import gestionmascotas.dp.modelos.AdoptanteDP;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdoptanteMD {
    public List<AdoptanteDP> obtenerTodos() throws SQLException {
        List<AdoptanteDP> adoptantes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ID_ADOPTANTE AS \"idAdoptante\", nombre, cedula, fechaNacimiento, direccion, ocupacion FROM Adoptante WHERE 1=1 AND estado_registro = 'ACT'");
        sql.append(" ORDER BY nombre");
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                adoptantes.add(new AdoptanteDP(
                    rs.getInt("idAdoptante"),
                    rs.getString("nombre"),
                    rs.getString("cedula"),
                    rs.getDate("fechaNacimiento"),
                    rs.getString("direccion"),
                    rs.getString("ocupacion")
                ));
            }
        }
        return adoptantes;
    }
    public void insertar(AdoptanteDP adoptante, int idPersonalEjecutor) throws SQLException {
        String sql = "INSERT INTO Adoptante (nombre, cedula, fechaNacimiento, direccion, ocupacion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, adoptante.getNombre());
                ps.setString(2, adoptante.getCedula());
                ps.setDate(3, adoptante.getFechaNacimiento());
                ps.setString(4, adoptante.getDireccion());
                ps.setString(5, adoptante.getOcupacion());
                ps.executeUpdate();
                try (ResultSet rsGen = ps.getGeneratedKeys()) {
                    if (rsGen.next()) adoptante.setIdAdoptante(rsGen.getInt(1));
                }
                AuditoriaMD.registrarAuditoria(conn, "Adoptante", "INSERT", "Adoptante insertado ID: " + adoptante.getIdAdoptante());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
    public void actualizar(AdoptanteDP adoptante, int idPersonalEjecutor) throws SQLException {
        String sql = "UPDATE Adoptante SET nombre = ?, cedula = ?, fechaNacimiento = ?, direccion = ?, ocupacion = ? WHERE ID_ADOPTANTE = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, adoptante.getNombre());
                ps.setString(2, adoptante.getCedula());
                ps.setDate(3, adoptante.getFechaNacimiento());
                ps.setString(4, adoptante.getDireccion());
                ps.setString(5, adoptante.getOcupacion());
                ps.setInt(6, adoptante.getIdAdoptante());
                ps.executeUpdate();
                AuditoriaMD.registrarAuditoria(conn, "Adoptante", "UPDATE", "Adoptante modificado ID: " + adoptante.getIdAdoptante());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
    public void eliminar(int idAdoptante, int idPersonalEjecutor) throws SQLException {
        String sql = "UPDATE Adoptante SET estado_registro = 'INA' WHERE ID_ADOPTANTE = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idAdoptante);
                ps.executeUpdate();
                AuditoriaMD.registrarAuditoria(conn, "Adoptante", "DELETE (LOGICO)", "Adoptante inactivo ID: " + idAdoptante);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}