package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.PriorityDAO;
import com.mycompany.teamcode_kanbanpro.dao.SprintDAO;
import com.mycompany.teamcode_kanbanpro.dao.TaskDAO;
import com.mycompany.teamcode_kanbanpro.model.Priority;
import com.mycompany.teamcode_kanbanpro.model.Sprint;
import com.mycompany.teamcode_kanbanpro.model.Task;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Emanuel
 */
public class TaskServerHandler {
    TaskDAO taskDAO;
    PriorityDAO priorityDAO;
    SprintDAO sprintDAO;

    public TaskServerHandler(TaskDAO taskDAO, PriorityDAO priorityDAO, SprintDAO sprintDAO) {
        this.taskDAO = taskDAO;
        this.priorityDAO = priorityDAO;
        this.sprintDAO = sprintDAO;
    }
    
    public Response handleGeTasksBySprintId(int sprintId) {
        try {
            List<Task> columns = taskDAO.selectTasksBySprintId(sprintId);
            Response r = new Response(true, "tareas cargadas exitosamente");
            r.setData(columns);
            return r;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno al cargar tareas: " + ex.getMessage());
        }
    }

    public Response handleMoveTask(Request req) {
        Map<String, Object> payload = req.getPayload();
        if (!payload.containsKey("idTarea") || !payload.containsKey("idColumna")) {
            return new Response(false, "Faltan parámetros idTarea o idColumna.");
        }
        try {
            int taskId = (Integer) payload.get("idTarea");
            int newColumnId = (Integer) payload.get("idColumna");

            Task success = taskDAO.updateTaskColumn(taskId, newColumnId);

            if (success != null) {
                Response r = new Response(true, "Tarea movida exitosamente.");
                r.setData(success);
                return r;

            } else {
                return new Response(false, "No se pudo actualizar la columna de la tarea (ID no encontrado o error en DB).");
            }

        } catch (ClassCastException e) {
            return new Response(false, "El tipo de dato de idTarea o idColumna es incorrecto.");
        } catch (Exception e) {
            // Loguear el error interno
            System.err.println("Error interno al mover tarea: " + e.getMessage());
            return new Response(false, "Error interno del servidor al mover tarea.");
        }
    }


    public Response handleCreateTask(Request req) {
        Map<String, Object> payload = req.getPayload();
        try {
            // Validar que exista el sprint
            Integer sprintId = (Integer) payload.get("idSprint");
            if (sprintId == null) {
                return new Response(false, "El ID del sprint es requerido.");
            }
            
            Sprint sprint = sprintDAO.selectSprintById(sprintId);
            if (sprint == null) {
                return new Response(false, "El sprint especificado no existe.");
            }
            
            // Validar fecha de vencimiento si existe
            if (payload.containsKey("fechaVencimiento")) {
                Date fechaVencimiento = Date.valueOf((String) payload.get("fechaVencimiento"));
                Date fechaFinSprint = sprint.getFechaFin();
                
                if (fechaFinSprint != null && fechaVencimiento.after(fechaFinSprint)) {
                    return new Response(false, 
                        "La fecha de vencimiento de la tarea (" + fechaVencimiento + 
                        ") no puede ser posterior a la fecha de fin del sprint (" + fechaFinSprint + ").");
                }
                
                // Tambien validar que la fecha de vencimiento no sea anterior a la fecha de inicio del sprint
                Date fechaInicioSprint = sprint.getFechaInicio();
                if (fechaInicioSprint != null && fechaVencimiento.before(fechaInicioSprint)) {
                    return new Response(false, 
                        "La fecha de vencimiento de la tarea (" + fechaVencimiento + 
                        ") no puede ser anterior a la fecha de inicio del sprint (" + fechaInicioSprint + ").");
                }
            }
            
            // Validar y obtener la prioridad
            Priority p = priorityDAO.selectPriorityById((Integer) payload.get("idPrioridad"));
            if (p == null) {
                return new Response(false, "La prioridad especificada no existe.");
            }
            
            // Crear la tarea
            Task newTask = new Task();
            newTask.setIdPrioridad(p.getIdPrioridad());
            newTask.setNombrePrioridad(p.getNombre());
            newTask.setIdColumna((Integer) payload.get("idColumna"));
            newTask.setIdProyecto((Integer) payload.get("idProyecto"));
            newTask.setIdSprint(sprintId);
            newTask.setTitulo((String) payload.get("titulo"));
            newTask.setDescripcion((String) payload.get("descripcion"));
            newTask.setCreadoPor((Integer) payload.get("creadoPor"));
            
            if (payload.containsKey("fechaVencimiento")) {
                newTask.setFechaVencimiento(Date.valueOf((String) payload.get("fechaVencimiento")));
            }

            int generatedId = taskDAO.insertTask(newTask);
            if (generatedId != -1) {
                Task createdTask = taskDAO.selectTaskWithGroupsById(generatedId);
                Response r = new Response(true, "Tarea creada exitosamente.");
                r.setData(createdTask);
                return r;
            } else {
                return new Response(false, "No se pudo crear la tarea.");
            }
        } catch (IllegalArgumentException e) {
            return new Response(false, "Formato de fecha inválido. Use el formato YYYY-MM-DD.");
        } catch (ClassCastException e) {
            return new Response(false, "El tipo de dato de algún parámetro es incorrecto.");
        } catch (Exception e) {
            // Loguear el error interno
            e.printStackTrace();
            System.err.println("Error interno al crear tarea: " + e.getMessage());
            return new Response(false, "Error interno del servidor al crear tarea.");
        }
    }
}