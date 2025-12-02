/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.ProjectDAO;
import com.mycompany.teamcode_kanbanpro.model.Project;
import java.util.List;

/**
 *
 * @author Emanuel
 */
public class ProjectServerHandler {
    private final ProjectDAO projectDAO;

    public ProjectServerHandler(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public Response handleGetProjectsByUser(int userId) {
        try {
            List<Project> projects = this.projectDAO.selectProjectsByUserId(userId);
            Response r = new Response(true, "Proyectos obtenidos exitosamente.");
            r.setData(projects); 
            return r;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno del servidor al obtener proyectos: " + ex.getMessage());
        }
    }
}
