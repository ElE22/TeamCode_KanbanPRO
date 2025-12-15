/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.model;

import java.io.Serializable;

/**
 *
 * @author escobe11
 */
public class Priority implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int idPrioridad;
    private String nombre;
    private int peso;
    private String color;
    
    // Constructores
    
    public Priority() {
    }
    
    public Priority(int idPrioridad, String nombre, int peso, String color) {
        this.idPrioridad = idPrioridad;
        this.nombre = nombre;
        this.peso = peso;
        this.color = color;
    }
    
    public Priority(String nombre, int peso, String color) {
        this.nombre = nombre;
        this.peso = peso;
        this.color = color;
    }
    
    // Getters y Setters
    
    public int getIdPrioridad() {
        return idPrioridad;
    }
    
    public void setIdPrioridad(int idPrioridad) {
        this.idPrioridad = idPrioridad;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getPeso() {
        return peso;
    }
    
    public void setPeso(int peso) {
        this.peso = peso;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    // toString
    
    @Override
    public String toString() {
        return "Priority{" +
                "idPrioridad=" + idPrioridad +
                ", nombre='" + nombre + '\'' +
                ", peso=" + peso +
                ", color='" + color + '\'' +
                '}';
    }
    
    // equals y hashCode (basados en idPrioridad)
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Priority priority = (Priority) o;
        return idPrioridad == priority.idPrioridad;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idPrioridad);
    }
}