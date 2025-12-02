/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.ProjectDAO;
import com.mycompany.teamcode_kanbanpro.dao.UserDAO;
import com.mycompany.teamcode_kanbanpro.model.Project;
import java.util.List;

/**
 *
 * @author Emanuel
 */
public class ProjectServerHandler {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;

    public ProjectServerHandler(ProjectDAO projectDAO, UserDAO userD) {
        this.projectDAO = projectDAO;
        this.userDAO = userD;
    }

    public Response handleGetProjectsByUser(int userId) {
        try {
            // validacion si el usaurio esta asociado a un grupo
            boolean userHasGroups = userDAO.isUserInAnyGroup(userId);
            if (!userHasGroups) {
                // Si el usuario no está en ningun grupo, devolvemos un mensaje de error 
                return new Response(false, "El usuario debe estar asociado a un grupo para ver proyectos.");
            }
    
            List<Project> projects = projectDAO.selectProjectsByUserId(userId);

            Response r = new Response(true, "Proyectos cargados exitosamente.");
            r.setData(projects);
            return r;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno al cargar proyectos: " + ex.getMessage());
        }
    }
}
