/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Emanuel
 */

public class Project implements Serializable {
    private int idProyecto;
    private int idUsuarioCreador; // fk a usuario
    private String nombre;
    private String descripcion;
    private Timestamp fechaCreacion;
    private String gruposPertenencia;

   
    
    // atributos extra para joins o data extendida
    private String nombreCreador; // nombre del usuario creador
    private List<Group> gruposAsignados; // para la relacion n:m con grupo

    // constructor
    public Project() {
    }

    public Project(int idProyecto, int idUsuarioCreador, String nombre) {
        this.idProyecto = idProyecto;
        this.idUsuarioCreador = idUsuarioCreador;
        this.nombre = nombre;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public int getIdUsuarioCreador() {
        return idUsuarioCreador;
    }

    public void setIdUsuarioCreador(int idUsuarioCreador) {
        this.idUsuarioCreador = idUsuarioCreador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreCreador() {
        return nombreCreador;
    }

    public void setNombreCreador(String nombreCreador) {
        this.nombreCreador = nombreCreador;
    }

    public List<Group> getGruposAsignados() {
        return gruposAsignados;
    }

    public void setGruposAsignados(List<Group> gruposAsignados) {
        this.gruposAsignados = gruposAsignados;
    }
    
    public String getGruposPertenencia() {
        return gruposPertenencia;
    }

    public void setGruposPertenencia(String gruposPertenencia) {
        this.gruposPertenencia = gruposPertenencia;
    }
}
