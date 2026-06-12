package gestionmascotas.md;

import gestionmascotas.dp.models.PersonalDP;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a Datos de Personal (Manejo de Datos - MD).
 */
public class PersonalMD implements IPersonalMD {

    public List<PersonalDP> obtenerTodos() throws SQLException {
        List<PersonalDP> lista = new ArrayList<>();
        String sql = "SELECT idPersonal, nombre, identificacion, rol, estado FROM Personal ORDER BY nombre";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new PersonalDP(
                    rs.getInt("idPersonal"),
                    rs.getString("nombre"),
                    rs.getString("identificacion"),
                    rs.getString("rol"),
                    rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    public void insertar(PersonalDP p, int idPersonalEjecutor) throws SQLException {
        String sql = "INSERT INTO Personal (nombre, identificacion, rol, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getIdentificacion());
                ps.setString(3, p.getRol());
                ps.setString(4, p.getEstado());
                ps.executeUpdate();
                
                try (ResultSet rsGen = ps.getGeneratedKeys()) {
                    if (rsGen.next()) {
                        p.setIdPersonal(rsGen.getInt(1));
                    }
                }
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "INSERT", 
                    "Registrado nuevo personal: " + p.getNombre() + " (" + p.getRol() + ")");
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public void actualizar(PersonalDP p, int idPersonalEjecutor) throws SQLException {
        String sql = "UPDATE Personal SET nombre = ?, identificacion = ?, rol = ?, estado = ? WHERE idPersonal = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getIdentificacion());
                ps.setString(3, p.getRol());
                ps.setString(4, p.getEstado());
                ps.setInt(5, p.getIdPersonal());
                ps.executeUpdate();
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "UPDATE", 
                    "Actualizado personal ID " + p.getIdPersonal() + ": " + p.getNombre());
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public void eliminar(int idPersonal, int idPersonalEjecutor) throws SQLException {
        String sql = "DELETE FROM Personal WHERE idPersonal = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idPersonal);
                ps.executeUpdate();
                
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "DELETE", 
                    "Eliminado personal ID " + idPersonal);
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
}
