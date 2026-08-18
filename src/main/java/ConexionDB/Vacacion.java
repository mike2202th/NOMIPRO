package ConexionDB;

/**
 * Modelo que representa un registro de vacaciones de un empleado.
 */
public class Vacacion {
    private int idVacacion;
    private int idEmpleado;
    private String nombreEmpleado;
    private java.sql.Date fechaInicio;
    private java.sql.Date fechaFin;
    private int cantidadDias;
    private String estado; // Pendiente, Aprobada, Rechazada, Completada
    private String observaciones;

    public Vacacion() {}

    public Vacacion(int idEmpleado, java.sql.Date fechaInicio, java.sql.Date fechaFin,
                    int cantidadDias, String estado, String observaciones) {
        this.idEmpleado = idEmpleado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadDias = cantidadDias;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public int getIdVacacion() { return idVacacion; }
    public void setIdVacacion(int idVacacion) { this.idVacacion = idVacacion; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public java.sql.Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(java.sql.Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public java.sql.Date getFechaFin() { return fechaFin; }
    public void setFechaFin(java.sql.Date fechaFin) { this.fechaFin = fechaFin; }

    public int getCantidadDias() { return cantidadDias; }
    public void setCantidadDias(int cantidadDias) { this.cantidadDias = cantidadDias; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
