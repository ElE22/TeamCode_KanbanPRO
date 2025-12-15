/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.PriorityDAO;
import com.mycompany.teamcode_kanbanpro.dao.TaskDAO;
import com.mycompany.teamcode_kanbanpro.model.Priority;
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

    public TaskServerHandler(TaskDAO taskDAO, PriorityDAO priorityDAO) {
        this.taskDAO = taskDAO;
        this.priorityDAO = priorityDAO;

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

            boolean success = taskDAO.updateTaskColumn(taskId, newColumnId);

            if (success) {
                return new Response(true, "Tarea movida exitosamente.");
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
            Priority p = priorityDAO.selectPriorityById((Integer) payload.get("idPrioridad"));
            Task newTask = new Task();
            newTask.setIdPrioridad(p.getIdPrioridad());
            newTask.setNombrePrioridad(p.getNombre());
            newTask.setIdColumna((Integer) payload.get("idColumna"));
            newTask.setIdProyecto((Integer) payload.get("idProyecto"));
            newTask.setIdSprint((Integer) payload.get("idSprint"));
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
        } catch (Exception e) {
            // Loguear el error interno
            System.err.println("Error interno al crear tarea: " + e.getMessage());
            return new Response(false, "Error interno del servidor al crear tarea.");
        }
    }
}
