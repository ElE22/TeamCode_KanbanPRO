/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Emanuel
 */

public class Task  implements Serializable{
    
    // atributos de la tabla tarea
    private int idTarea;
    private int idProyecto;
    private Integer idSprint; // integer para permitir null
    private int idColumna;
    private int idPrioridad;
    private String titulo;
    private String descripcion;
    private Timestamp fechaCreacion;
    private Date fechaVencimiento;
    private Timestamp fechaModificacion;
    private int creadoPor;
    private Integer idTareaPadre; // integer para permitir null (subtarea)

    // atributos extra para joins y relaciones
    private String nombreProyecto;
    private String nombreSprint;
    private String nombreColumna;
    private String nombrePrioridad;
    private String nombreCreador; // nombre del usuario que creo la tarea
    private String tituloTareaPadre;
    
    // para relaciones n:m (asignacion de usuarios) y recursiva (subtareas)
    private List<User> usuariosAsignados; 
    private List<Task> subtareas;
    private List<Group> gruposAsignados;

    // constructor vacio
    public Task() {
    }

    // constructor con campos obligatorios (not null)
    public Task(int idProyecto, int idColumna, int idPrioridad, String titulo, int creadoPor) {
        this.idProyecto = idProyecto;
        this.idColumna = idColumna;
        this.idPrioridad = idPrioridad;
        this.titulo = titulo;
        this.creadoPor = creadoPor;
    }

    // getters y setters
    
    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public Integer getIdSprint() {
        return idSprint;
    }

    public void setIdSprint(Integer idSprint) {
        this.idSprint = idSprint;
    }

    public int getIdColumna() {
        return idColumna;
    }

    public void setIdColumna(int idColumna) {
        this.idColumna = idColumna;
    }

    public int getIdPrioridad() {
        return idPrioridad;
    }

    public void setIdPrioridad(int idPrioridad) {
        this.idPrioridad = idPrioridad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Timestamp getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Timestamp fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public int getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(int creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Integer getIdTareaPadre() {
        return idTareaPadre;
    }

    public void setIdTareaPadre(Integer idTareaPadre) {
        this.idTareaPadre = idTareaPadre;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getNombreSprint() {
        return nombreSprint;
    }

    public void setNombreSprint(String nombreSprint) {
        this.nombreSprint = nombreSprint;
    }

    public String getNombreColumna() {
        return nombreColumna;
    }

    public void setNombreColumna(String nombreColumna) {
        this.nombreColumna = nombreColumna;
    }

    public String getNombrePrioridad() {
        return nombrePrioridad;
    }

    public void setNombrePrioridad(String nombrePrioridad) {
        this.nombrePrioridad = nombrePrioridad;
    }

    public String getNombreCreador() {
        return nombreCreador;
    }

    public void setNombreCreador(String nombreCreador) {
        this.nombreCreador = nombreCreador;
    }

    public String getTituloTareaPadre() {
        return tituloTareaPadre;
    }

    public void setTituloTareaPadre(String tituloTareaPadre) {
        this.tituloTareaPadre = tituloTareaPadre;
    }

    public List<User> getUsuariosAsignados() {
        return usuariosAsignados;
    }

    public void setUsuariosAsignados(List<User> usuariosAsignados) {
        this.usuariosAsignados = usuariosAsignados;
    }

    public List<Task> getSubtareas() {
        return subtareas;
    }

    public void setSubtareas(List<Task> subtareas) {
        this.subtareas = subtareas;
    }
}
