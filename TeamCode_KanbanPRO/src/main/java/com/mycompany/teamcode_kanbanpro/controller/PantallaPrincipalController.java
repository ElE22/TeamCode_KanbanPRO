/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    private ProyectosCardController proyectosController = null;

    public PantallaPrincipalController(ClientConnector connector) {
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

    private void putNameUserBar() {
        if (this.connector.getUserName() != null) {
            String nombreUsuario = this.connector.getUserName() + "(" + this.connector.getUserRole() + ")";
            view.setLblUsuario(nombreUsuario);
        } else {
            view.setLblUsuario("Usuario: error al cargar nombre");
            JOptionPane.showMessageDialog(view, "Error al cargar el nombre de usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attachListeners() {

        view.getBtnInicio().addActionListener(e -> view.mostrarPanel("Dashboard"));

        view.getBtnKanbanBoard().addActionListener(e -> view.mostrarPanel("Kanban Board"));

        view.getBtnProyectos().addActionListener(this::handleProyectosClick);

        view.getBtnSalir().addActionListener(this::handleLogout);
    }

    private void handleProyectosClick(ActionEvent e) {
        if (proyectosController == null) {
            ProyectosView proyectosView = view.getPanelProyectos();

            proyectosController = new ProyectosCardController(proyectosView, this.connector);
        } else {
            proyectosController.cargarProyectosIniciales();
        }
        view.mostrarPanel("Proyectos");
    }

    private void handleLogout(ActionEvent e) {
        int resp = JOptionPane.showConfirmDialog(view, "¿Deseas cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            try {
                if (connector != null) {
                    connector.close();
                }

            } catch (Exception ex) {
                System.err.println("Error al cerrar el conector principal: " + ex.getMessage());
            }
            view.dispose();
            new AuthController();
        }
    }

}
