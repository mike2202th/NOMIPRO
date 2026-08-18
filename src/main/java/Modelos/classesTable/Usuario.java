/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos.classesTable;

/**
 *
 * @author maico
 */

public class Usuario {

    // =========================
    // CAMPOS
    // =========================

    private int idUsuario;
    private Integer idEmpleado;
    private int idRol;
    private String usuario;
    private String contrasena;
    private String email;
    private String ultimoAcceso;
    private int intentosFallidos;
    private String estado;
    private String creadoEn;


    // =========================
    // CONSTRUCTOR VACÍO
    // =========================

    public Usuario() {
    }


    // =========================
    // CONSTRUCTOR COMPLETO
    // =========================

    public Usuario(int idUsuario, Integer idEmpleado, int idRol,
                   String usuario, String contrasena, String email,
                   String ultimoAcceso, int intentosFallidos,
                   String estado, String creadoEn) {

        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
        this.idRol = idRol;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.email = email;
        this.ultimoAcceso = ultimoAcceso;
        this.intentosFallidos = intentosFallidos;
        this.estado = estado;
        this.creadoEn = creadoEn;
    }


    // =========================
    // GETTERS Y SETTERS
    // =========================

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }


    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }


    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }


    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }


    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(String ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }


    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }


    // =========================
    // TOSTRING
    // =========================

    @Override
    public String toString() {
        return usuario;
    }
}

