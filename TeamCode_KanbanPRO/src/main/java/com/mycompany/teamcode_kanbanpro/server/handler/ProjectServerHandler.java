/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.ProjectDAO;
import com.mycompany.teamcode_kanbanpro.dao.GroupDAO;
import com.mycompany.teamcode_kanbanpro.dao.UserDAO;
import com.mycompany.teamcode_kanbanpro.model.Project;
import com.mycompany.teamcode_kanbanpro.model.Group;

import java.util.List;
import java.util.Map;

/**
 * Handler del servidor para operaciones de Proyectos
 * 
 * Maneja:
 * - Obtener proyectos por usuario
 * - Crear nuevo proyecto
 * - Actualizar proyecto
 * - Eliminar proyecto
 * 
 * @author Emanuel
 */
public class ProjectServerHandler {
    
    private final ProjectDAO projectDAO;
   // private final GroupDAO groupDAO;
    private final UserDAO userDAO;

    public ProjectServerHandler(ProjectDAO projectDAO, UserDAO userDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        //this.groupDAO = new GroupDAO(); // Necesitamos acceso a grupos
    }
    /*
    // Constructor alternativo con GroupDAO
    public ProjectServerHandler(ProjectDAO projectDAO, UserDAO userDAO, GroupDAO groupDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        //this.groupDAO = groupDAO;
    }

    /**
     * Obtiene los proyectos a los que tiene acceso un usuario
     * (a través de sus grupos)
     */
    
    public Response handleGetProjectsByUser(int userId) {
        try {
            System.out.println("[ProjectHandler] Obteniendo proyectos para usuario ID: " + userId);
            
            /*
            // Validar si el usuario está en algún grupo
            boolean userHasGroups = userDAO.isUserInAnyGroup(userId);
            
            if (!userHasGroups) {
                System.out.println("[ProjectHandler] Usuario no pertenece a ningún grupo");
                return new Response(false, 
                    "El usuario no pertenece a ningún grupo.\n" +
                    "Contacte al administrador para ser asignado a un grupo.");
            }
    */
            List<Project> projects = projectDAO.selectProjectsByUserId(userId);
            
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
    
    /**
     * Crea un nuevo proyecto con sus grupos asignados
     */
    public Response handleCreateProject(Request req) {
        try {
            Map<String, Object> p = req.getPayload();
            
            System.out.println("[ProjectHandler] Solicitud de creación de proyecto recibida");
            
            // ========== 1. EXTRAER DATOS ==========
            String nombre = (String) p.get("nombre");
            String descripcion = (String) p.get("descripcion");
            Integer creadorId = (Integer) p.get("creadorId");
            
            //@SuppressWarnings("unchecked")
          //  List<Integer> gruposIds = (List<Integer>) p.get("gruposIds");
            
            System.out.println("[ProjectHandler] Datos recibidos:");
            System.out.println("  - Nombre: " + nombre);
            System.out.println("  - Creador ID: " + creadorId);
           // System.out.println("  - Grupos IDs: " + gruposIds);
            
            // ========== 2. VALIDACIONES ==========
            
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
            /*
            if (gruposIds == null || gruposIds.isEmpty()) {
                return new Response(false, "Debe asignar al menos un grupo al proyecto");
            }
            */
            // Verificar que el nombre no esté duplicado
            // (La BD tiene UNIQUE pero es mejor validar antes)
            List<Project> existentes = projectDAO.selectAllProjects();
            for (Project proj : existentes) {
                if (proj.getNombre().equalsIgnoreCase(nombre)) {
                    return new Response(false, 
                        "Ya existe un proyecto con el nombre '" + nombre + "'");
                }
            }
            
            // ========== 3. CREAR PROYECTO ==========
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
            /*
            // ========== 4. ASIGNAR GRUPOS AL PROYECTO ==========
            System.out.println("[ProjectHandler] Asignando grupos al proyecto...");
            
            int gruposAsignados = 0;
            for (Integer grupoId : gruposIds) {
                boolean asignado = groupDAO.assignGroupToProject(projectId, grupoId);
                if (asignado) {
                    gruposAsignados++;
                    System.out.println("  - Grupo " + grupoId + " asignado correctamente");
                } else {
                    System.err.println("  - Error al asignar grupo " + grupoId);
                }
            }
            
            if (gruposAsignados == 0) {
                // Si no se pudo asignar ningún grupo, eliminar el proyecto creado
                projectDAO.deleteProject(projectId);
                return new Response(false, 
                    "Error al asignar grupos al proyecto. El proyecto no fue creado.");
            }
            */
            // ========== 5. CREAR COLUMNAS KANBAN POR DEFECTO ==========
            crearColumnasKanbanPorDefecto(projectId);
            
            // ========== 6. PREPARAR RESPUESTA ==========
            newProject.setIdProyecto(projectId);
            
            Response r = new Response(true, 
                "Proyecto '" + nombre + "' creado exitosamente con ");
            //+  gruposAsignados + " grupo(s) asignado(s)");
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
            // Usar ColumnDAO si existe, o ejecutar SQL directo
            // Por ahora, las columnas se crean con SQL directo a través del DAO
            
            String[][] columnasDefault = {
                {"Backlog", "1", "#9E9E9E"},
                {"To Do", "2", "#2196F3"},
                {"In Progress", "3", "#FF9800"},
                {"Done", "4", "#4CAF50"}
            };
            
            // Nota: Necesitarías un ColumnaKanbanDAO para esto
            // Por ahora solo logueamos que se necesitan crear
            System.out.println("[ProjectHandler] Se deben crear columnas Kanban para proyecto " + projectId);
            System.out.println("  (Implementar ColumnaKanbanDAO para crear automáticamente)");
            
        } catch (Exception e) {
            System.err.println("[ProjectHandler] Error al crear columnas: " + e.getMessage());
            // No es crítico, el proyecto ya fue creado
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
            
            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                return new Response(false, "El nombre es obligatorio");
            }
            
            // Obtener proyecto existente
            Project project = projectDAO.selectProjectById(projectId);
            if (project == null) {
                return new Response(false, "Proyecto no encontrado");
            }
            
            // Actualizar campos
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
            // Verificar que existe
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