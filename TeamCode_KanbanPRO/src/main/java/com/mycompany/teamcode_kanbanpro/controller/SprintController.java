/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.view.CrearSprintView;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.util.ImageLoader;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Emanuel
 */
public class SprintController {

    private ClientConnector connector;
    private CrearSprintView view;
    private int projectId;
    private Runnable onSprintCreated;
    //duracion minima recomendada de un sprint
    private static final int DURACION_MINIMA_SPRINT = 8;
    private static final int DURACION_MAXIMA_SPRINT = 60;

    public SprintController(CrearSprintView view, ClientConnector connector,
            int projectId, Runnable onSprintCreated) {
        this.view = view;
        this.connector = connector;
        this.projectId = projectId;
        this.onSprintCreated = onSprintCreated;

        configurarVentana();
        attachListeners();
        this.view.setIconImage(ImageLoader.loadImage());
    }

    private void configurarVentana() {
        // Hacer la ventana modal (bloquea la ventana padre)
        view.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
    }

    private void attachListeners() {
        view.getBtnGuardar().addActionListener(e -> crearSprint());

        view.getBtnCancelar().addActionListener(e -> {
            view.limpiarCampos();
            view.dispose();
        });

        view.getRootPane().registerKeyboardAction(
                e -> view.dispose(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    //Logica principal para crear un sprint
    private void crearSprint() {
        String nombre = view.getTxtNombre().getText().trim();
        String descripcion = view.getTxtDescripcion().getText().trim();
        
        // Obtener las fechas de los JDateChooser
        Date fechaInicioDate = view.getDateChooserInicio().getDate();
        Date fechaFinDate = view.getDateChooserFin().getDate();

        // ========== VALIDACIÓN DEL NOMBRE ==========
        if (nombre.isEmpty()) {
            mostrarError("El nombre del sprint es obligatorio.");
            view.getTxtNombre().requestFocus();
            return;
        }

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

        
        // Validar que se hayan seleccionado ambas fechas
        if (fechaInicioDate == null) {
            mostrarError("La fecha de inicio es obligatoria.");
            view.getDateChooserInicio().requestFocus();
            return;
        }

        if (fechaFinDate == null) {
            mostrarError("La fecha de fin es obligatoria.");
            view.getDateChooserFin().requestFocus();
            return;
        }

        // Convertir Date a LocalDate para facilitar comparaciones
        LocalDate fechaInicio = fechaInicioDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        LocalDate fechaFin = fechaFinDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Validar que la fecha de fin sea posterior a la de inicio
        if (fechaFin.isBefore(fechaInicio)) {
            mostrarError("La fecha de fin debe ser posterior a la fecha de inicio.");
            view.getDateChooserFin().requestFocus();
            return;
        }

        // Validar que la fecha de fin no sea igual a la de inicio
        if (fechaFin.isEqual(fechaInicio)) {
            mostrarError("Un sprint no puede durar un solo día.\n"
                    + "La duración mínima recomendada es de " + DURACION_MINIMA_SPRINT + " días.");
            view.getDateChooserFin().requestFocus();
            return;
        }

        // Calcular la duracio del sprint en días
        long duracionDias = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1; // +1 para incluir ambos días

        // Validar duracion minima del sprint
        if (duracionDias < DURACION_MINIMA_SPRINT) {
            // Preguntar al usuario si desea continuar con un sprint corto
            int respuesta = JOptionPane.showConfirmDialog(view,
                    "La duración del sprint es de " + duracionDias + " días.\n"
                    + "La duración mínima recomendada es de " + DURACION_MINIMA_SPRINT + " días.\n\n"
                    + "¿Desea crear el sprint de todas formas?",
                    "Sprint con duración corta",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        //advertir si el sprint es muy largo
        if (duracionDias > DURACION_MAXIMA_SPRINT) {
            int respuesta = JOptionPane.showConfirmDialog(view,
                    "La duración del sprint es de " + duracionDias + " días.\n"
                    + "Esto es más largo que la duración típica de un sprint (" + DURACION_MAXIMA_SPRINT + " días).\n\n"
                    + "¿Desea continuar?",
                    "Sprint con duración larga",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        //convertir a fromato sql.Date
        java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicioDate.getTime());
        java.sql.Date sqlFechaFin = new java.sql.Date(fechaFinDate.getTime());

        // Convertir a formato String para el servidor (yyyy-MM-dd)
        String fechaInicioStr = sqlFechaInicio.toString(); // Formato: yyyy-MM-dd
        String fechaFinStr = sqlFechaFin.toString();       // Formato: yyyy-MM-dd

        // eviatamos el doble clic
        view.getBtnGuardar().setEnabled(false);
        view.getBtnGuardar().setText("Creando...");
        view.getBtnCancelar().setEnabled(false);

        try {
            Request req = new Request();
            req.setAction("createSprint");

            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", projectId);
            payload.put("nombre", nombre);
            payload.put("descripcion", descripcion);
            payload.put("fechaInicio", fechaInicioStr);
            payload.put("fechaFin", fechaFinStr);
            req.setPayload(payload);

            Response resp = connector.sendRequest(req);

            if (resp.isSuccess()) {

                JOptionPane.showMessageDialog(view,
                        "Sprint '" + nombre + "' creado exitosamente.\n"
                        + "Duración: " + duracionDias + " días",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                view.limpiarCampos();
                view.dispose();

                if (onSprintCreated != null) {
                    // llamar al callback para refrescar la lista de sprints
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

    private void rehabilitarBotones() {
        view.getBtnGuardar().setEnabled(true);
        view.getBtnGuardar().setText("Guardar Sprint");
        view.getBtnCancelar().setEnabled(true);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(view,
                mensaje,
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
    }
}