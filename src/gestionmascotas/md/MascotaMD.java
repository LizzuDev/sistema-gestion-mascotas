package gestionmascotas.md;

import gestionmascotas.dp.models.MascotaDP;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a Datos de Mascotas (Manejo de Datos - MD).
 */
public class MascotaMD implements IMascotaMD {

    public List<MascotaDP> obtenerTodas() throws SQLException {
        List<MascotaDP> mascotas = new ArrayList<>();
        String sql = "SELECT idMascota, idCentro, nombre, especie, edad, estado FROM Mascota ORDER BY idMascota DESC";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                mascotas.add(new MascotaDP(
                    rs.getInt("idMascota"),
                    rs.getInt("idCentro"),
                    rs.getString("nombre"),
                    rs.getString("especie"),
                    rs.getInt("edad"),
                    rs.getString("estado")
                ));
            }
        }
        return mascotas;
    }

    public List<MascotaDP> obtenerFiltradas(String especie, Integer idCentro, String estado) throws SQLException {
        List<MascotaDP> mascotas = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT idMascota, idCentro, nombre, especie, edad, estado FROM Mascota WHERE 1=1");
        
        if (especie != null && !especie.isEmpty() && !especie.equals("TODOS")) {
            sql.append(" AND especie = ?");
        }
        if (idCentro != null && idCentro > 0) {
            sql.append(" AND idCentro = ?");
        }
        if (estado != null && !estado.isEmpty() && !estado.equals("TODOS")) {
            sql.append(" AND estado = ?");
        }
        sql.append(" ORDER BY idMascota DESC");

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (especie != null && !especie.isEmpty() && !especie.equals("TODOS")) {
                ps.setString(paramIndex++, especie);
            }
            if (idCentro != null && idCentro > 0) {
                ps.setInt(paramIndex++, idCentro);
            }
            if (estado != null && !estado.isEmpty() && !estado.equals("TODOS")) {
                ps.setString(paramIndex++, estado);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(new MascotaDP(
                        rs.getInt("idMascota"),
                        rs.getInt("idCentro"),
                        rs.getString("nombre"),
                        rs.getString("especie"),
                        rs.getInt("edad"),
                        rs.getString("estado")
                    ));
                }
            }
        }
        return mascotas;
    }

    /**
     * Inserta una nueva mascota realizando una transacción controlada (STD-04).
     */
    public void insertar(MascotaDP mascota, int idPersonalEjecutor) throws SQLException {
        String insertMascota = "INSERT INTO Mascota (idCentro, nombre, especie, edad, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false); // Iniciar transacción controlada (STD-04)

            try {
                // 1. Insertar la mascota
                try (PreparedStatement psInsert = conn.prepareStatement(insertMascota, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setInt(1, mascota.getIdCentro());
                    psInsert.setString(2, mascota.getNombre());
                    psInsert.setString(3, mascota.getEspecie());
                    psInsert.setInt(4, mascota.getEdad());
                    psInsert.setString(5, mascota.getEstado());
                    psInsert.executeUpdate();

                    try (ResultSet rsGen = psInsert.getGeneratedKeys()) {
                        if (rsGen.next()) {
                            mascota.setIdMascota(rsGen.getInt(1));
                        }
                    }
                }

                // 2. Registrar auditoría (STD-06)
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "INSERT", 
                    "Ingreso de mascota: " + mascota.getNombre() + " (" + mascota.getEspecie() + ") asignada al centro ID " + mascota.getIdCentro());

                conn.commit(); // Confirmar transacción exitosa
            } catch (SQLException e) {
                conn.rollback(); // Revertir todo si falla (STD-04)
                throw e;
            }
        }
    }

    public void actualizar(MascotaDP mascota, int idPersonalEjecutor) throws SQLException {
        String sql = "UPDATE Mascota SET nombre = ?, especie = ?, edad = ?, estado = ? WHERE idMascota = ?";
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, mascota.getNombre());
                ps.setString(2, mascota.getEspecie());
                ps.setInt(3, mascota.getEdad());
                ps.setString(4, mascota.getEstado());
                ps.setInt(5, mascota.getIdMascota());
                ps.executeUpdate();
                
                // Registrar Auditoría (STD-06)
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "UPDATE", 
                    "Actualizada mascota ID " + mascota.getIdMascota() + ": " + mascota.getNombre());
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void eliminar(int idMascota, int idCentro, int idPersonalEjecutor) throws SQLException {
        String deleteMascota = "DELETE FROM Mascota WHERE idMascota = ?";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                // 1. Eliminar mascota
                try (PreparedStatement psDel = conn.prepareStatement(deleteMascota)) {
                    psDel.setInt(1, idMascota);
                    psDel.executeUpdate();
                }

                // 2. Registrar auditoría (STD-06)
                AuditoriaMD.registrarAuditoria(conn, idPersonalEjecutor, "DELETE", 
                    "Eliminada mascota ID " + idMascota + " del centro ID " + idCentro);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
