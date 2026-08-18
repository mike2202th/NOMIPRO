package ConexionDB;

/**
 * Modelo que representa un registro de falta de un empleado.
 */
public class Falta {
    private int idFalta;
    private int idEmpleado;
    private String nombreEmpleado;
    private java.sql.Date fecha;
    private String tipoFalta;
    private boolean justificada;
    private String observaciones;

    public Falta() {}

    public Falta(int idEmpleado, java.sql.Date fecha, String tipoFalta,
                 boolean justificada, String observaciones) {
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.tipoFalta = tipoFalta;
        this.justificada = justificada;
        this.observaciones = observaciones;
    }

    public int getIdFalta() { return idFalta; }
    public void setIdFalta(int idFalta) { this.idFalta = idFalta; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public java.sql.Date getFecha() { return fecha; }
    public void setFecha(java.sql.Date fecha) { this.fecha = fecha; }

    public String getTipoFalta() { return tipoFalta; }
    public void setTipoFalta(String tipoFalta) { this.tipoFalta = tipoFalta; }

    public boolean isJustificada() { return justificada; }
    public void setJustificada(boolean justificada) { this.justificada = justificada; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
