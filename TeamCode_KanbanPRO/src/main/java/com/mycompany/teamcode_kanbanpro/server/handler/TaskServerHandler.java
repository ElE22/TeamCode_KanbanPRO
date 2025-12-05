/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.TaskDAO;
import com.mycompany.teamcode_kanbanpro.model.Task;
import java.util.List;

/**
 *
 * @author Emanuel
 */
public class TaskServerHandler {
    TaskDAO taskDAO;

    public TaskServerHandler(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
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
}
