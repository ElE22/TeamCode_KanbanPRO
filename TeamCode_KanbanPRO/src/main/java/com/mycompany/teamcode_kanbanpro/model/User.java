/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Emanuel
 */
public class User implements Serializable {

    private int idUsuario;
    private int idRol;
    private String usuario;
    private String nombre;
    private String email;
    private String password;
    private boolean activo;
    private Timestamp creadoEn;

    public User() {
    }
    
    public User( int idRol, String usuario, String nombre, String email, String password, boolean activo, Timestamp creadoEn) {
        this.idRol = idRol;
        this.usuario = usuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public User(int idUsuario, int idRol, String usuario, String nombre, String email, String password, boolean activo, Timestamp creadoEn) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.usuario = usuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Timestamp getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Timestamp creadoEn) {
        this.creadoEn = creadoEn;
    }

    
//    @Override
//    public String toString() {
//        return "User{" +
//               "idUsuario=" + idUsuario +
//               ", idRol=" + idRol +
//               ", usuario='" + usuario + '\'' +
//               ", nombre='" + nombre + '\'' +
//               ", email='" + email + '\'' +
//               ", activo=" + activo +
//               ", creadoEn=" + creadoEn +
//               '}';
//    }
}
