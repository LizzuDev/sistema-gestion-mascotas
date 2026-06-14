package gestionmascotas.dp.modelos;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
public class MascotaDP {
    private int idMascota;
    private int idCentro;
    private String nombre;
    private String especie;
    private Date fechaNacimiento;
    private String raza;
    private String estadoAdopcion;
    public MascotaDP() {}
    public MascotaDP(int idMascota, int idCentro, String nombre, String especie, Date fechaNacimiento, String raza, String estadoAdopcion) {
        this.idMascota = idMascota; this.idCentro = idCentro; this.nombre = nombre; this.especie = especie; this.fechaNacimiento = fechaNacimiento; this.raza = raza; this.estadoAdopcion = estadoAdopcion;
    }
    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }
    public int getIdCentro() { return idCentro; }
    public void setIdCentro(int idCentro) { this.idCentro = idCentro; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public String getEstadoAdopcion() { return estadoAdopcion; }
    public void setEstadoAdopcion(String estadoAdopcion) { this.estadoAdopcion = estadoAdopcion; }
    public boolean verificarDP() throws SQLException { return true; }
    public void grabarDP(int ejecutor) throws SQLException {
        gestionmascotas.md.MascotaMD md = new gestionmascotas.md.MascotaMD();
        if (this.idMascota == 0) {
            if (!md.verificarCapacidadSede(this.idCentro)) throw new SQLException("No hay cupo disponible en la sede seleccionada.");
            md.insertar(this, ejecutor);
        } else md.actualizar(this, ejecutor);
    }
    public void eliminarDP(int ejecutor) throws SQLException {
        new gestionmascotas.md.MascotaMD().eliminar(this.idMascota, this.idCentro, ejecutor);
    }
    public List<MascotaDP> consultarDP() throws SQLException {
        return new gestionmascotas.md.MascotaMD().obtenerTodas();
    }
}
