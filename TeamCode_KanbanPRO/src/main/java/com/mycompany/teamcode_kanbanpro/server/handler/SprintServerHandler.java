/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.SprintDAO;
import com.mycompany.teamcode_kanbanpro.model.Sprint;
import java.util.List;
/**
 *
 * @author Emanuel
 */
public class SprintServerHandler {
    
    private SprintDAO sprintDAO;
    
    public SprintServerHandler (SprintDAO spDAO) {
        this.sprintDAO = spDAO;
    }
    
    public Response handleGetSprintByProject(int projectID){
        try {
            
            List<Sprint> sprints = this.sprintDAO.selectSprintsByProjectId(projectID);

            Response r = new Response(true, "Sprints cargados exitosamente.");
            r.setData(sprints); 
            return r;

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error interno al cargar sprints: " + e.getMessage());
        }
        
    }
    
}
