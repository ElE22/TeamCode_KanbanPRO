/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.view.CrearSprintView;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;

import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Controlador para la creación de Sprints
 * 
 * Maneja la lógica de validación y comunicación con el servidor
 * para crear nuevos sprints dentro de un proyecto.
 * 
 * @author salaz / Emanuel
 */
public class SprintController {
    
    private ClientConnector connector;
    private CrearSprintView view;
    private int projectId;
    private Runnable onSprintCreated; // Callback para notificar creación exitosa
    
    // Patrón regex para validar formato de fecha yyyy-MM-dd
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    
    /**
     * Constructor del controlador de Sprint
     * 
     * @param view La vista del formulario de creación
     * @param connector Conexión activa al servidor
     * @param projectId ID del proyecto donde se creará el sprint
     * @param onSprintCreated Callback que se ejecuta al crear exitosamente (puede ser null)
     */
    public SprintController(CrearSprintView view, ClientConnector connector, 
                           int projectId, Runnable onSprintCreated) {
        this.view = view;
        this.connector = connector;
        this.projectId = projectId;
        this.onSprintCreated = onSprintCreated;
        
        // Configurar la ventana
        configurarVentana();
        
        // Adjuntar los listeners a los botones
        attachListeners();
    }
    
    /**
     * Configura propiedades adicionales de la ventana
     */
    private void configurarVentana() {
        // Establecer ícono de la ventana
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
        
        // Hacer la ventana modal (bloquea la ventana padre)
        view.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
    }
    
    /**
     * Adjunta los listeners a los componentes de la vista
     */
    private void attachListeners() {
        // Botón Guardar
        view.getBtnGuardar().addActionListener(e -> crearSprint());
        
        // Botón Cancelar
        view.getBtnCancelar().addActionListener(e -> {
            view.limpiarCampos();
            view.dispose();
        });
        
        // También cerrar con ESC (opcional pero útil)
        view.getRootPane().registerKeyboardAction(
            e -> view.dispose(),
            javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    /**
     * Lógica principal para crear un sprint
     */
    private void crearSprint() {
        // ========== 1. OBTENER DATOS DE LA VISTA ==========
        String nombre = view.getTxtNombre().getText().trim();
        String descripcion = view.getTxtDescripcion().getText().trim();
        String fechaInicio = view.getTxtFechaInicio().getText().trim();
        String fechaFin = view.getTxtFechaFin().getText().trim();

        // ========== 2. VALIDACIONES DEL CLIENTE ==========
        
        // Validar nombre obligatorio
        if (nombre.isEmpty()) {
            mostrarError("El nombre del sprint es obligatorio.");
            view.getTxtNombre().requestFocus();
            return;
        }
        
        // Validar longitud del nombre
        if (nombre.length() < 3) {
            mostrarError("El nombre del sprint debe tener al menos 3 caracteres.");
            view.getTxtNombre().requestFocus();
            return;
        }
        
        if (nombre.length() > 100) {
            mostrarError("El nombre del sprint no puede exceder 100 caracteres.");
            view.getTxtNombre().requestFocus();
            return;
        }

        // Validar fecha de inicio obligatoria
        if (fechaInicio.isEmpty()) {
            mostrarError("La fecha de inicio es obligatoria.");
            view.getTxtFechaInicio().requestFocus();
            return;
        }

        // Validar fecha de fin obligatoria
        if (fechaFin.isEmpty()) {
            mostrarError("La fecha de fin es obligatoria.");
            view.getTxtFechaFin().requestFocus();
            return;
        }

        // Validar formato de fecha de inicio
        if (!DATE_PATTERN.matcher(fechaInicio).matches()) {
            mostrarError("Formato de fecha de inicio inválido.\n\n" +
                        "Use el formato: YYYY-MM-DD\n" +
                        "Ejemplo: 2024-12-31");
            view.getTxtFechaInicio().requestFocus();
            view.getTxtFechaInicio().selectAll();
            return;
        }

        // Validar formato de fecha de fin
        if (!DATE_PATTERN.matcher(fechaFin).matches()) {
            mostrarError("Formato de fecha de fin inválido.\n\n" +
                        "Use el formato: YYYY-MM-DD\n" +
                        "Ejemplo: 2024-12-31");
            view.getTxtFechaFin().requestFocus();
            view.getTxtFechaFin().selectAll();
            return;
        }

        // Validar que las fechas sean válidas en el calendario
        if (!esFechaValida(fechaInicio)) {
            mostrarError("La fecha de inicio no es una fecha válida del calendario.\n" +
                        "Verifique el día y mes ingresados.");
            view.getTxtFechaInicio().requestFocus();
            return;
        }

        if (!esFechaValida(fechaFin)) {
            mostrarError("La fecha de fin no es una fecha válida del calendario.\n" +
                        "Verifique el día y mes ingresados.");
            view.getTxtFechaFin().requestFocus();
            return;
        }
        
        // Validar que fecha fin sea posterior a fecha inicio
        try {
            java.sql.Date inicio = java.sql.Date.valueOf(fechaInicio);
            java.sql.Date fin = java.sql.Date.valueOf(fechaFin);
            
            if (fin.before(inicio)) {
                mostrarError("La fecha de fin debe ser posterior a la fecha de inicio.");
                view.getTxtFechaFin().requestFocus();
                return;
            }
            
            if (fin.equals(inicio)) {
                // Advertencia pero permitir (sprint de un día)
                int respuesta = JOptionPane.showConfirmDialog(view,
                    "Las fechas de inicio y fin son iguales.\n" +
                    "¿Desea crear un sprint de un solo día?",
                    "Confirmar duración",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        } catch (IllegalArgumentException e) {
            mostrarError("Error al procesar las fechas. Verifique el formato.");
            return;
        }

        // ========== 3. DESHABILITAR BOTÓN PARA EVITAR DOBLE CLICK ==========
        view.getBtnGuardar().setEnabled(false);
        view.getBtnGuardar().setText("Creando...");
        view.getBtnCancelar().setEnabled(false);

        try {
            // ========== 4. CREAR REQUEST ==========
            Request req = new Request();
            req.setAction("createSprint");
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", projectId);
            payload.put("nombre", nombre);
            payload.put("descripcion", descripcion);
            payload.put("fechaInicio", fechaInicio);
            payload.put("fechaFin", fechaFin);
            req.setPayload(payload);

            // ========== 5. ENVIAR AL SERVIDOR ==========
            System.out.println("Enviando solicitud de creación de sprint...");
            System.out.println("  - Proyecto ID: " + projectId);
            System.out.println("  - Nombre: " + nombre);
            System.out.println("  - Fecha inicio: " + fechaInicio);
            System.out.println("  - Fecha fin: " + fechaFin);
            
            Response resp = connector.sendRequest(req);

            // ========== 6. PROCESAR RESPUESTA ==========
            if (resp.isSuccess()) {
                System.out.println("Sprint creado exitosamente!");
                
                JOptionPane.showMessageDialog(view, 
                    "Sprint '" + nombre + "' creado exitosamente.", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Limpiar y cerrar el formulario
                view.limpiarCampos();
                view.dispose();
                
                // Ejecutar callback para refrescar la tabla de sprints
                if (onSprintCreated != null) {
                    System.out.println("Ejecutando callback para refrescar tabla...");
                    onSprintCreated.run();
                }
            } else {
                System.err.println("Error del servidor: " + resp.getMessage());
                mostrarError("Error del servidor:\n" + resp.getMessage());
                rehabilitarBotones();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error de comunicación con el servidor:\n" + ex.getMessage());
            rehabilitarBotones();
        }
    }
    
    /**
     * Rehabilita los botones después de un error
     */
    private void rehabilitarBotones() {
        view.getBtnGuardar().setEnabled(true);
        view.getBtnGuardar().setText("Guardar Sprint");
        view.getBtnCancelar().setEnabled(true);
    }

    /**
     * Valida si una fecha en formato string es válida en el calendario
     * @param fecha Fecha en formato yyyy-MM-dd
     * @return true si es válida, false si no
     */
    private boolean esFechaValida(String fecha) {
        try {
            java.sql.Date.valueOf(fecha);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Muestra un mensaje de error al usuario
     * @param mensaje El mensaje a mostrar
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(view, 
            mensaje, 
            "Error de Validación", 
            JOptionPane.ERROR_MESSAGE);
    }
}





