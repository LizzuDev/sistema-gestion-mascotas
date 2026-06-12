package gestionmascotas.md;

import gestionmascotas.dp.models.PersonalDP;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface para el Manejo de Datos (MD) de Personal.
 * Sigue el principio GoF: "Programar hacia una interface, no una hacia implementación".
 */
public interface IPersonalMD {
    List<PersonalDP> obtenerTodos() throws SQLException;
    void insertar(PersonalDP p, int idPersonalEjecutor) throws SQLException;
    void actualizar(PersonalDP p, int idPersonalEjecutor) throws SQLException;
    void eliminar(int idPersonal, int idPersonalEjecutor) throws SQLException;
}
