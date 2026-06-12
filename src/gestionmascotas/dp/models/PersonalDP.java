package gestionmascotas.dp.models;

/**
 * Representa la entidad Personal en la capa DP (Dominio del Problema).
 * Estándar de Estilo: STD-02.
 */
public class PersonalDP {
    private int idPersonal;
    private String nombre;
    private String identificacion;
    private String rol; // 'LIDER', 'PLANIFICACION_CALIDAD', 'DESARROLLO', 'PERSONAL_APOYO'
    private String estado; // 'ACTIVO', 'INACTIVO'

    public PersonalDP() {}

    public PersonalDP(int idPersonal, String nombre, String identificacion, String rol, String estado) {
        this.idPersonal = idPersonal;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.rol = rol;
        this.estado = estado;
    }

    public int getIdPersonal() {
        return idPersonal;
    }

    public void setIdPersonal(int idPersonal) {
        this.idPersonal = idPersonal;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre + " (" + rol + ")";
    }
}
