package ConexionDB;

/**
 * Modelo que representa un registro de asistencia de un empleado.
 */
public class Asistencia {
    private int idAsistencia;
    private int idEmpleado;
    private String nombreEmpleado;
    private java.sql.Date fecha;
    private String horaEntrada;
    private String horaSalida;
    private String estado; // Presente, Ausente, Tardanza, Permiso, Vacaciones
    private String observaciones;

    public Asistencia() {}

    public Asistencia(int idEmpleado, java.sql.Date fecha, String horaEntrada,
                      String horaSalida, String estado, String observaciones) {
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public java.sql.Date getFecha() { return fecha; }
    public void setFecha(java.sql.Date fecha) { this.fecha = fecha; }

    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }

    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
