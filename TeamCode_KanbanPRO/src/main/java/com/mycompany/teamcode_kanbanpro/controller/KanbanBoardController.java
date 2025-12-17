package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Task; 
import com.mycompany.teamcode_kanbanpro.model.Column;
import com.mycompany.teamcode_kanbanpro.model.Priority;
import com.mycompany.teamcode_kanbanpro.util.ImageLoader;
import com.mycompany.teamcode_kanbanpro.view.CreateTaskDialog;
import com.mycompany.teamcode_kanbanpro.view.KanbanBoardView;
import com.mycompany.teamcode_kanbanpro.view.KanbanTaskPanel;
import com.mycompany.teamcode_kanbanpro.view.KanbanColumnPanel;
import java.util.ArrayList;

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
    private final String currentProjectName;
    private final String currentSprintName;

    public KanbanBoardController( ClientConnector connector, int sprintId, int pId, String pNombre, String sNombre) {
        this.view = new KanbanBoardView();
        this.connector = connector;
        this.currentSprintId = sprintId;
        this.currentProjectId = pId;
        this.currentProjectName = pNombre;
        this.currentSprintName = sNombre;
        view.setController(this);
        
        this.connector.setKanbanController(this);
        loadKanbanBoard();
        attachListeners();
        this.view.setIconImage(ImageLoader.loadImage());
        view.setTitle("Pizarra Kanban - Proyecto: " + currentProjectName + " | Sprint: " + currentSprintName);
        view.setTitleLabel("Proyecto (" + currentProjectName + ") | Sprint (" + currentSprintName + ")");
        this.view.setVisible(true);
    }
    
    private void loadKanbanBoard() {
        loadColumns(); 
        loadTasks();
    }
    
    private void attachListeners() {
        
        view.getCreateTaskButton().addActionListener(e -> handleNewTask());
    }
    
    
//    private void handleNewTask(){
//       
//        JOptionPane.showMessageDialog(view, "Funcionalidad de crear tarea aquí");
//    }
    
    private void loadColumns() {
        try {
            Request req = new Request();
            req.setAction("getcolumnskanbanboard");
            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", currentProjectId);
            req.setPayload(payload);
            Response resp = connector.sendRequest(req);
            
            
            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
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

    public void hanlderIncomingTaskUpdatedNotification(Response resp) {
        // Implementar si es necesario
        if (resp == null) {
            JOptionPane.showMessageDialog(view, "Respuesta nula recibida en la notificación de tarea actualizada", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String action = resp.getAction();
        if (action == null) {
            JOptionPane.showMessageDialog(view, "Acción nula recibida en la notificación de tarea actualizada", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        switch (action) {
            case "taskmoved":
                handleIncomingTaskMovedNotification(resp);
                break;
            case "taskcreated":
                handleIncomingTaskCreatedNotification(resp);
                break;
            default:
                JOptionPane.showMessageDialog(view, "Acción desconocida recibida en la notificación de tarea actualizada: " + action, "Error", JOptionPane.ERROR_MESSAGE);
                break;
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
            if(resp == null){
                JOptionPane.showMessageDialog(view, "Respuesta nula recibida en la notificación de tarea movida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(!(resp.getData() instanceof Task)){
                JOptionPane.showMessageDialog(view, "Datos de tarea inválidos recibidos en la notificación de tarea movida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Task taskData = (Task) resp.getData();
            if (taskData == null) {
                JOptionPane.showMessageDialog(view, "Datos de tarea nulos recibidos en la notificación de tarea movida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(taskData.getIdSprint() != currentSprintId){
                // La tarea no pertenece al sprint actual, ignorar
                //JOptionPane.showMessageDialog(view, "La tarea movida no pertenece al sprint actual, ignorando notificación", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int taskId = taskData.getIdTarea();
            int newColumnId = taskData.getIdColumna();


            

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
    
    private void handleNewTask() {
        try {
            List<Column> columns = new ArrayList<>();

            Request req = new Request();
            req.setAction("getcolumnskanbanboard");
            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", currentProjectId);
            req.setPayload(payload);

            Response resp = connector.sendRequest(req);

            if (!resp.isSuccess() || resp.getData() == null) {
                JOptionPane.showMessageDialog(view,
                        "No se pudieron cargar las columnas disponibles",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            columns = (List<Column>) resp.getData();
            CreateTaskDialog dialog = new CreateTaskDialog(view, columns);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                Task newTask = dialog.getCreatedTask();

                // Completar los datos faltantes
                newTask.setIdProyecto(currentProjectId);
                newTask.setIdSprint(currentSprintId);
                newTask.setCreadoPor(this.connector.getUserID());

                // Enviar la solicitud al servidor
                Request createReq = new Request();
                createReq.setAction("createtask");

                Map<String, Object> taskPayload = new HashMap<>();
                taskPayload.put("idProyecto", newTask.getIdProyecto());
                taskPayload.put("idPrioridad", newTask.getIdPrioridad());
                taskPayload.put("idSprint", newTask.getIdSprint());
                taskPayload.put("idColumna", newTask.getIdColumna());
                taskPayload.put("idPrioridad", newTask.getIdPrioridad());
                taskPayload.put("titulo", newTask.getTitulo());
                taskPayload.put("descripcion", newTask.getDescripcion());
                taskPayload.put("creadoPor", newTask.getCreadoPor());

                if (newTask.getFechaVencimiento() != null) {
                    taskPayload.put("fechaVencimiento", newTask.getFechaVencimiento().toString());
                }

                createReq.setPayload(taskPayload);

                Response createResp = connector.sendRequest(createReq);

                if (createResp.isSuccess()) {
                    // Obtener la tarea creada con su ID del servidor
                    Task createdTaskFromServer = (Task) createResp.getData();

                    if (createdTaskFromServer != null) {
                        // Crear el panel visual de la tarea
                        System.out.println("Tarea creada con ID: " + createdTaskFromServer.getIdTarea() + " nombre"
                                + createdTaskFromServer.getNombrePrioridad());
                        KanbanTaskPanel taskPanel = new KanbanTaskPanel(createdTaskFromServer, view);

                        // Encontrar la columna destino y agregar la tarea
                        KanbanColumnPanel targetColumn = view.findColumnById(createdTaskFromServer.getIdColumna());

                        if (targetColumn != null) {
                            targetColumn.addTask(taskPanel);
                            view.revalidate();
                            view.repaint();

                            handleTaskMoved(createdTaskFromServer, targetColumn.getColumnData());
                        } else {
                            JOptionPane.showMessageDialog(view,
                                    "Error: Columna destino no encontrada para la nueva tarea",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(view,
                                "Error: El servidor no devolvió la tarea creada",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(view,
                            "Error al crear la tarea: " + createResp.getMessage(),
                            "Error del Servidor",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al crear la tarea: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void handleIncomingTaskCreatedNotification(Response resp) {
        try {
            // Obtener los datos de la nueva tarea desde la respuesta del servidor
            Task newTask = (Task) resp.getData();
            if (newTask == null) {
                JOptionPane.showMessageDialog(view,
                        "Error: No se recibieron datos de la tarea creada",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Verificar que la tarea pertenece al sprint actual
            if (newTask.getIdSprint() != currentSprintId) {
                System.out.println("La tarea pertenece a otro sprint, ignorando notificación");
                return;
            }

            // Verificar si la tarea ya existe en la vista (evitar duplicados)
            KanbanTaskPanel existingTaskPanel = view.findTaskPanelById(newTask.getIdTarea());
            if (existingTaskPanel != null) {
                System.out.println("La tarea ya existe en la vista, no se requiere agregar");
                return;
            }

            // Buscar la columna destino por ID
            KanbanColumnPanel targetColumn = view.findColumnById(newTask.getIdColumna());

            if (targetColumn == null) {
                JOptionPane.showMessageDialog(view,
                        "Error: Columna destino no encontrada para la nueva tarea",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear el panel visual de la tarea
            KanbanTaskPanel taskPanel = new KanbanTaskPanel(newTask, view);

            // Agregar la tarea a la columna destino
            targetColumn.addTask(taskPanel);

            // Refrescar la interfaz
            view.revalidate();
            view.repaint();

        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(view,
                    "Error al procesar los datos de la nueva tarea",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al procesar notificación de nueva tarea: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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