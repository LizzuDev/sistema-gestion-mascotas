package gestionmascotas.md;

import gestionmascotas.dp.models.VeterinarioDP;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a Datos de Veterinario (Manejo de Datos - MD).
 */
public class VeterinarioMD implements IVeterinarioMD {

    public List<VeterinarioDP> obtenerTodos() throws SQLException {
        List<VeterinarioDP> lista = new ArrayList<>();
        String sql = "SELECT idVeterinario, nombre, identificacion, especialidad, telefono FROM Veterinario ORDER BY nombre";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new VeterinarioDP(
                    rs.getInt("idVeterinario"),
                    rs.getString("nombre"),
                    rs.getString("identificacion"),
                    rs.getString("especialidad"),
                    rs.getString("telefono")
                ));
            }
        }
        return lista;
    }

    public void insertar(VeterinarioDP v, int idPersonalEjecutor) throws SQLException {
        String sql = "INSERT INTO Veterinario (nombre, identificacion, especialidad, telefono) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, v.getNombre());
                ps.setString(2, v.getIdentificacion());
                ps.setString(3, v.getEspecialidad());
                ps.setString(4, v.getTelefono());
                ps.executeUpdate();
                
                try (ResultSet rsGen = ps.getGeneratedKeys()) {
                    if (rsGen.next()) {
                        v.setIdVeterinario(rsGen.getInt(1));
                    }
                }
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "INSERT", 
                    "Registrado nuevo veterinario: " + v.getNombre() + " (" + v.getEspecialidad() + ")");
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public void actualizar(VeterinarioDP v, int idPersonalEjecutor) throws SQLException {
        String sql = "UPDATE Veterinario SET nombre = ?, identificacion = ?, especialidad = ?, telefono = ? WHERE idVeterinario = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, v.getNombre());
                ps.setString(2, v.getIdentificacion());
                ps.setString(3, v.getEspecialidad());
                ps.setString(4, v.getTelefono());
                ps.setInt(5, v.getIdVeterinario());
                ps.executeUpdate();
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "UPDATE", 
                    "Actualizado veterinario ID " + v.getIdVeterinario() + ": " + v.getNombre());
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public void eliminar(int idVeterinario, int idPersonalEjecutor) throws SQLException {
        String sql = "DELETE FROM Veterinario WHERE idVeterinario = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idVeterinario);
                ps.executeUpdate();
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "DELETE", 
                    "Eliminado veterinario ID " + idVeterinario);
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
}
