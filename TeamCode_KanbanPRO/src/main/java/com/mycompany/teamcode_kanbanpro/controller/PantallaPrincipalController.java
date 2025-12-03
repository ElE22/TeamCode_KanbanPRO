package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.view.PantallaPrincipal;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Emanuel
 */
public class PantallaPrincipalController {
    private PantallaPrincipal view;
    private ClientConnector connector;
    
    // Almacenamos el estado de los controladores para saber si ya se inicializaron
    private ProyectosCardController proyectosController = null;

    public PantallaPrincipalController( ClientConnector connector) {
        this.view = new PantallaPrincipal();
        this.connector = connector;
        putNameUserBar();
        attachListeners();
        setIconoVentana();
        view.setVisible(true);
    }

    private void setIconoVentana() {
        // icono de la ventana
        java.net.URL imgURL = getClass().getResource("/com/mycompany/teamcode_kanbanpro/images/KanbanPro.png");
        if (imgURL != null) {
            ImageIcon icono = new ImageIcon(imgURL);
            view.setIconImage(icono.getImage());
        } else {
            System.err.println("No se pudo cargar el ícono de la aplicación.");
        }
    }
    
    private void putNameUserBar(){
        if (this.connector.getUserName() != null) {
            String nombreUsuario = this.connector.getUserName() + "(" + this.connector.getUserRole() + ")";
            view.setLblUsuario(nombreUsuario);
        }else {
            view.setLblUsuario("Usuario: error al cargar nombre");
            JOptionPane.showMessageDialog(view, "Error al cargar el nombre de usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attachListeners() {
        
        // boton inicio
        view.getBtnInicio().addActionListener(e -> view.mostrarPanel("Dashboard"));
        
        //boton Kanban Board
        view.getBtnKanbanBoard().addActionListener(e -> view.mostrarPanel("Kanban Board"));

        // para manejar los clicks en proyectos
        view.getBtnProyectos().addActionListener(this::handleProyectosClick);

        // boton logout
        view.getBtnSalir().addActionListener(this::handleLogout);
    }

    private void handleProyectosClick(ActionEvent e) {
        if (proyectosController == null) {
            ProyectosView proyectosView = view.getPanelProyectos(); 
            // Crear el controlador, pasarle su vista y el conector activo
            proyectosController = new ProyectosCardController(proyectosView, this.connector); 
        }else {
            proyectosController.cargarProyectosIniciales();
        }
        view.mostrarPanel("Proyectos");
    }
    
    private void handleLogout(ActionEvent e) {
        int resp = JOptionPane.showConfirmDialog(view, "¿Deseas cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            try {
                if (connector != null) {connector.close();}
                
            } catch (Exception ex) {
                System.err.println("Error al cerrar el conector principal: " + ex.getMessage());
            }
            view.dispose();
            new AuthController(); 
        }
    }
        
}
