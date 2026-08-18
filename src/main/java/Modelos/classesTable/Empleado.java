/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos.classesTable;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author maico
 */

public class Empleado {
    private int idEmpleado;
    private int idEmpresa;
    private int idDepartamento;
    private int idPuesto;
    private int idTipoContrato;
    private String nombres;
    private String apellidos;
    private String cedula;
    private Date fechaNacimiento;
    private String genero; // 'M','F','OTRO'
    private String estadoCivil; // 'SOLTERO','CASADO'
    private String direccion;
    private String telefono;
    private String email;
    private String foto;
    private Date fechaIngreso;
    private Date fechaSalida;
    private BigDecimal salarioBase;
    private String tipoPago; // 'MENSUAL','QUINCENAL','SEMANAL'
    private String banco;
    private String cuentaBancaria;
    private String estado; // 'ACTIVO','INACTIVO','SUSPENDIDO','DESVINCULADO'
    private Date creadoEn;

    // Constructor vacío
    public Empleado() {
    }

    // Constructor con campos principales (útil para consultas rápidas o combos)
    public Empleado(int idEmpleado, String nombres, String apellidos) {
        this.idEmpleado = idEmpleado;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    // Constructor completo
    public Empleado(int idEmpleado, int idEmpresa, int idDepartamento, int idPuesto, int idTipoContrato, 
                    String nombres, String apellidos, String cedula, Date fechaNacimiento, String genero, 
                    String estadoCivil, String direccion, String telefono, String email, String foto, 
                    Date fechaIngreso, Date fechaSalida, BigDecimal salarioBase, String tipoPago, 
                    String banco, String cuentaBancaria, String estado, Date creadoEn) {
        this.idEmpleado = idEmpleado;
        this.idEmpresa = idEmpresa;
        this.idDepartamento = idDepartamento;
        this.idPuesto = idPuesto;
        this.idTipoContrato = idTipoContrato;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.estadoCivil = estadoCivil;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.foto = foto;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.salarioBase = salarioBase;
        this.tipoPago = tipoPago;
        this.banco = banco;
        this.cuentaBancaria = cuentaBancaria;
        this.estado = estado;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public int getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(int idPuesto) {
        this.idPuesto = idPuesto;
    }

    public int getIdTipoContrato() {
        return idTipoContrato;
    }

    public void setIdTipoContrato(int idTipoContrato) {
        this.idTipoContrato = idTipoContrato;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Date creadoEn) {
        this.creadoEn = creadoEn;
    }

    // Método toString clave para que el JComboBox muestre el nombre completo en lugar de códigos de memoria
    @Override
    public String toString() {
        return nombres + " " + apellidos;
    }
}

