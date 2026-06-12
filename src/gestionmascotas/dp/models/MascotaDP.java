package gestionmascotas.dp.models;

/**
 * Representa la entidad Mascota en la capa DP (Dominio del Problema).
 * Estándar de Estilo: STD-02.
 */
public class MascotaDP {
    private int idMascota;
    private int idCentro;
    private String nombre;
    private String especie; // 'PERRO', 'GATO', 'OTRO'
    private int edad;
    private String estado; // 'DISPONIBLE', 'ADOPTADA', 'EN_TRATAMIENTO'

    public MascotaDP() {}

    public MascotaDP(int idMascota, int idCentro, String nombre, String especie, int edad, String estado) {
        this.idMascota = idMascota;
        this.idCentro = idCentro;
        this.nombre = nombre;
        this.especie = especie;
        this.edad = edad;
        this.estado = estado;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public int getIdCentro() {
        return idCentro;
    }

    public void setIdCentro(int idCentro) {
        this.idCentro = idCentro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre + " (" + especie + ", " + edad + " años)";
    }
}
