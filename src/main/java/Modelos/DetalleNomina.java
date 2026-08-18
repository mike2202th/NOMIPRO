/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import java.math.BigDecimal;

/**
 *
 * @author maico
 */
public class DetalleNomina {
    private int idDetalle;
    private int idNomina;
    private int idEmpleado;
    private BigDecimal salarioBase;
    private BigDecimal diasTrabajados;
    private BigDecimal totalHorasExtras;
    private BigDecimal totalBonificaciones;
    private BigDecimal totalDeducciones;
    private BigDecimal salarioBruto;
    private BigDecimal salarioNeto;
    private String estado;

    // Constructor vacío
    public DetalleNomina() {
    }

    // Constructor con todos los campos
    public DetalleNomina(int idDetalle, int idNomina, int idEmpleado, BigDecimal salarioBase, 
                         BigDecimal diasTrabajados, BigDecimal totalHorasExtras, 
                         BigDecimal totalBonificaciones, BigDecimal totalDeducciones, 
                         BigDecimal salarioBruto, BigDecimal salarioNeto, String estado) {
        this.idDetalle = idDetalle;
        this.idNomina = idNomina;
        this.idEmpleado = idEmpleado;
        this.salarioBase = salarioBase;
        this.diasTrabajados = diasTrabajados;
        this.totalHorasExtras = totalHorasExtras;
        this.totalBonificaciones = totalBonificaciones;
        this.totalDeducciones = totalDeducciones;
        this.salarioBruto = salarioBruto;
        this.salarioNeto = salarioNeto;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdNomina() {
        return idNomina;
    }

    public void setIdNomina(int idNomina) {
        this.idNomina = idNomina;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public BigDecimal getDiasTrabajados() {
        return diasTrabajados;
    }

    public void setDiasTrabajados(BigDecimal diasTrabajados) {
        this.diasTrabajados = diasTrabajados;
    }

    public BigDecimal getTotalHorasExtras() {
        return totalHorasExtras;
    }

    public void setTotalHorasExtras(BigDecimal totalHorasExtras) {
        this.totalHorasExtras = totalHorasExtras;
    }

    public BigDecimal getTotalBonificaciones() {
        return totalBonificaciones;
    }

    public void setTotalBonificaciones(BigDecimal totalBonificaciones) {
        this.totalBonificaciones = totalBonificaciones;
    }

    public BigDecimal getTotalDeducciones() {
        return totalDeducciones;
    }

    public void setTotalDeducciones(BigDecimal totalDeducciones) {
        this.totalDeducciones = totalDeducciones;
    }

    public BigDecimal getSalarioBruto() {
        return salarioBruto;
    }

    public void setSalarioBruto(BigDecimal salarioBruto) {
        this.salarioBruto = salarioBruto;
    }

    public BigDecimal getSalarioNeto() {
        return salarioNeto;
    }

    public void setSalarioNeto(BigDecimal salarioNeto) {
        this.salarioNeto = salarioNeto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void generarPDF(int idNomina) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

