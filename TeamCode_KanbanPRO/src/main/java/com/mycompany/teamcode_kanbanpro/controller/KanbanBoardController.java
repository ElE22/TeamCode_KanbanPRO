package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Task; 
import com.mycompany.teamcode_kanbanpro.model.Column; 
import com.mycompany.teamcode_kanbanpro.view.KanbanBoardView;
import com.mycompany.teamcode_kanbanpro.view.KanbanTaskPanel;
import com.mycompany.teamcode_kanbanpro.view.KanbanColumnPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Emanuel
 */
public class KanbanBoardController {
    
    private final KanbanBoardView view;
    private final ClientConnector connector;
    private final int currentSprintId; 
    private final int currentProjectId; 
    // Asume que la vista ya ha sido creada y se le pasa al constructor
    public KanbanBoardController( ClientConnector connector, int sprintId, int pId) {
        this.view = new KanbanBoardView();
        this.connector = connector;
        this.currentSprintId = sprintId;
        this.currentProjectId = pId;
        
        // Se establecen las referencias
        view.setController(this); // La vista necesita una referencia al Controller (ver modificaciones)
        
        // El controller inicia el proceso de carga
        loadKanbanBoard();
        attachListeners();
        
        this.view.setVisible(true);
    }
    
    private void loadKanbanBoard() {
        loadColumns(); 
        loadTasks();
    }
    
    private void attachListeners() {
        
        view.getCreateTaskButton().addActionListener(e -> handleNewTask());
    }
    
    
    private void handleNewTask(){
       
        JOptionPane.showMessageDialog(view, "Funcionalidad de crear tarea aquí");
    }
    
    /**
     * Solicita al servidor la lista de columnas para el tablero.
     */
    private void loadColumns() {
        try {
            Request req = new Request();
            req.setAction("getcolumnskanbanboard");
            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", currentProjectId);
            req.setPayload(payload);
            Response resp = connector.sendRequest(req);
            
            
            if (resp.isSuccess()) {
                List<Column> columnsServer = (List<Column>) resp.getData(); 
              
                if (columnsServer == null || columnsServer.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "No se encontraron columnas para el sprint.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                 // setup de las columnas que va ocupar el boardPanel
                view.setLayoutBoard(columnsServer.size());
               
                for (Column columnData : columnsServer) {
                    KanbanColumnPanel columnPanel = new KanbanColumnPanel(columnData, view); 
                    view.addColumns(columnPanel); 
                }
                
            } else {
                JOptionPane.showMessageDialog(view, "Error de servidor al cargar columnas: " + resp.getMessage(), "Error de Servidor", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error de conexión al cargar las columnas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Solicita al servidor las tareas y las distribuye en las columnas.
     */
    private void loadTasks() {
        try {
            Request req = new Request();
            req.setAction("gettasksbysprint");
            Map<String, Object> payload = new HashMap<>();
            payload.put("sprintId", currentSprintId);
            req.setPayload(payload);
            
            Response resp = connector.sendRequest(req);
            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Task> tasks = (List<Task>) resp.getData();

                if (tasks == null || tasks.isEmpty()) { return; }
                for (Task taskData : tasks) {
                    KanbanTaskPanel taskPanel = new KanbanTaskPanel(
                        taskData, // Pasamos el objeto Task completo
                        view
                    );
                    
                    // Encontrar la columna destino en la vista 
                    KanbanColumnPanel targetColumn = view.findColumnByName(taskData.getNombreColumna());
                    
                    if (targetColumn != null) {
                        targetColumn.addTask(taskPanel);
                    } else {
                        System.err.println("Advertencia: Columna '" + taskData.getNombreColumna() + "' no encontrada para la tarea " + taskData.getTitulo());
                    }
                }
                view.revalidate();
                view.repaint();

            } else {
                JOptionPane.showMessageDialog(view, "Error de servidor al cargar tareas: " + resp.getMessage(), "Error de Servidor", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error de conexión al cargar las tareas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // =================================================================
    // MÉTODOS PARA MANEJAR EVENTOS (RESPUESTA a la interacción)
    // =================================================================

    /**
     * Método que maneja la lógica de negocio cuando una tarea ha sido movida.
     * Es llamado por el ColumnTransferHandler.
     * @param task El objeto Task completo que fue movido.
     * @param newColumn El objeto Column completo de la columna de destino.
     */
    public void handleTaskMoved(Task task, Column newColumn) {
        System.out.println("Tarea '" + task.getTitulo() + "' movida a la columna '" + newColumn.getNombre() + "'. ID Columna: " + newColumn.getIdColumna());
        
        try {
            // 1. Preparar la Solicitud de Actualización
            Request req = new Request();
            req.setAction("updateTaskStatus");
            Map<String, Object> payload = new HashMap<>();
            payload.put("idTarea", task.getIdTarea());
            payload.put("idColumna", newColumn.getIdColumna()); // Usamos el ID de la columna
            req.setPayload(payload);
            
            // 2. Enviar y Recibir Respuesta
            Response resp = connector.sendRequest(req);
            
            if (!resp.isSuccess()) {
                JOptionPane.showMessageDialog(view, "Error al actualizar estado en el servidor: " + resp.getMessage(), "Error de Sincronización", JOptionPane.ERROR_MESSAGE);
                // NOTA: Idealmente, aquí se revierte el movimiento visual si la actualización falla.
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error de conexión al actualizar el estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}