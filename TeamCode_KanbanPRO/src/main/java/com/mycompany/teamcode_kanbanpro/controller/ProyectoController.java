/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.controller;
import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Group;
import com.mycompany.teamcode_kanbanpro.view.CrearProyectoView;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 * Controlador para la creación de Proyectos
 * 
 * Maneja la lógica de:
 * - Cargar grupos disponibles desde el servidor
 * - Validar datos del formulario
 * - Enviar solicitud de creación al servidor
 * 
 * @author salaz
 */
public class ProyectoController {
     private CrearProyectoView view;
    private ClientConnector connector;
    private Runnable onProyectoCreado; // Callback para notificar creación exitosa
    
    /**
     * Constructor del controlador
     * 
     * @param view La vista del formulario de creación
     * @param connector Conexión activa al servidor
     * @param onProyectoCreado Callback que se ejecuta al crear exitosamente
     */
    public ProyectoController(CrearProyectoView view, ClientConnector connector, 
                              Runnable onProyectoCreado) {
        this.view = view;
        this.connector = connector;
        this.onProyectoCreado = onProyectoCreado;
        
        // Configurar la ventana
        configurarVentana();
        
        // Cargar grupos disponibles
       // cargarGruposDisponibles();
        
        // Adjuntar listeners
        attachListeners();
    }
    
    /**
     * Configura propiedades de la ventana
     */
    private void configurarVentana() {
        try {
            java.net.URL imgURL = getClass().getResource(
                "/com/mycompany/teamcode_kanbanpro/images/KanbanPro.png");
            if (imgURL != null) {
                ImageIcon icono = new ImageIcon(imgURL);
                view.setIconImage(icono.getImage());
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el ícono: " + e.getMessage());
        }
        
        // Cerrar con ESC
        view.getRootPane().registerKeyboardAction(
            e -> view.dispose(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    /**
     * Carga los grupos disponibles desde el servidor
     */
    /*
    private void cargarGruposDisponibles() {
        try {
            Request req = new Request();
            req.setAction("getAllGroups");
            
            Response resp = connector.sendRequest(req);
            
            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Group> grupos = (List<Group>) resp.getData();
                view.cargarGruposDisponibles(grupos);
                System.out.println("[ProyectoController] Grupos cargados: " + 
                                 (grupos != null ? grupos.size() : 0));
            } else {
                mostrarError("Error al cargar grupos: " + resp.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error de comunicación al cargar grupos:\n" + e.getMessage());
        }
    }
    
    /**
     * Adjunta los listeners a los componentes de la vista
     */
    private void attachListeners() {
        // Botón Guardar
        view.getBtnGuardar().addActionListener(e -> crearProyecto());
        
        // Botón Cancelar
        view.getBtnCancelar().addActionListener(e -> {
            view.limpiarCampos();
            view.dispose();
        });
    }
    
    /**
     * Lógica principal para crear un proyecto
     */
    private void crearProyecto() {
        // ========== 1. OBTENER DATOS ==========
        String nombre = view.getTxtNombre().getText().trim();
        String descripcion = view.getTxtDescripcion().getText().trim();
       // List<Integer> gruposIds = view.getGruposAsignadosIds();
        
        // ========== 2. VALIDACIONES ==========
        
        // Validar nombre
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
        /*
        // Validar grupos asignados
        if (gruposIds.isEmpty()) {
            mostrarError("Debe asignar al menos un grupo al proyecto.\n\n" +
                        "Seleccione grupos de la lista izquierda y use el botón '>>' " +
                        "para agregarlos.");
            return;
        }
        */
        // ========== 3. CONFIRMAR CREACIÓN ==========
        /*
        int confirmacion = JOptionPane.showConfirmDialog(view,
            "¿Desea crear el proyecto '" + nombre + "' con " + 
            gruposIds.size() + " grupo(s) asignado(s)?",
            "Confirmar creación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        */
        // ========== 4. DESHABILITAR BOTONES ==========
        view.getBtnGuardar().setEnabled(false);
        view.getBtnGuardar().setText("Creando...");
        view.getBtnCancelar().setEnabled(false);
        
        try {
            // ========== 5. CREAR REQUEST ==========
            Request req = new Request();
            req.setAction("createProject");
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("nombre", nombre);
            payload.put("descripcion", descripcion);
            payload.put("creadorId", connector.getUserID());
           // payload.put("gruposIds", gruposIds);
            req.setPayload(payload);
            
            System.out.println("[ProyectoController] Enviando solicitud de creación...");
            System.out.println("  - Nombre: " + nombre);
            System.out.println("  - Creador ID: " + connector.getUserID());
           // System.out.println("  - Grupos: " + gruposIds);
            
            // ========== 6. ENVIAR AL SERVIDOR ==========
            Response resp = connector.sendRequest(req);
            
            // ========== 7. PROCESAR RESPUESTA ==========
            if (resp.isSuccess()) {
                System.out.println("[ProyectoController] Proyecto creado exitosamente!");
                
                JOptionPane.showMessageDialog(view,
                    "Proyecto '" + nombre + "' creado exitosamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                
                view.limpiarCampos();
                view.dispose();
                
                // Ejecutar callback para refrescar la tabla
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
    
    /**
     * Rehabilita los botones después de un error
     */
    private void rehabilitarBotones() {
        view.getBtnGuardar().setEnabled(true);
        view.getBtnGuardar().setText("Crear Proyecto");
        view.getBtnCancelar().setEnabled(true);
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(view,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
