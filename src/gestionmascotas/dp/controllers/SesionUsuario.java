package gestionmascotas.dp.controllers;

import gestionmascotas.dp.models.PersonalDP;

/**
 * Controla la sesión del usuario actual en la aplicación.
 * Permite simular los diferentes roles definidos en TSPi (Líder, Planificación/Calidad, Desarrollo).
 */
public class SesionUsuario {
    private static PersonalDP usuarioActual;

    public static PersonalDP getUsuarioActual() {
        if (usuarioActual == null) {
            // Por defecto, iniciar con el Líder para facilidad de pruebas
            usuarioActual = new PersonalDP(1, "Paúl Rosero", "1712345678", "LIDER", "ACTIVO");
        }
        return usuarioActual;
    }

    public static void setUsuarioActual(PersonalDP personal) {
        usuarioActual = personal;
    }

    /**
     * Verifica si el usuario actual tiene permisos para realizar una acción
     * según su rol en la metodología.
     */
    public static boolean esLider() {
        return getUsuarioActual() != null && "LIDER".equals(getUsuarioActual().getRol());
    }

    public static boolean esCalidad() {
        return getUsuarioActual() != null && "PLANIFICACION_CALIDAD".equals(getUsuarioActual().getRol());
    }

    public static boolean esDesarrollo() {
        return getUsuarioActual() != null && "DESARROLLO".equals(getUsuarioActual().getRol());
    }
}
