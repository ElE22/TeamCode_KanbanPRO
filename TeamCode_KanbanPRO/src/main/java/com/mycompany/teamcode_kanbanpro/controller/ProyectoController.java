/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Group;
import com.mycompany.teamcode_kanbanpro.util.ImageLoader;
import com.mycompany.teamcode_kanbanpro.view.CrearProyectoView;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @author salaz
 */
public class ProyectoController {

    private CrearProyectoView view;
    private ClientConnector connector;
    private Runnable onProyectoCreado;

    public ProyectoController(CrearProyectoView view, ClientConnector connector,Runnable onProyectoCreado) {
        this.view = view;
        this.connector = connector;
        this.onProyectoCreado = onProyectoCreado;
        
        configurarVentana();
        
        cargarGruposDelUsuario();
        
        attachListeners();
        this.view.setIconImage(ImageLoader.loadImage());
    }

    private void configurarVentana() {
        // Cerrar con ESC
        view.getRootPane().registerKeyboardAction(
                e -> view.dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

   
    private void cargarGruposDelUsuario() {
        try {
            Request req = new Request();
            req.setAction("getallgroups");
            Response resp = connector.sendRequest(req);
            
            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Group> grupos = (List<Group>) resp.getData();
                
                if (grupos == null || grupos.isEmpty()) {
                    // Usuario no pertenece a ningún grupo
                    mostrarError("No perteneces a ningún grupo.\n\n" +
                                "Debes ser miembro de al menos un grupo para crear proyectos.\n" +
                                "Contacta al administrador para ser asignado a un grupo.");
                    view.getBtnGuardar().setEnabled(true);
                    view.dispose();
                    return;
                }
                
                view.cargarGrupos(grupos);
                System.out.println("[ProyectoController] Grupos del usuario cargados: " + grupos.size());
            } else {
                mostrarError("Error al cargar grupos: " + resp.getMessage());
                view.getBtnGuardar().setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error de comunicación al cargar grupos:\n" + e.getMessage());
            view.getBtnGuardar().setEnabled(false);
        }
    }

    private void attachListeners() {
        view.getBtnGuardar().addActionListener(e -> crearProyecto());

        view.getBtnCancelar().addActionListener(e -> {
            view.limpiarCampos();
            view.dispose();
        });
    }

    private void crearProyecto() {
        // OBTENER DATOS
        String nombre = view.getTxtNombre().getText().trim();
        String descripcion = view.getTxtDescripcion().getText().trim();
        
        int grupoId = view.getGrupoSeleccionadoId();

        // VALIDACIONES
        if (nombre.isEmpty()) {
            mostrarError("El nombre del proyecto es obligatorio.");
            view.getTxtNombre().requestFocus();
            return;
        }

        if (nombre.length() < 3) {
            mostrarError("El nombre debe tener al menos 3 caracteres.");
            view.getTxtNombre().requestFocus();
            return;
        }

        if (nombre.length() > 100) {
            mostrarError("El nombre no puede exceder 100 caracteres.");
            view.getTxtNombre().requestFocus();
            return;
        }

        if (!view.tieneGrupoSeleccionado() || grupoId <= 0) {
            mostrarError("Debe seleccionar un grupo para el proyecto.\n\n" +
                        "El proyecto será visible para todos los miembros del grupo seleccionado.");
            view.getCmbGrupos().requestFocus();
            return;
        }

        // DESHABILITAR BOTONES 
        view.getBtnGuardar().setEnabled(false);
        view.getBtnGuardar().setText("Creando...");
        view.getBtnCancelar().setEnabled(false);

        try {
            Request req = new Request();
            req.setAction("createProject");

            Map<String, Object> payload = new HashMap<>();
            payload.put("nombre", nombre);
            payload.put("descripcion", descripcion);
            payload.put("creadorId", connector.getUserID());
            // === NUEVA INTEGRACIÓN: Enviar ID del grupo ===
            payload.put("grupoId", grupoId);
            req.setPayload(payload);

            System.out.println("[ProyectoController] Enviando solicitud de creación...");
            System.out.println("  - Nombre: " + nombre);
            System.out.println("  - Creador ID: " + connector.getUserID());
            System.out.println("  - Grupo ID: " + grupoId);

            Response resp = connector.sendRequest(req);

            if (resp.isSuccess()) {
                System.out.println("[ProyectoController] Proyecto creado exitosamente!");

                JOptionPane.showMessageDialog(view,
                        "Proyecto '" + nombre + "' creado exitosamente.\n" +
                        "El proyecto ha sido asignado al grupo seleccionado.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                view.limpiarCampos();
                view.dispose();

                if (onProyectoCreado != null) {
                    onProyectoCreado.run();
                }
            } else {
                System.err.println("[ProyectoController] Error: " + resp.getMessage());
                mostrarError("Error del servidor:\n" + resp.getMessage());
                rehabilitarBotones();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error de comunicación con el servidor:\n" + e.getMessage());
            rehabilitarBotones();
        }
    }

    private void rehabilitarBotones() {
        view.getBtnGuardar().setEnabled(true);
        view.getBtnGuardar().setText("Crear Proyecto");
        view.getBtnCancelar().setEnabled(true);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(view,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}