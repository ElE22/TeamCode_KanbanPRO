/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.ProjectDAO;
import com.mycompany.teamcode_kanbanpro.dao.ColumnDAO;
import com.mycompany.teamcode_kanbanpro.dao.GroupDAO;
import com.mycompany.teamcode_kanbanpro.dao.UserDAO;
import com.mycompany.teamcode_kanbanpro.model.Project;
import com.mycompany.teamcode_kanbanpro.model.Group;

import java.util.List;
import java.util.Map;

/**
 * @author Emanuel
 */
public class ProjectServerHandler {
    
    private final ProjectDAO projectDAO;
    private GroupDAO groupDAO;
    private final UserDAO userDAO;
    private ColumnDAO columnDAO;

    public ProjectServerHandler(ProjectDAO projectDAO, UserDAO userDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
    }
    
    public ProjectServerHandler(ProjectDAO projectDAO, UserDAO userDAO, GroupDAO groupDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.groupDAO = groupDAO;
    }

    
    //Obtiene los proyectos accesibles para un usuario. 
    public Response handleGetProjectsByUser(int userId) {
        try {
            System.out.println("[ProjectHandler] Obteniendo proyectos para usuario ID: " + userId);
            
            // Validar si el usuario está en algún grupo
            boolean userHasGroups = userDAO.isUserInAnyGroup(userId);
            
            if (!userHasGroups) {
                System.out.println("[ProjectHandler] Usuario no pertenece a ningún grupo");
                Response r = new Response(true, "Usuario sin grupos asignados.");
                r.setData(new java.util.ArrayList<Project>()); // Lista vacía
                return r;
            }
    
            // Obtener proyectos con información de grupos
            List<Project> projects = projectDAO.selectProjectsByUserIdWithGroups(userId);
            
            System.out.println("[ProjectHandler] Proyectos encontrados: " + 
                             (projects != null ? projects.size() : 0));

            Response r = new Response(true, "Proyectos cargados exitosamente.");
            r.setData(projects);
            return r;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno al cargar proyectos: " + ex.getMessage());
        }
    }
  
    //Crea un nuevo proyecto y lo asigna a un grupo.
    public Response handleCreateProject(Request req) {
        try {
            Map<String, Object> p = req.getPayload();
            
            System.out.println("[ProjectHandler] Solicitud de creación de proyecto recibida");
            
            // EXTRAER DATOS
            String nombre = (String) p.get("nombre");
            String descripcion = (String) p.get("descripcion");
            Integer creadorId = (Integer) p.get("creadorId");
            // === NUEVA INTEGRACIÓN: Obtener ID del grupo ===
            Integer grupoId = (Integer) p.get("grupoId");
            
            System.out.println("[ProjectHandler] Datos recibidos:");
            System.out.println("  - Nombre: " + nombre);
            System.out.println("  - Creador ID: " + creadorId);
            System.out.println("  - Grupo ID: " + grupoId);
            
            // VALIDACIONES
            if (nombre == null || nombre.trim().isEmpty()) {
                return new Response(false, "El nombre del proyecto es obligatorio");
            }
            
            nombre = nombre.trim();
            
            if (nombre.length() < 3) {
                return new Response(false, "El nombre debe tener al menos 3 caracteres");
            }
            
            if (nombre.length() > 100) {
                return new Response(false, "El nombre no puede exceder 100 caracteres");
            }
            
            if (creadorId == null || creadorId <= 0) {
                return new Response(false, "ID de creador inválido");
            }
            
            //Validar grupo obligatorio
            if (grupoId == null || grupoId <= 0) {
                return new Response(false, "Debe seleccionar un grupo para el proyecto");
            }
            
            //Verificar que el grupo existe
            Group grupo = groupDAO.selectGroupById(grupoId);
            if (grupo == null) {
                return new Response(false, "El grupo seleccionado no existe");
            }
            
            //Verificar que el usuario pertenece al grupo
            if (!groupDAO.isUserInGroup(creadorId, grupoId)) {
                return new Response(false, 
                    "No tienes permisos para crear proyectos en el grupo '" + grupo.getNombre() + "'.\n" +
                    "Debes ser miembro del grupo.");
            }
            
            // Verificar que el nombre no esté duplicado
            List<Project> existentes = projectDAO.selectAllProjects();
            for (Project proj : existentes) {
                if (proj.getNombre().equalsIgnoreCase(nombre)) {
                    return new Response(false, 
                        "Ya existe un proyecto con el nombre '" + nombre + "'");
                }
            }
            
            // CREAR PROYECTO 
            Project newProject = new Project();
            newProject.setIdUsuarioCreador(creadorId);
            newProject.setNombre(nombre);
            newProject.setDescripcion(descripcion != null ? descripcion.trim() : "");
            
            System.out.println("[ProjectHandler] Insertando proyecto en BD...");
            
            int projectId = projectDAO.insertProject(newProject);
            
            if (projectId <= 0) {
                return new Response(false, "Error al crear el proyecto en la base de datos");
            }
            
            System.out.println("[ProjectHandler] Proyecto creado con ID: " + projectId);
            
            //Asignar proyecto al grupo
            System.out.println("[ProjectHandler] Asignando proyecto al grupo " + grupoId + "...");
            
            boolean asignado = groupDAO.assignGroupToProject(projectId, grupoId);
            
            if (!asignado) {
                // Si falla la asignación, eliminar el proyecto creado
                System.err.println("[ProjectHandler] Error al asignar grupo, eliminando proyecto...");
                projectDAO.deleteProject(projectId);
                return new Response(false, 
                    "Error al asignar el proyecto al grupo. El proyecto no fue creado.");
            }
            
            System.out.println("[ProjectHandler] Proyecto asignado al grupo '" + grupo.getNombre() + "'");
            
            // Crear columnas Kanban por defecto
            crearColumnasKanbanPorDefecto(projectId);
            
            // PREPARAR RESPUESTA 
            newProject.setIdProyecto(projectId);
            newProject.setGruposPertenencia(grupo.getNombre());
            
            Response r = new Response(true, 
                "Proyecto '" + nombre + "' creado exitosamente en el grupo '" + grupo.getNombre() + "'");
            r.setData(newProject);
            
            System.out.println("[ProjectHandler] Proyecto creado exitosamente!");
            
            return r;
            
        } catch (ClassCastException e) {
            System.err.println("[ProjectHandler] Error de tipo de datos: " + e.getMessage());
            return new Response(false, "Datos de entrada inválidos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ProjectHandler] Error interno: " + e.getMessage());
            e.printStackTrace();
            return new Response(false, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    /**
     * Crea las columnas Kanban por defecto para un proyecto nuevo
     */
    private void crearColumnasKanbanPorDefecto(int projectId) {
        try {
            // Nota: Necesitarías un ColumnaKanbanDAO para esto
            // Por ahora solo logueamos
            String[][] columnasDefault = {
                {"Backlog", "1", "#9E9E9E"},
                {"To Do", "2", "#2196F3"},
                {"In Progress", "3", "#FF9800"},
                {"Done", "4", "#4CAF50"}
            };


            
            System.out.println("[ProjectHandler] Se deben crear columnas Kanban para proyecto " + projectId);
            System.out.println("  (Implementar ColumnaKanbanDAO para crear automáticamente)");
            
        } catch (Exception e) {
            System.err.println("[ProjectHandler] Error al crear columnas: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza un proyecto existente
     */
    public Response handleUpdateProject(Request req) {
        try {
            Map<String, Object> p = req.getPayload();
            
            int projectId = (int) p.get("projectId");
            String nombre = (String) p.get("nombre");
            String descripcion = (String) p.get("descripcion");
            
            if (nombre == null || nombre.trim().isEmpty()) {
                return new Response(false, "El nombre es obligatorio");
            }
            
            Project project = projectDAO.selectProjectById(projectId);
            if (project == null) {
                return new Response(false, "Proyecto no encontrado");
            }
            
            project.setNombre(nombre.trim());
            project.setDescripcion(descripcion != null ? descripcion.trim() : "");
            
            boolean actualizado = projectDAO.updateProject(project);
            
            if (actualizado) {
                return new Response(true, "Proyecto actualizado exitosamente");
            } else {
                return new Response(false, "No se pudo actualizar el proyecto");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al actualizar proyecto: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un proyecto
     */
    public Response handleDeleteProject(int projectId) {
        try {
            Project project = projectDAO.selectProjectById(projectId);
            if (project == null) {
                return new Response(false, "Proyecto no encontrado");
            }
            
            boolean eliminado = projectDAO.deleteProject(projectId);
            
            if (eliminado) {
                return new Response(true, "Proyecto eliminado exitosamente");
            } else {
                return new Response(false, "No se pudo eliminar el proyecto");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al eliminar proyecto: " + e.getMessage());
        }
    }

}