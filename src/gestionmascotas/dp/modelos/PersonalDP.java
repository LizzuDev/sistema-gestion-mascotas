package gestionmascotas.dp.modelos;
import java.sql.SQLException;
import java.util.List;
public class PersonalDP {
    private int idPersonal;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String cargo;
    private String telefono;
    private String correo;
    private String clave;
    private String estado;
    public PersonalDP() {}
    public PersonalDP(int idPersonal, String cedula, String nombres, String apellidos, String cargo, String telefono, String correo, String clave, String estado) {
        this.idPersonal = idPersonal;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.telefono = telefono;
        this.correo = correo;
        this.clave = clave;
        this.estado = estado;
    }
    public int getIdPersonal() { return idPersonal; }
    public void setIdPersonal(int idPersonal) { this.idPersonal = idPersonal; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean verificarDP() throws SQLException { return true; }
    public void grabarDP(int ejecutor) throws SQLException {
        gestionmascotas.md.PersonalMD md = new gestionmascotas.md.PersonalMD();
        if (this.idPersonal == 0) md.insertar(this, ejecutor);
        else md.actualizar(this, ejecutor);
    }
    public void eliminarDP(int ejecutor) throws SQLException {
        new gestionmascotas.md.PersonalMD().eliminar(this.idPersonal, ejecutor);
    }
    public List<PersonalDP> consultarDP() throws SQLException {
        return new gestionmascotas.md.PersonalMD().obtenerTodos();
    }
}
