package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Task; 
import com.mycompany.teamcode_kanbanpro.model.Column; 
import com.mycompany.teamcode_kanbanpro.model.Group; 
import com.mycompany.teamcode_kanbanpro.view.KanbanBoardView;
import com.mycompany.teamcode_kanbanpro.view.KanbanTaskPanel;
import com.mycompany.teamcode_kanbanpro.view.KanbanColumnPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 *
 * @author Emanuel
 */
public class KanbanBoardController {
    
    private final KanbanBoardView view;
    private final ClientConnector connector;
    private final String currentSprintId; 
    
    // Asume que la vista ya ha sido creada y se le pasa al constructor
    public KanbanBoardController(KanbanBoardView view, ClientConnector connector, String sprintId) {
        this.view = view;
        this.connector = connector;
        this.currentSprintId = sprintId;
        
        // Se establecen las referencias
        view.setController(this); // La vista necesita una referencia al Controller (ver modificaciones)
        
        // El controller inicia el proceso de carga
        loadKanbanBoard();
        attachListeners();
    }
    
    private void loadKanbanBoard() {
        loadColumns(); 
        loadTasks();
    }
    
    private void attachListeners() {
        // Por ahora, solo necesitamos que el ColumnTransferHandler llame al método 
        // handleTaskMoved cuando el Drag & Drop finalice, lo cual se hace a través 
        // de la referencia al controller que le pasamos a la vista en el constructor.
        
        // Si tuvieras un botón "Nueva Tarea", iría aquí:
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
            // 1. Preparar la Solicitud
            Request req = new Request();
            req.setAction("getColumnsKanbanBoard");
            Map<String, Object> payload = new HashMap<>();
            payload.put("sprintId", currentSprintId);
            req.setPayload(payload);
            
            // 2. Enviar y Recibir Respuesta
            Response resp = connector.sendRequest(req);
            
            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                // Asumimos que el conector deserializa correctamente a List<Column>
                List<Column> columns = (List<Column>) resp.getData(); 
                
                if (columns == null || columns.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "No se encontraron columnas para el sprint.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 3. Crear y agregar los paneles a la vista
                for (Column columnData : columns) {
                    // << IMPORTANTE: Pasamos el modelo Column al constructor de KanbanColumnPanel (VER MODIFICACIONES)
                    KanbanColumnPanel columnPanel = new KanbanColumnPanel(columnData, view); 
                    view.addColumn(columnPanel); 
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
            // 1. Preparar la Solicitud
            Request req = new Request();
            req.setAction("getTasksKanbanBoard");
            Map<String, Object> payload = new HashMap<>();
            payload.put("sprintId", currentSprintId);
            req.setPayload(payload);
            
            // 2. Enviar y Recibir Respuesta
            Response resp = connector.sendRequest(req);

            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Task> tasks = (List<Task>) resp.getData();

                if (tasks == null || tasks.isEmpty()) { return; }

                // 3. Distribuir cada tarea
                for (Task taskData : tasks) {
                    
                    // Crear el componente visual de la tarea
                    // << IMPORTANTE: Pasamos el modelo Task al constructor de KanbanTaskPanel (VER MODIFICACIONES)
                    KanbanTaskPanel taskPanel = new KanbanTaskPanel(
                        taskData, // Pasamos el objeto Task completo
                        view
                    );
                    
                    // Encontrar la columna destino en la vista (usamos el nombre de la columna que viene en el modelo)
                    KanbanColumnPanel targetColumn = view.findColumnByName(taskData.getNombreColumna());
                    
                    if (targetColumn != null) {
                        targetColumn.addTask(taskPanel);
                    } else {
                        System.err.println("Advertencia: Columna '" + taskData.getNombreColumna() + "' no encontrada para la tarea " + taskData.getTitulo());
                    }
                }
                
                // 4. Refrescar la vista para mostrar los cambios
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