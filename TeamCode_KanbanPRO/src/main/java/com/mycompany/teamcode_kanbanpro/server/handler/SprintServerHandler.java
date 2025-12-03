/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.SprintDAO;
import com.mycompany.teamcode_kanbanpro.model.Sprint;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Handler del servidor para operaciones relacionadas con Sprints
 * 
 * Maneja las solicitudes del cliente para:
 * - Obtener sprints por proyecto
 * - Crear nuevos sprints
 * - (Futuro) Actualizar y eliminar sprints
 * 
 * @author Emanuel
 */
public class SprintServerHandler {
    
    private SprintDAO sprintDAO;
    
    public SprintServerHandler(SprintDAO sprintDAO) {
        this.sprintDAO = sprintDAO;
    }
    
    /**
     * Obtiene todos los sprints de un proyecto específico
     * 
     * @param projectID ID del proyecto
     * @return Response con la lista de sprints o mensaje de error
     */
    public Response handleGetSprintByProject(int projectID) {
        try {
            System.out.println("[SprintHandler] Obteniendo sprints para proyecto ID: " + projectID);
            
            List<Sprint> sprints = sprintDAO.selectSprintsByProjectId(projectID);
            
            if (sprints == null) {
                System.out.println("[SprintHandler] La consulta retornó null");
                Response r = new Response(true, "No se encontraron sprints.");
                r.setData(new java.util.ArrayList<Sprint>());
                return r;
            }
            
            System.out.println("[SprintHandler] Sprints encontrados: " + sprints.size());
            
            // Debug: imprimir detalles de cada sprint
            for (Sprint s : sprints) {
                System.out.println("  - Sprint ID: " + s.getIdSprint() + 
                                 ", Nombre: " + s.getNombre() + 
                                 ", Estado: " + s.getNombreEstado() +
                                 ", IdEstado: " + s.getIdEstado());
            }

            Response r = new Response(true, "Sprints cargados exitosamente.");
            r.setData(sprints);
            return r;

        } catch (Exception e) {
            System.err.println("[SprintHandler] Error al obtener sprints: " + e.getMessage());
            e.printStackTrace();
            return new Response(false, "Error interno al cargar sprints: " + e.getMessage());
        }
    }
    
    /**
     * Crea un nuevo sprint en el proyecto especificado
     * 
     * @param req Request con los datos del sprint
     * @return Response indicando éxito o error
     */
    public Response handleCreateSprint(Request req) {
        try {
            Map<String, Object> p = req.getPayload();
            
            System.out.println("[SprintHandler] Solicitud de creación de sprint recibida");
            
            // ========== 1. EXTRAER DATOS DEL PAYLOAD ==========
            Integer projectId = (Integer) p.get("projectId");
            String nombre = (String) p.get("nombre");
            String descripcion = (String) p.get("descripcion");
            String fechaInicioStr = (String) p.get("fechaInicio");
            String fechaFinStr = (String) p.get("fechaFin");
            
            System.out.println("[SprintHandler] Datos recibidos:");
            System.out.println("  - projectId: " + projectId);
            System.out.println("  - nombre: " + nombre);
            System.out.println("  - fechaInicio: " + fechaInicioStr);
            System.out.println("  - fechaFin: " + fechaFinStr);
            
            // ========== 2. VALIDACIONES ==========
            
            // Validar projectId
            if (projectId == null || projectId <= 0) {
                return new Response(false, "ID de proyecto inválido");
            }
            
            // Validar nombre
            if (nombre == null || nombre.trim().isEmpty()) {
                return new Response(false, "El nombre del sprint es obligatorio");
            }
            
            nombre = nombre.trim();
            
            if (nombre.length() < 3) {
                return new Response(false, "El nombre debe tener al menos 3 caracteres");
            }
            
            if (nombre.length() > 100) {
                return new Response(false, "El nombre no puede exceder 100 caracteres");
            }
            
            // Validar fechas
            if (fechaInicioStr == null || fechaInicioStr.trim().isEmpty()) {
                return new Response(false, "La fecha de inicio es obligatoria");
            }
            
            if (fechaFinStr == null || fechaFinStr.trim().isEmpty()) {
                return new Response(false, "La fecha de fin es obligatoria");
            }
            
            // ========== 3. CONVERTIR STRINGS A SQL DATE ==========
            Date sqlFechaInicio;
            Date sqlFechaFin;
            
            try {
                sqlFechaInicio = Date.valueOf(fechaInicioStr.trim());
                sqlFechaFin = Date.valueOf(fechaFinStr.trim());
            } catch (IllegalArgumentException e) {
                System.err.println("[SprintHandler] Error de formato de fecha: " + e.getMessage());
                return new Response(false, 
                    "Formato de fecha inválido. Use yyyy-MM-dd (ejemplo: 2024-12-31)");
            }
            
            // ========== 4. VALIDAR LÓGICA DE FECHAS ==========
            if (sqlFechaFin.before(sqlFechaInicio)) {
                return new Response(false, 
                    "La fecha de fin debe ser igual o posterior a la fecha de inicio");
            }
            
            // ========== 5. CREAR OBJETO SPRINT ==========
            Sprint newSprint = new Sprint();
            newSprint.setIdProyecto(projectId);
            newSprint.setIdEstado(1); // 1 = Planificado (estado inicial por defecto)
            newSprint.setNombre(nombre);
            newSprint.setDescripcion(descripcion != null ? descripcion.trim() : "");
            newSprint.setFechaInicio(sqlFechaInicio);
            newSprint.setFechaFin(sqlFechaFin);
            newSprint.setLimiteWip(null); // Opcional
            
            System.out.println("[SprintHandler] Insertando sprint en BD...");
            
            // ========== 6. INSERTAR EN BD ==========
            int sprintId = sprintDAO.insertSprint(newSprint);
            
            if (sprintId > 0) {
                System.out.println("[SprintHandler] Sprint creado con ID: " + sprintId);
                
                // Establecer el ID generado y el nombre del estado
                newSprint.setIdSprint(sprintId);
                newSprint.setNombreEstado("Planificado"); // Para mostrar en la UI
                
                Response r = new Response(true, "Sprint '" + nombre + "' creado exitosamente");
                r.setData(newSprint);
                return r;
            } else {
                System.err.println("[SprintHandler] Error: insertSprint retornó " + sprintId);
                return new Response(false, "Error al crear el sprint en la base de datos");
            }
            
        } catch (ClassCastException e) {
            System.err.println("[SprintHandler] Error de tipo de datos: " + e.getMessage());
            return new Response(false, "Datos de entrada inválidos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[SprintHandler] Error interno: " + e.getMessage());
            e.printStackTrace();
            return new Response(false, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el estado de un sprint
     * 
     * @param req Request con sprintId y nuevoEstadoId
     * @return Response indicando éxito o error
     */
    public Response handleUpdateSprintStatus(Request req) {
        try {
            Map<String, Object> p = req.getPayload();
            
            int sprintId = (int) p.get("sprintId");
            int nuevoEstadoId = (int) p.get("estadoId");
            
            // Obtener el sprint actual
            Sprint sprint = sprintDAO.selectSprintById(sprintId);
            
            if (sprint == null) {
                return new Response(false, "Sprint no encontrado");
            }
            
            // Actualizar el estado
            sprint.setIdEstado(nuevoEstadoId);
            boolean actualizado = sprintDAO.updateSprint(sprint);
            
            if (actualizado) {
                return new Response(true, "Estado del sprint actualizado");
            } else {
                return new Response(false, "No se pudo actualizar el sprint");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al actualizar sprint: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un sprint
     * 
     * @param sprintId ID del sprint a eliminar
     * @return Response indicando éxito o error
     */
    public Response handleDeleteSprint(int sprintId) {
        try {
            boolean eliminado = sprintDAO.deleteSprint(sprintId);
            
            if (eliminado) {
                return new Response(true, "Sprint eliminado exitosamente");
            } else {
                return new Response(false, "No se pudo eliminar el sprint. Verifique que existe.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al eliminar sprint: " + e.getMessage());
        }
    }
}






