package gestionmascotas.dp.models;

/**
 * Representa la entidad Adoptante en la capa DP (Dominio del Problema).
 * Estándar de Estilo: STD-02.
 */
public class AdoptanteDP {
    private int idAdoptante;
    private String nombre;
    private String identificacion;
    private String correo;
    private String telefono;

    public AdoptanteDP() {}

    public AdoptanteDP(int idAdoptante, String nombre, String identificacion, String correo, String telefono) {
        this.idAdoptante = idAdoptante;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.correo = correo;
        this.telefono = telefono;
    }

    public int getIdAdoptante() {
        return idAdoptante;
    }

    public void setIdAdoptante(int idAdoptante) {
        this.idAdoptante = idAdoptante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre + " (CI: " + identificacion + ")";
    }
}
