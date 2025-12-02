/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.view.PantallaPrincipal;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author Emanuel
 */
public class PantallaPrincipalController {
    private PantallaPrincipal view;
    private ClientConnector connector;
    
    // Almacenamos el estado de los controladores para saber si ya se inicializaron
    private ProyectosController proyectosController = null;

    public PantallaPrincipalController(PantallaPrincipal view, ClientConnector connector) {
        this.view = view;
        this.connector = connector;
        
        // 1. Manejar la conexión (centralizada y persistente)
        // Ya tienes el conector abierto y validado desde el Login.

        // 2. Adjuntar los listeners a los botones del menú
        attachListeners();
        
        // Hacer la ventana visible DESPUÉS de toda la configuración
        view.setVisible(true);
    }
    
    private void attachListeners() {
        // Asume que los botones del menú de PantallaPrincipal son accesibles
        
        // Botón "Inicio"
        view.getBtnInicio().addActionListener(e -> view.mostrarPanel("Dashboard"));
        
        // Botón "Kanban Board"
        view.getBtnKanbanBoard().addActionListener(e -> view.mostrarPanel("Kanban Board"));

        // *** EL PUNTO CLAVE: Carga Diferida de Proyectos ***
        view.getBtnProyectos().addActionListener(this::handleProyectosClick);

        // Botón "Sprints"
        view.getBtnSprints().addActionListener(e -> view.mostrarPanel("Sprints"));

        // Botón "Cerrar Sesión"
        view.getBtnSalir().addActionListener(this::handleLogout);
    }

    private void handleProyectosClick(ActionEvent e) {
        // 1. Mostrar el panel primero (opcional, pero da feedback rápido)
        view.mostrarPanel("Proyectos");

        // 2. Si el controlador de Proyectos no ha sido inicializado, ¡hazlo ahora!
        if (proyectosController == null) {
            
            // Asumiendo que ProyectosView ya fue añadido al CardLayout en PantallaPrincipal
            ProyectosView proyectosView = view.getPanelProyectos(); 
            
            // Crear el controlador, pasarle su vista y el conector activo
            proyectosController = new ProyectosController(proyectosView, this.connector); 
            
            // El controlador de Proyectos es quien dispara la solicitud al servidor.
            // (La llamada a cargarProyectosIniciales() debe estar en el constructor de ProyectosController)
        }
        
        // Si ya existe el controlador, no hacemos nada más, la vista ya está visible.
    }
    
    private void handleLogout(ActionEvent e) {
        // Lógica de cerrar sesión (similar a tu código anterior)
        int resp = JOptionPane.showConfirmDialog(view, "¿Deseas cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            // Cierra el conector y luego la ventana
            try {
                if (connector != null) connector.close();
            } catch (Exception ex) {
                System.err.println("Error al cerrar el conector principal: " + ex.getMessage());
            }
            view.dispose();
            System.out.println("Sesión cerrada.");
        }
    }
}
