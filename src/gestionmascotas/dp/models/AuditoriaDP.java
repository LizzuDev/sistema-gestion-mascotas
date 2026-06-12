package gestionmascotas.dp.models;

import java.util.Date;

/**
 * Representa la entidad Auditoria en la capa DP (Dominio del Problema).
 * Estándar de Estilo: STD-02.
 */
public class AuditoriaDP {
    private int idAuditoria;
    private int idPersonal;
    private String accion; // 'INSERT', 'UPDATE', 'DELETE'
    private String detalle;
    private Date fechaRegistro;

    public AuditoriaDP() {}

    public AuditoriaDP(int idAuditoria, int idPersonal, String accion, String detalle, Date fechaRegistro) {
        this.idAuditoria = idAuditoria;
        this.idPersonal = idPersonal;
        this.accion = accion;
        this.detalle = detalle;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public int getIdPersonal() {
        return idPersonal;
    }

    public void setIdPersonal(int idPersonal) {
        this.idPersonal = idPersonal;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
