package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.util.ImageLoader;
import com.mycompany.teamcode_kanbanpro.view.PantallaPrincipal;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;
import com.mycompany.teamcode_kanbanpro.view.GrupoView;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author Emanuel
 */
public class PantallaPrincipalController {

    private PantallaPrincipal view;
    private ClientConnector connector;
    private PermissionManager permission;

    private ProyectosCardController proyectosController = null;
    private GroupController grupoController = null;

    public PantallaPrincipalController(ClientConnector connector) {
        this.view = new PantallaPrincipal();
        this.connector = connector;
        this.permission = new PermissionManager(this.connector.getUserRole());
        putNameUserBar();
        configurarPermisosGrupos();
        attachListeners();
        view.setIconImage(ImageLoader.loadImage());
        view.setVisible(true);
    }
    
    private void configurarPermisosGrupos() {
   
            // Desarrolladores pueden ver sus grupos pero no crear/gestionar
            view.getBtnGrupos().setVisible(true);
            view.getBtnGrupos().setEnabled(true);
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

        view.getBtnGrupos().addActionListener(this::handleGruposClick);

        view.getBtnProyectos().addActionListener(this::handleProyectosClick);

        view.getBtnSalir().addActionListener(this::handleLogout);
    }
    //nuevo
    private void handleProyectosClick(ActionEvent e) {
        if (proyectosController == null) {
            ProyectosView proyectosView = view.getPanelProyectos();

            proyectosController = new ProyectosCardController(proyectosView, this.connector, permission);
        } else {
            proyectosController.cargarProyectosIniciales();
        }
        view.mostrarPanel("Proyectos");
    }
    
     private void handleGruposClick(ActionEvent e) {
        if (grupoController == null) {
            GrupoView grupoView = view.getPanelGrupos();
            grupoController = new GroupController(grupoView, this.connector, permission);
        } else {
            // Refrescar datos al volver al panel
            grupoController.cargarTodosLosGrupos();
        }
        view.mostrarPanel("Grupos");
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
    
     public PantallaPrincipal getView() {
        return view;
    }
    
    public ClientConnector getConnector() {
        return connector;
    }
    
    public PermissionManager getPermission() {
        return permission;
    }

}