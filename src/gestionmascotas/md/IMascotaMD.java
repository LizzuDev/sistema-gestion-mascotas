package gestionmascotas.md;

import gestionmascotas.dp.models.MascotaDP;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface para el Manejo de Datos (MD) de Mascotas.
 * Sigue el principio GoF: "Programar hacia una interface, no una hacia implementación".
 */
public interface IMascotaMD {
    List<MascotaDP> obtenerTodas() throws SQLException;
    List<MascotaDP> obtenerFiltradas(String especie, Integer idCentro, String estado) throws SQLException;
    void insertar(MascotaDP m, int idPersonalEjecutor) throws SQLException;
    void actualizar(MascotaDP m, int idPersonalEjecutor) throws SQLException;
    void eliminar(int idMascota, int idCentro, int idPersonalEjecutor) throws SQLException;
}
