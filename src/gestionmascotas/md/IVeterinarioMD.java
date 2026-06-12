package gestionmascotas.md;

import gestionmascotas.dp.models.VeterinarioDP;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface para el Manejo de Datos (MD) de Veterinarios.
 * Sigue el principio GoF: "Programar hacia una interface, no una hacia implementación".
 */
public interface IVeterinarioMD {
    List<VeterinarioDP> obtenerTodos() throws SQLException;
    void insertar(VeterinarioDP v, int idPersonalEjecutor) throws SQLException;
    void actualizar(VeterinarioDP v, int idPersonalEjecutor) throws SQLException;
    void eliminar(int idVeterinario, int idPersonalEjecutor) throws SQLException;
}
