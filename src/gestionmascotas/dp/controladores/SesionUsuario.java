package gestionmascotas.dp.controladores;
import gestionmascotas.dp.modelos.PersonalDP;
public class SesionUsuario {
    private static PersonalDP usuarioActual;
    public static PersonalDP getUsuarioActual() {
        return usuarioActual;
    }
    public static void setUsuarioActual(PersonalDP personal) {
        usuarioActual = personal;
    }
    public static boolean esAdministrador() {
        return getUsuarioActual() != null && "ADMINISTRADOR".equals(getUsuarioActual().getCargo());
    }
    public static boolean esVoluntario() {
        return getUsuarioActual() != null && "VOLUNTARIO".equals(getUsuarioActual().getCargo());
    }
    public static boolean esVeterinario() {
        return getUsuarioActual() != null && "VETERINARIO_JEFE".equals(getUsuarioActual().getCargo());
    }
}