/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author maico
 */
public class Nomina {
    private int idNomina;
    private int idPeriodo;
    private Date fechaGeneracion;
    private int generadoPor;
    private BigDecimal totalDevengado;
    private BigDecimal totalDeducciones;
    private BigDecimal totalNeto;
    private String estado; // 'BORRADOR','APROBADA','PAGADA','ANULADA'

    // Constructor vacío
    public Nomina() {
    }

    // Constructor con todos los campos
    public Nomina(int idNomina, int idPeriodo, Date fechaGeneracion, int generadoPor, 
                  BigDecimal totalDevengado, BigDecimal totalDeducciones, 
                  BigDecimal totalNeto, String estado) {
        this.idNomina = idNomina;
        this.idPeriodo = idPeriodo;
        this.fechaGeneracion = fechaGeneracion;
        this.generadoPor = generadoPor;
        this.totalDevengado = totalDevengado;
        this.totalDeducciones = totalDeducciones;
        this.totalNeto = totalNeto;
        this.estado = estado;
    }

    public int getIdNomina() {
        return idNomina;
    }

    public void setIdNomina(int idNomina) {
        this.idNomina = idNomina;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Date fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public int getGeneradoPor() {
        return generadoPor;
    }

    public void setGeneradoPor(int generadoPor) {
        this.generadoPor = generadoPor;
    }

    public BigDecimal getTotalDevengado() {
        return totalDevengado;
    }

    public void setTotalDevengado(BigDecimal totalDevengado) {
        this.totalDevengado = totalDevengado;
    }

    public BigDecimal getTotalDeducciones() {
        return totalDeducciones;
    }

    public void setTotalDeducciones(BigDecimal totalDeducciones) {
        this.totalDeducciones = totalDeducciones;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        this.totalNeto = totalNeto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
