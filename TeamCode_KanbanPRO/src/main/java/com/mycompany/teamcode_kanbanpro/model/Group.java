/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.model;

import java.io.Serializable;
import java.util.List;
/**
 *
 * @author Emanuel
 */

public class Group implements Serializable {
    private int idGrupo;
    private String nombre;
    private String descripcion;
    
    // atributos extra para relaciones
    private List<User> miembros; // miembros del grupo (relacion n:m con usuario)
    private List<Project> proyectosAsignados; // proyectos asignados a este grupo (relacion n:m con proyecto)

    
    public Group() {
    }

    public Group(int idGrupo, String nombre) {
        this.idGrupo = idGrupo;
        this.nombre = nombre;
    }
    
    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
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

    public List<User> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<User> miembros) {
        this.miembros = miembros;
    }

    public List<Project> getProyectosAsignados() {
        return proyectosAsignados;
    }

    public void setProyectosAsignados(List<Project> proyectosAsignados) {
        this.proyectosAsignados = proyectosAsignados;
    }
}