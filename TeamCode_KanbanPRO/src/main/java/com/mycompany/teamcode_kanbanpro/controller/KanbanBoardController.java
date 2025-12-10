package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Task; 
import com.mycompany.teamcode_kanbanpro.model.Column; 
import com.mycompany.teamcode_kanbanpro.util.ImageLoader;
import com.mycompany.teamcode_kanbanpro.view.KanbanBoardView;
import com.mycompany.teamcode_kanbanpro.view.KanbanTaskPanel;
import com.mycompany.teamcode_kanbanpro.view.KanbanColumnPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
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
    public KanbanBoardController( ClientConnector connector, int sprintId, int pId) {
        this.view = new KanbanBoardView();
        this.connector = connector;
        this.currentSprintId = sprintId;
        this.currentProjectId = pId;
        view.setController(this);
        
        loadKanbanBoard();
        attachListeners();
        this.view.setIconImage(ImageLoader.loadImage());
        
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
                       // System.err.println("Advertencia: Columna '" + taskData.getNombreColumna() + "' no encontrada para la tarea " + taskData.getTitulo());
                        JOptionPane.showMessageDialog(view, "Advertencia: Columna '" + taskData.getNombreColumna() + "' no encontrada para la tarea " + taskData.getTitulo(),
                                "Advertencia", JOptionPane.WARNING_MESSAGE);
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

    public void handleTaskMoved(Task task, Column newColumn) {
        try {
            Request req = new Request();
            
            req.setAction("movetask"); 
            Map<String, Object> payload = new HashMap<>();
            payload.put("idTarea", task.getIdTarea());
            payload.put("idColumna", newColumn.getIdColumna()); // Usamos el ID de la columna
            req.setPayload(payload);
            this.connector.setKanbanController(this);
            Response resp = connector.sendRequest(req);
            
            if (resp.isSuccess()) {
                
                task.setNombreColumna(newColumn.getNombre());
                //  System.out.println("Sincronización de movimiento de tarea exitosa.");
            } else {
                JOptionPane.showMessageDialog(view, "Error al actualizar estado en el servidor: " + resp.getMessage(), "Error de Sincronización", JOptionPane.ERROR_MESSAGE);
               
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error de conexión al actualizar el estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleIncomingTaskMovedNotification(Response resp) {
        try {
            // los datos de la tarea movida por otro usuario
            Map<String, Object> data = (Map<String, Object>) resp.getData();

            int taskId = (int) data.get("idTarea");
            int newColumnId = (int) data.get("idColumna");

            //System.out.println("Broadcast recibido: tarea " + taskId + " movida a columna " + newColumnId);

            // Buscar el panel de la tarea
            KanbanTaskPanel taskPanel = view.findTaskPanelById(taskId);

            if (taskPanel == null) {
                // System.err.println("Tarea con ID " + taskId + " no encontrada en la vista");
                JOptionPane.showMessageDialog(view, "Tarea con ID " + taskId + " no encontrada en la vista", "Error",JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar la columna destino por ID
            KanbanColumnPanel targetColumn = view.findColumnById(newColumnId);

            if (targetColumn == null) {
                // System.err.println("Columna con ID " + newColumnId + " no encontrada en la vista");
                JOptionPane.showMessageDialog(view, "Columna con ID " + newColumnId + " no encontrada en la vista","Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener los datos de la tarea
            Task task = taskPanel.getTaskData();

            // Buscar la columna actual por el nombre almacenado en la tarea
            KanbanColumnPanel currentColumn = view.findColumnByName(task.getNombreColumna());

            if (currentColumn == null) {
                // System.err.println(" Columna actual '" + task.getNombreColumna() + "' no encontrada");
                JOptionPane.showMessageDialog(view, "Columna actual '" + task.getNombreColumna() + "' no encontrada","Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si la tarea ya está en la columna destino
            if (currentColumn.getColumnData().getIdColumna() == newColumnId) {
                // System.out.println("La tarea ya está en la columna destino, no se requiere actualización");
                JOptionPane.showMessageDialog(view,"La tarea ya está en la columna destino, no se requiere actualización", "Información",JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Remover la tarea de la columna actual
            currentColumn.removeTask(taskPanel);

            // Actualizar el modelo de la tarea
            task.setNombreColumna(targetColumn.getColumnName());
            task.setIdColumna(newColumnId);

            // Agregar la tarea a la nueva columna
            targetColumn.addTask(taskPanel);

            // Refrescar la interfaz
            view.revalidate();
            view.repaint();

            // System.out.println(" Tarea '" + task.getTitulo() + "' movida exitosamente a columna '" +
            //         targetColumn.getColumnName() + "'");

        } catch (Exception e) {
            System.err.println("Error procesando broadcast: " + e.getMessage());
            JOptionPane.showMessageDialog(view, "Error al procesar notificación de tarea movida: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isVisible() {
        return view.isVisible();
    }

    public void toFront() {
        view.toFront();
    }

    public JFrame getView() {
        return view;
    }
    
}