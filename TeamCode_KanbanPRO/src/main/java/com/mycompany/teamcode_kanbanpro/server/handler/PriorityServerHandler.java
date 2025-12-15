/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.PriorityDAO;
import com.mycompany.teamcode_kanbanpro.model.Priority;

/**
 *
 * @author escobe11
 */
public class PriorityServerHandler {

    private final PriorityDAO priorityDAO;

    public PriorityServerHandler( PriorityDAO priorityDAO) {
        this.priorityDAO = priorityDAO;
    }

    public Response handleGetPriorityByName(Request req) {
        try {
            String nombrePrioridad = (String) req.getPayload().get("nombrePrioridad");
            Priority priority = priorityDAO.selectPriorityByName(nombrePrioridad);
            Response r = new Response(true, "Prioridad cargada exitosamente");
            r.setData(priority);
            return r;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno al cargar prioridad: " + ex.getMessage());
        }
    }
    
}
