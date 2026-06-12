package gestionmascotas.md;

import gestionmascotas.dp.models.AdoptanteDP;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface para el Manejo de Datos (MD) de Adoptantes.
 * Sigue el principio GoF: "Programar hacia una interface, no una hacia implementación".
 */
public interface IAdoptanteMD {
    List<AdoptanteDP> obtenerTodos() throws SQLException;
    void insertar(AdoptanteDP a, int idPersonalEjecutor) throws SQLException;
    void actualizar(AdoptanteDP a, int idPersonalEjecutor) throws SQLException;
    void eliminar(int idAdoptante, int idPersonalEjecutor) throws SQLException;
}
