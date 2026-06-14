package gestionmascotas.dp.modelos;
import java.sql.SQLException;
import java.util.List;
public class VeterinarioDP {
    private int idVeterinario;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String licencia;
    private String telefono;
    public VeterinarioDP() {}
    public VeterinarioDP(int idVeterinario, String cedula, String nombres, String apellidos, String especialidad, String licencia, String telefono) {
        this.idVeterinario = idVeterinario; this.cedula = cedula; this.nombres = nombres; this.apellidos = apellidos; this.especialidad = especialidad; this.licencia = licencia; this.telefono = telefono;
    }
    public int getIdVeterinario() { return idVeterinario; }
    public void setIdVeterinario(int idVeterinario) { this.idVeterinario = idVeterinario; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public boolean verificarDP() throws SQLException { return true; }
    public void grabarDP(int ejecutor) throws SQLException {
        gestionmascotas.md.VeterinarioMD md = new gestionmascotas.md.VeterinarioMD();
        if (this.idVeterinario == 0) md.insertar(this, ejecutor);
        else md.actualizar(this, ejecutor);
    }
    public void eliminarDP(int ejecutor) throws SQLException {
        new gestionmascotas.md.VeterinarioMD().eliminar(this.idVeterinario, ejecutor);
    }
    public List<VeterinarioDP> consultarDP() throws SQLException {
        return new gestionmascotas.md.VeterinarioMD().obtenerTodos();
    }
}
