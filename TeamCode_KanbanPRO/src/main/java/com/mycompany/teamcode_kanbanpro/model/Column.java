
package com.mycompany.teamcode_kanbanpro.model;

import java.io.Serializable;

/**
 *
 * @author Emanuel
 */
public class Column implements Serializable {
    
    private int idColumna;
    private int idProyecto;
    private String nombre;
    private int orden;
    private String color;

    public Column() {
    }

    public Column(int idColumna, int idProyecto, String nombre, int orden, String color) {
        this.idColumna = idColumna;
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.orden = orden;
        this.color = color;
    }

    public int getIdColumna() {
        return idColumna;
    }

    public void setIdColumna(int idColumna) {
        this.idColumna = idColumna;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
