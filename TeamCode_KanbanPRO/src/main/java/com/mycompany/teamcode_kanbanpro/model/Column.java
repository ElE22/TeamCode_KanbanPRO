
package com.mycompany.teamcode_kanbanpro.model;

/**
 *
 * @author Emanuel
 */
public class Column {
    
    private int idColumna;
    private int idProyecto;
    private String nombre;
    private int orden;
    private String color; // Almacena el color en formato hexadecimal (#RRGGBB)

    // Constructor vacío (útil para frameworks de serialización como Gson/Jackson)
    public Column() {
    }

    // Constructor completo (útil para crear objetos manualmente o en pruebas)
    public Column(int idColumna, int idProyecto, String nombre, int orden, String color) {
        this.idColumna = idColumna;
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.orden = orden;
        this.color = color;
    }

    // --- Getters y Setters ---

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
