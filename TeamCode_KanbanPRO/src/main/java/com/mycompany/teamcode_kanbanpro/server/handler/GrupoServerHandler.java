/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.GroupDAO;
import com.mycompany.teamcode_kanbanpro.dao.UserDAO; 
import com.mycompany.teamcode_kanbanpro.model.Group;
import com.mycompany.teamcode_kanbanpro.model.User; 
import java.util.List;
import java.util.Map;
/**
 *
 * @author salaz
 */
public class GrupoServerHandler {

    private final GroupDAO groupDAO;
    private final UserDAO userDAO;


    public GrupoServerHandler(GroupDAO groupDAO, UserDAO userDAO) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
    }

  
    public GrupoServerHandler(GroupDAO groupDAO) {
        this.groupDAO = groupDAO;
        this.userDAO = new UserDAO();
    }

    public Response handleGetAllGroups() {
        try {
            List<Group> group = groupDAO.selectAllGroups();
            Response response = new Response(true, "Grupos obtenidos exitosamente.");
            response.setData(group);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al obtener grupos: " + e.getMessage());
        }
    }

    public Response handleGetGroupsByUser(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();

            if (payload == null || !payload.containsKey("idUsuario")) {
                return new Response(false, "Falta el ID del usuario.");
            }

            int idUsuario = ((Number) payload.get("idUsuario")).intValue();

            List<Group> groups = groupDAO.selectGroupsByUserId(idUsuario);

            Response response = new Response(true, "Grupos del usuario obtenidos.");
            response.setData(groups);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al obtener grupos del usuario: " + e.getMessage());
        }
    }

    
    public Response handleCreateGroup(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();
            
            if (payload == null) {
                return new Response(false, "No se recibieron datos.");
            }
            
            String nombre = (String) payload.get("nombre");
            String descripcion = (String) payload.get("descripcion");

            if (nombre == null || nombre.trim().isEmpty()) {
                return new Response(false, "El nombre del grupo es obligatorio.");
            }
            
            //Validación de longitud mínima 
            if (nombre.trim().length() < 3) {
                return new Response(false, "El nombre debe tener al menos 3 caracteres.");
            }
            
            if (groupDAO.selectGroupByName(nombre.trim()) != null) {
                return new Response(false, "Ya existe un grupo con ese nombre.");
            }

            Group newGroup = new Group();
            newGroup.setNombre(nombre.trim());
            newGroup.setDescripcion(descripcion != null ? descripcion.trim() : "");

            int generatedId = groupDAO.insertGroup(newGroup);

            if (generatedId > 0) {
                Response response = new Response(true, "Grupo '" + nombre + "' creado exitosamente.");
                response.setData(newGroup);
                return response;
            } else {
                return new Response(false, "Error al crear el grupo en la base de datos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error interno al crear grupo: " + e.getMessage());
        }
    }

    
     //Obtiene los usuarios que pertenecen a un grupo específico     
    public Response handleGetUsersByGroup(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();

            if (payload == null || !payload.containsKey("idGrupo")) {
                return new Response(false, "Falta el ID del grupo.");
            }

            int idGrupo = ((Number) payload.get("idGrupo")).intValue();

            // Verificar que el grupo existe
            Group group = groupDAO.selectGroupById(idGrupo);
            if (group == null) {
                return new Response(false, "El grupo no existe.");
            }

            // Obtener los miembros del grupo
            List<User> miembros = groupDAO.selectUsersByGroupId(idGrupo);

            Response response = new Response(true, "Miembros del grupo obtenidos.");
            response.setData(miembros);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al obtener miembros del grupo: " + e.getMessage());
        }
    }

    // Validaciones adicionales añadidas 
    public Response handleJoinGroup(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();

            if (payload == null || !payload.containsKey("idUsuario") || !payload.containsKey("idGrupo")) {
                return new Response(false, "Faltan datos: idUsuario o idGrupo.");
            }

            int idUsuario = ((Number) payload.get("idUsuario")).intValue();
            int idGrupo = ((Number) payload.get("idGrupo")).intValue();

            // Verificar si el grupo existe
            Group group = groupDAO.selectGroupById(idGrupo);
            if (group == null) {
                return new Response(false, "El grupo seleccionado no existe.");
            }

            //Verificar si el usuario existe 
            User user = userDAO.selectUserById(idUsuario);
            if (user == null) {
                return new Response(false, "El usuario no existe.");
            }

            // Asignar usuario al grupo
            boolean success = groupDAO.addUserToGroup(idUsuario, idGrupo);

            if (success) {
                Response response = new Response(true, "Usuario agregado al grupo '" + group.getNombre() + "'.");
                response.setData(group);
                return response;
            } else {
                //Mensaje
                return new Response(false, "Error al agregar usuario al grupo. Es posible que ya sea miembro.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error interno al unirse al grupo: " + e.getMessage());
        }
    }

    public Response handleLeaveGroup(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();

            if (payload == null || !payload.containsKey("idUsuario") || !payload.containsKey("idGrupo")) {
                return new Response(false, "Faltan datos: idUsuario o idGrupo.");
            }

            int idUsuario = ((Number) payload.get("idUsuario")).intValue();
            int idGrupo = ((Number) payload.get("idGrupo")).intValue();

            // Remover usuario del grupo
            boolean success = groupDAO.removeUserFromGroup(idUsuario, idGrupo);

            if (success) {
                return new Response(true, "Usuario removido del grupo exitosamente.");
            } else {
                return new Response(false, "Error al remover usuario del grupo.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error interno al salir del grupo: " + e.getMessage());
        }
    }

    public Response handleGetGroupById(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();

            if (payload == null || !payload.containsKey("idGrupo")) {
                return new Response(false, "Falta el ID del grupo.");
            }

            int idGrupo = ((Number) payload.get("idGrupo")).intValue();

            Group group = groupDAO.selectGroupById(idGrupo);

            if (group != null) {
                Response response = new Response(true, "Grupo encontrado.");
                response.setData(group);
                return response;
            } else {
                return new Response(false, "Grupo no encontrado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al buscar grupo: " + e.getMessage());
        }
    }

    public Response handleGetGroupsByProject(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();

            if (payload == null || !payload.containsKey("idProyecto")) {
                return new Response(false, "Falta el ID del proyecto.");
            }

            int idProyecto = ((Number) payload.get("idProyecto")).intValue();

            List<Group> groups = groupDAO.selectGroupsByProjectId(idProyecto);

            Response response = new Response(true, "Grupos del proyecto obtenidos.");
            response.setData(groups);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al obtener grupos del proyecto: " + e.getMessage());
        }
    }

   
     // Obtiene todos los usuarios del sistema
    public Response handleGetAllUsers() {
        try {
            List<User> users = userDAO.selectAllUsers();

            // Limpiar passwords por seguridad
            for (User u : users) {
                u.setPassword(null);
            }

            Response response = new Response(true, "Usuarios obtenidos exitosamente.");
            response.setData(users);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al obtener usuarios: " + e.getMessage());
        }
    }
}

