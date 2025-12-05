/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.ColumnDAO;
import com.mycompany.teamcode_kanbanpro.dao.GroupDAO;
import com.mycompany.teamcode_kanbanpro.dao.ProjectDAO;
import com.mycompany.teamcode_kanbanpro.dao.RoleDAO;
import com.mycompany.teamcode_kanbanpro.dao.SprintDAO;
import com.mycompany.teamcode_kanbanpro.dao.TaskDAO;
import com.mycompany.teamcode_kanbanpro.dao.UserDAO;
import com.mycompany.teamcode_kanbanpro.model.Role;
import com.mycompany.teamcode_kanbanpro.model.User;
import com.mycompany.teamcode_kanbanpro.server.handler.ColumnServerHandler;
//import com.mycompany.teamcode_kanbanpro.server.handler.GroupServerHandler;
import com.mycompany.teamcode_kanbanpro.server.handler.ProjectServerHandler;
import com.mycompany.teamcode_kanbanpro.server.handler.RoleServerHandler;
import com.mycompany.teamcode_kanbanpro.server.handler.SprintServerHandler;
import com.mycompany.teamcode_kanbanpro.server.handler.TaskServerHandler;
import com.mycompany.teamcode_kanbanpro.server.handler.UserServerHandler;
import com.mycompany.teamcode_kanbanpro.util.BCryptUtil;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * Manejador de conexiones de clientes
 * 
 * Procesa todas las solicitudes entrantes y las delega
 * al handler correspondiente según la acción solicitada.
 * 
 * @author Emanuel / TeamCode
 */
public class ClientHandler implements Runnable {
    
    private Socket socket;
    
    // DAOs
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final ProjectDAO projectDAO;
    private final SprintDAO sprintDAO;
    private final GroupDAO groupDAO;
    private final ColumnDAO columnDAO;
    private final TaskDAO taskDAO;
    
    // Handlers
    private final UserServerHandler userHandler;
    private final RoleServerHandler roleHandler;
    private final ProjectServerHandler projectHandler;
    private final SprintServerHandler sprintHandler;
    private final ColumnServerHandler columnHandler;
    private final TaskServerHandler taskHandler;
    //private final GroupServerHandler groupHandler;
    
    public ClientHandler(Socket socket) {
        this.socket = socket;
        
        // Inicializar DAOs
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
        this.projectDAO = new ProjectDAO();
        this.sprintDAO = new SprintDAO();
        this.groupDAO = new GroupDAO();
        this.columnDAO = new ColumnDAO();
        this.taskDAO = new TaskDAO();
        // Inicializar Handlers
        this.userHandler = new UserServerHandler(userDAO, roleDAO);
        this.roleHandler = new RoleServerHandler(roleDAO);
        this.projectHandler = new ProjectServerHandler(projectDAO, userDAO); //grupoDAO
        this.sprintHandler = new SprintServerHandler(sprintDAO);
        this.columnHandler = new ColumnServerHandler(columnDAO);
        this.taskHandler = new TaskServerHandler(taskDAO);
       // this.groupHandler = new GroupServerHandler(groupDAO);
    }

    @Override
    public void run() {
        String clientAddress = socket.getInetAddress().getHostAddress();
        System.out.println("[Server] Cliente conectado: " + clientAddress);
        
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Object obj;
            
            // Bucle para procesar múltiples requests del mismo cliente
            while ((obj = in.readObject()) != null) {
                if (!(obj instanceof Request)) {
                    System.err.println("[Server] Objeto recibido no es un Request");
                    continue;
                }
                
                Request req = (Request) obj;
                System.out.println("[Server] Request recibido: " + req.getAction());
                
                // Procesar y obtener respuesta
                Response resp = handleRequest(req);
                
                // Enviar respuesta al cliente
                out.writeObject(resp);
                out.flush();
                
                System.out.println("[Server] Response enviado: " + 
                                 (resp.isSuccess() ? "SUCCESS" : "ERROR"));
            }
            
        } catch (EOFException eof) {
            System.out.println("[Server] Cliente desconectado: " + clientAddress);
        } catch (Exception e) {
            System.err.println("[Server] Error con cliente " + clientAddress + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception ex) {
                System.err.println("[Server] Error al cerrar socket: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Procesa una solicitud y la delega al handler correspondiente
     */
    private Response handleRequest(Request req) {
        String action = req.getAction();
        
        if (action == null || action.isEmpty()) {
            return new Response(false, "Acción no especificada");
        }
        
        try {
            switch (action.toLowerCase()) {
                
                // ==================== AUTENTICACIÓN ====================
                case "login":
                    return handleLogin(req);
                    
                case "register":
                    return userHandler.handleRegister(req);
                
                // ==================== ROLES ====================
                case "getroles":
                    return roleHandler.handleGetRoles();
                /*
                // ==================== GRUPOS ====================
                case "getallgroups":
                    return groupHandler.handleGetAllGroups();
                /*    
                case "getgroupsbyproject":
                    return groupHandler.handleGetGroupsByProject(
                        (int) req.getPayload().get("projectId"));
                    */
                    /*
                case "getgroupsbyuser":
                    return groupHandler.handleGetGroupsByUser(
                        (int) req.getPayload().get("userId"));
                */
                // ==================== PROYECTOS ====================
                    
                case "getprojectsbyuser":
                    return projectHandler.handleGetProjectsByUser(
                        (int) req.getPayload().get("userId"));
                    
                case "createproject":
                    return projectHandler.handleCreateProject(req);
                    
                case "updateproject":
                    return projectHandler.handleUpdateProject(req);
                    
                case "deleteproject":
                    return projectHandler.handleDeleteProject(
                        (int) req.getPayload().get("projectId"));
                
                // ==================== SPRINTS ====================
                case "getsprintsbyproject":
                    return sprintHandler.handleGetSprintByProject(
                        (int) req.getPayload().get("projectId"));
                    
                case "createsprint":
                    return sprintHandler.handleCreateSprint(req);
                    
                case "updatesprintstatus":
                    return sprintHandler.handleUpdateSprintStatus(req);
                    
                case "deletesprint":
                    return sprintHandler.handleDeleteSprint(
                        (int) req.getPayload().get("sprintId"));
                
                // ==================== TAREAS (TODO: Implementar) ====================
                case "getcolumnskanbanboard":
                    return columnHandler.handleGetColumns((int) req.getPayload().get("projectId"));
                    
                case "gettasksbysprint":
                    return taskHandler.handleGeTasksBySprintId((int) req.getPayload().get("sprintId"));
                    
                case "createtask":
                    return new Response(false, "Funcionalidad de tareas pendiente de implementar");
                    
                case "updatetask":
                    return new Response(false, "Funcionalidad de tareas pendiente de implementar");
                    
                case "movetask":
                    return new Response(false, "Funcionalidad de tareas pendiente de implementar");
                
                // ==================== ACCIÓN NO RECONOCIDA ====================
                default:
                    System.err.println("[Server] Acción no soportada: " + action);
                    return new Response(false, "Acción no soportada: " + action);
            }
            
        } catch (ClassCastException e) {
            System.err.println("[Server] Error de tipo de datos en payload: " + e.getMessage());
            return new Response(false, "Datos de entrada inválidos");
        } catch (NullPointerException e) {
            System.err.println("[Server] Payload incompleto: " + e.getMessage());
            return new Response(false, "Faltan datos requeridos en la solicitud");
        } catch (Exception e) {
            System.err.println("[Server] Error procesando acción '" + action + "': " + e.getMessage());
            e.printStackTrace();
            return new Response(false, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    /**
     * Maneja el proceso de login
     */
    private Response handleLogin(Request req) {
        try {
            Map<String, Object> p = req.getPayload();

            String credencial = (String) p.get("usuario");
            // Mantener compatibilidad con el typo anterior
            if (credencial == null) {
                credencial = (String) p.get("usaurio"); // typo original
            }
            String clavePlain = (String) p.get("clave");
            
            System.out.println("[Server] Intento de login: " + credencial);
            
            if (credencial == null || credencial.isEmpty()) {
                return new Response(false, "El usuario es requerido");
            }
            
            if (clavePlain == null || clavePlain.isEmpty()) {
                return new Response(false, "La contraseña es requerida");
            }

            // Buscar usuario por username o email
            User user = userDAO.selectUserByUsernameOrEmail(credencial);

            if (user == null) {
                System.out.println("[Server] Usuario no encontrado: " + credencial);
                return new Response(false, "Usuario o contraseña incorrectos");
            }

            // Verificar si está activo
            if (!user.isActivo()) {
                System.out.println("[Server] Usuario inactivo: " + credencial);
                return new Response(false, "La cuenta está inactiva. Contacte al administrador.");
            }

            // Verificar contraseña
            boolean passwordOk = BCryptUtil.checkPwd(clavePlain, user.getPassword());
            
            if (!passwordOk) {
                System.out.println("[Server] Contraseña incorrecta para: " + credencial);
                return new Response(false, "Usuario o contraseña incorrectos");
            }
            
            // Login exitoso - preparar respuesta segura (sin password)
            System.out.println("[Server] Login exitoso: " + user.getNombre());
            
            User safeUser = new User();
            safeUser.setIdUsuario(user.getIdUsuario());
            safeUser.setIdRol(user.getIdRol());
            safeUser.setUsuario(user.getUsuario());
            safeUser.setNombre(user.getNombre());
            safeUser.setEmail(user.getEmail());
            safeUser.setActivo(user.isActivo());
            
            // Obtener nombre del rol
            Role rol = roleDAO.getRoleById(user.getIdRol());
            if (rol != null) {
                safeUser.setRolNombre(rol.getNombre());
            }
            
            Response r = new Response(true, "Login exitoso. Bienvenido, " + user.getNombre());
            r.setData(safeUser);
            return r;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno del servidor: " + ex.getMessage());
        }
    }
}