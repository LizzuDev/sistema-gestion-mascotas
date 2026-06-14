package gestionmascotas.dp.modelos;
import java.sql.Date;
public class AdoptanteDP {
    private int idAdoptante;
    private String nombre;
    private String cedula;
    private Date fechaNacimiento;
    private String direccion;
    private String ocupacion;
    public AdoptanteDP() {}
    public AdoptanteDP(int idAdoptante, String nombre, String cedula, Date fechaNacimiento, String direccion, String ocupacion) {
        this.idAdoptante = idAdoptante;
        this.nombre = nombre;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.ocupacion = ocupacion;
    }
    public int getIdAdoptante() { return idAdoptante; }
    public void setIdAdoptante(int idAdoptante) { this.idAdoptante = idAdoptante; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
    @Override
    public String toString() {
        return nombre + " (CI: " + cedula + ")";
    }
    public boolean verificarDP() throws java.sql.SQLException { return true; }
    public void grabarDP(int ejecutor) throws java.sql.SQLException {
        gestionmascotas.md.AdoptanteMD md = new gestionmascotas.md.AdoptanteMD();
        if (this.idAdoptante == 0) md.insertar(this, ejecutor);
        else md.actualizar(this, ejecutor);
    }
    public void eliminarDP(int ejecutor) throws java.sql.SQLException {
        new gestionmascotas.md.AdoptanteMD().eliminar(this.idAdoptante, ejecutor);
    }
    public java.util.List<AdoptanteDP> consultarDP() throws java.sql.SQLException {
        return new gestionmascotas.md.AdoptanteMD().obtenerTodos();
    }
}