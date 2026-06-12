package gestionmascotas.dp.models;

/**
 * Representa la entidad Veterinario en la capa DP (Dominio del Problema).
 * Estándar de Estilo: STD-02.
 */
public class VeterinarioDP {
    private int idVeterinario;
    private String nombre;
    private String identificacion;
    private String especialidad;
    private String telefono;

    public VeterinarioDP() {}

    public VeterinarioDP(int idVeterinario, String nombre, String identificacion, String especialidad, String telefono) {
        this.idVeterinario = idVeterinario;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.especialidad = especialidad;
        this.telefono = telefono;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre + " (" + especialidad + ")";
    }
}
