/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 *
 * @author Emanuel
 */

public class ImageLoader {
    
    private final static String path = "src/main/resources/KanbanPro.png";

    public static Image loadImage() {
        try {
            File file = new File(path);

            if (!file.exists()) {
                System.err.println("Archivo no encontrado: " + file.getAbsolutePath());
                return null;
            }

            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            return icon.getImage();

        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            return null;
        }
    }

    public static ImageIcon loadIcon() {
        try {
            File file = new File(path);

            if (!file.exists()) {
                System.err.println("Archivo no encontrado: " + file.getAbsolutePath());
                return null;
            }

            return new ImageIcon(file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error cargando icono: " + e.getMessage());
            return null;
        }
    }
}
