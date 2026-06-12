package gestionmascotas.md;

import gestionmascotas.dp.models.AdoptanteDP;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a Datos de Adoptantes (Manejo de Datos - MD).
 */
public class AdoptanteMD implements IAdoptanteMD {

    public List<AdoptanteDP> obtenerTodos() throws SQLException {
        List<AdoptanteDP> adoptantes = new ArrayList<>();
        String sql = "SELECT idAdoptante, nombre, identificacion, correo, telefono FROM Adoptante ORDER BY nombre";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                adoptantes.add(new AdoptanteDP(
                    rs.getInt("idAdoptante"),
                    rs.getString("nombre"),
                    rs.getString("identificacion"),
                    rs.getString("correo"),
                    rs.getString("telefono")
                ));
            }
        }
        return adoptantes;
    }

    public void insertar(AdoptanteDP adoptante, int idPersonalEjecutor) throws SQLException {
        String sql = "INSERT INTO Adoptante (nombre, identificacion, correo, telefono) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, adoptante.getNombre());
                ps.setString(2, adoptante.getIdentificacion());
                ps.setString(3, adoptante.getCorreo());
                ps.setString(4, adoptante.getTelefono());
                ps.executeUpdate();
                
                try (ResultSet rsGen = ps.getGeneratedKeys()) {
                    if (rsGen.next()) {
                        adoptante.setIdAdoptante(rsGen.getInt(1));
                    }
                }
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "INSERT", 
                    "Registrado nuevo adoptante: " + adoptante.getNombre() + " (CI: " + adoptante.getIdentificacion() + ")");
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void actualizar(AdoptanteDP adoptante, int idPersonalEjecutor) throws SQLException {
        String sql = "UPDATE Adoptante SET nombre = ?, identificacion = ?, correo = ?, telefono = ? WHERE idAdoptante = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, adoptante.getNombre());
                ps.setString(2, adoptante.getIdentificacion());
                ps.setString(3, adoptante.getCorreo());
                ps.setString(4, adoptante.getTelefono());
                ps.setInt(5, adoptante.getIdAdoptante());
                ps.executeUpdate();
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "UPDATE", 
                    "Actualizado adoptante ID " + adoptante.getIdAdoptante() + ": " + adoptante.getNombre());
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void eliminar(int idAdoptante, int idPersonalEjecutor) throws SQLException {
        String sql = "DELETE FROM Adoptante WHERE idAdoptante = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idAdoptante);
                ps.executeUpdate();
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "DELETE", 
                    "Eliminado adoptante ID " + idAdoptante);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
