package com.mycompany.teamcode_kanbanpro.server;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.*;
import com.mycompany.teamcode_kanbanpro.model.Role;
import com.mycompany.teamcode_kanbanpro.model.User;
import com.mycompany.teamcode_kanbanpro.server.handler.*;
import com.mycompany.teamcode_kanbanpro.util.BCryptUtil;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Emanuel
 */
public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    private final Server server; 
    private ObjectOutputStream out; 
    private final Socket socket;
    
    // handlers especializados por dominio
    private final UserServerHandler userHandler;
    private final RoleServerHandler roleHandler;
    private final ProjectServerHandler projectHandler;
    private final SprintServerHandler sprintHandler;
    private final ColumnServerHandler columnHandler;
    private final TaskServerHandler taskHandler;
    private final PriorityServerHandler priorityHandler;
    private final GrupoServerHandler grupoHandler;
    

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        // inicializar daos
        UserDAO userDAO = new UserDAO();
        RoleDAO roleDAO = new RoleDAO();
        ProjectDAO projectDAO = new ProjectDAO();
        SprintDAO sprintDAO = new SprintDAO();
        ColumnDAO columnDAO = new ColumnDAO();
        TaskDAO taskDAO = new TaskDAO();
        PriorityDAO priorityDAO = new PriorityDAO();
        GroupDAO groupDAO = new GroupDAO();
        
        // inicializar handlers
        this.userHandler = new UserServerHandler(userDAO, roleDAO);
        this.roleHandler = new RoleServerHandler(roleDAO);
        this.projectHandler = new ProjectServerHandler(projectDAO, userDAO);
        this.sprintHandler = new SprintServerHandler(sprintDAO);
        this.columnHandler = new ColumnServerHandler(columnDAO);
        this.taskHandler = new TaskServerHandler(taskDAO, priorityDAO, sprintDAO);
        this.priorityHandler = new PriorityServerHandler(priorityDAO);
        this.grupoHandler = new GrupoServerHandler(groupDAO, userDAO);
    }

    @Override
    public void run() {
        String clientAddress = socket.getInetAddress().getHostAddress();
        LOGGER.info("cliente conectado: " + clientAddress);

        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();

            processClientRequests(in, this.out);

        } catch (EOFException e) {
            LOGGER.info("cliente desconectado normalmente: " + clientAddress);
        } catch (java.net.SocketException e) {
            // conexion interrumpida abruptamente (cliente cerro la app, red caida, etc)
            if (isConnectionReset(e)) {
                LOGGER.info("cliente desconectado abruptamente: " + clientAddress);
            } else {
                LOGGER.log(Level.WARNING, "error de socket con cliente " + clientAddress, e);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "error de io con cliente " + clientAddress, e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "error inesperado con cliente " + clientAddress, e);
        } finally {
            closeSocket();
            server.deregisterHandler(this);
        }
    }

    // verifica si es un error de conexion reset comun
    private boolean isConnectionReset(SocketException e) {
        String msg = e.getMessage();
        return msg != null && (
            msg.contains("Connection reset") ||
            msg.contains("Broken pipe") ||
            msg.contains("Connection closed")
        );
    }

    // procesa requests del cliente en bucle
    private void processClientRequests(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        Object obj;
        
        while ((obj = in.readObject()) != null) {
            if (!(obj instanceof Request)) {
                LOGGER.warning("objeto recibido no es un request");
                continue;
            }

            Request req = (Request) obj;
            LOGGER.fine("request recibido: " + req.getAction());

            Response resp = handleRequest(req);
            resp.setRequestId(req.getRequestId());
            out.writeObject(resp);
            out.flush();

            LOGGER.fine("response enviado: " + (resp.isSuccess() ? "success" : "error"));
        }
    }

    public synchronized void sendResponse(Response resp) {
        try {
            if (this.out != null) {
                LOGGER.fine("Desde sendUpdate a " + socket.getInetAddress() + " — resp.isBroadcast="
                        + resp.isBroadcast()
                        + " — dataClass=" + (resp.getData() != null ? resp.getData().getClass().getName() : "null"));
                out.writeObject(resp);
                out.flush();
                LOGGER.fine("broadcast enviado a " + socket.getInetAddress());
            } else {
                LOGGER.warning("out es null al intentar enviar broadcast a " + socket.getInetAddress());
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al enviar broadcast a cliente: " + socket.getInetAddress(), e);
        }
    }

    // router principal de requests
    private Response handleRequest(Request req) {
        String action = req.getAction();

        if (action == null || action.isEmpty()) {
            return new Response(false, "accion no especificada");
        }

        try {
            return routeAction(action.toLowerCase(), req);
            
        } catch (ClassCastException e) {
            LOGGER.log(Level.WARNING, "error de tipo en payload", e);
            return new Response(false, "datos de entrada invalidos");
            
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARNING, "payload incompleto", e);
            return new Response(false, "faltan datos requeridos");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "error procesando accion: " + action, e);
            return new Response(false, "error interno del servidor");
        }
    }

    // delega la accion al handler correspondiente
    private Response routeAction(String action, Request req) {
        Map<String, Object> payload = req.getPayload();
        
        switch (action) {
            // auth
            case "login":
                return handleLogin(req);
            case "register":
                return userHandler.handleRegister(req);

            // roles
            case "getroles":
                return roleHandler.handleGetRoles();
                
            // GRUPOS 
            case "getallgroups":
                return grupoHandler.handleGetAllGroups();
                
            case "getgroupsbyuser":
                return grupoHandler.handleGetGroupsByUser(req);
                
            case "creategroup":
                return grupoHandler.handleCreateGroup(req);
                
            case "getusersbygroup":
                return grupoHandler.handleGetUsersByGroup(req);
                
            case "getallusers":
                return grupoHandler.handleGetAllUsers();
                
            case "joingroup":
                return grupoHandler.handleJoinGroup(req);
                
            case "leavegroup":
                return grupoHandler.handleLeaveGroup(req);
                
            case "getgroupbyid":
                return grupoHandler.handleGetGroupById(req);
                
            case "getgroupsbyproject":
                return grupoHandler.handleGetGroupsByProject(req);
                
            case "checkuserhasgroup":
                return handleCheckUserHasGroup(req);


            // projects
            case "getprojectsbyuser":
                return projectHandler.handleGetProjectsByUser(getIntParam(payload, "userId"));
            case "createproject":
                return projectHandler.handleCreateProject(req);
            case "updateproject":
                return projectHandler.handleUpdateProject(req);
            case "deleteproject":
                return projectHandler.handleDeleteProject(getIntParam(payload, "projectId"));

            // sprints
            case "getsprintsbyproject":
                return sprintHandler.handleGetSprintByProject(getIntParam(payload, "projectId"));
            case "createsprint":
                return sprintHandler.handleCreateSprint(req);
            case "updatesprintstatus":
                return sprintHandler.handleUpdateSprintStatus(req);
            case "deletesprint":
                return sprintHandler.handleDeleteSprint(getIntParam(payload, "sprintId"));

            // columns
            case "getcolumnskanbanboard":
                return columnHandler.handleGetColumns(getIntParam(payload, "projectId"));

            // tasks
            case "gettasksbysprint":
                return taskHandler.handleGeTasksBySprintId(getIntParam(payload, "sprintId"));
            case "createtask":
                Response res = taskHandler.handleCreateTask(req);
                // Si fue exitoso, notifica a todos los clientes (broadcast)
                if (res.isSuccess()) {
                    Response notification = new Response(true, "tarea movida por otro usuario");
                    notification.setBroadcast(true);
                    notification.setData(res.getData());
                    notification.setAction("taskcreated");
                    try {
                        server.broadcastUpdate(notification, this); // Excluye este cliente del broadcast
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "error al hacer broadcast de movimiento de tarea", e);
                    }
                }
                return res;
            case "updatetask":
                return new Response(false, "funcionalidad pendiente de implementar");
            case "movetask": 
              Response resp = taskHandler.handleMoveTask(req);
                
                // Si fue exitoso, notifica a todos los clientes (broadcast)
                if (resp.isSuccess()) {
                    Response notification = new Response(true, "tarea movida por otro usuario");
                    notification.setBroadcast(true);
                    notification.setData(resp.getData()); 
                    notification.setAction("taskmoved");
                    
                    try {
                        server.broadcastUpdate(notification, this); // Excluye este cliente del broadcast
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "error al hacer broadcast de movimiento de tarea", e);
                    }
                }
                return resp;
            //prioridades
            case "getpriorityidbyname":
                return priorityHandler.handleGetPriorityByName(req);

            default:
                LOGGER.warning("accion no soportada: " + action);
                return new Response(false, "accion no soportada: " + action);
        }
    }
    
    // Verifica si usuario tiene grupos
    private Response handleCheckUserHasGroup(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();

            if (payload == null || !payload.containsKey("idUsuario")) {
                return new Response(false, "Falta el ID del usuario.");
            }

            int idUsuario = ((Number) payload.get("idUsuario")).intValue();

            UserDAO userDAO = new UserDAO();
            boolean hasGroup = userDAO.isUserInAnyGroup(idUsuario);

            Response r = new Response(true, hasGroup ? "Usuario tiene grupos" : "Usuario sin grupos");
            r.setData(hasGroup);
            return r;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "error verificando grupos de usuario", e);
            return new Response(false, "Error al verificar grupos: " + e.getMessage());
        }
    }

    // maneja autenticacion de usuarios
    private Response handleLogin(Request req) {
        try {
            Map<String, Object> payload = req.getPayload();

            String credencial = getStringParam(payload, "usuario", "usaurio");
            String clavePlain = getStringParam(payload, "clave");

            // validar input
            if (credencial == null || credencial.trim().isEmpty()) {
                return new Response(false, "el usuario es requerido");
            }
            if (clavePlain == null || clavePlain.isEmpty()) {
                return new Response(false, "la contraseña es requerida");
            }

            LOGGER.info("intento de login: " + credencial);

            // buscar usuario
            UserDAO userDAO = new UserDAO();
            User user = userDAO.selectUserByUsernameOrEmail(credencial);

            if (user == null) {
                LOGGER.info("usuario no encontrado: " + credencial);
                return new Response(false, "usuario o contraseña incorrectos");
            }

            // verificar estado activo
            if (!user.isActivo()) {
                LOGGER.info("usuario inactivo: " + credencial);
                return new Response(false, "la cuenta esta inactiva");
            }

            // verificar contraseña
            if (!BCryptUtil.checkPwd(clavePlain, user.getPassword())) {
                LOGGER.info("contraseña incorrecta para: " + credencial);
                return new Response(false, "usuario o contraseña incorrectos");
            }

            // login exitoso
            LOGGER.info("login exitoso: " + user.getNombre());
            return createLoginResponse(user);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "error en login", e);
            return new Response(false, "error interno del servidor");
        }
    }

    // crea respuesta de login con datos seguros (sin password)
    private Response createLoginResponse(User user) {
        User safeUser = new User();
        safeUser.setIdUsuario(user.getIdUsuario());
        safeUser.setIdRol(user.getIdRol());
        safeUser.setUsuario(user.getUsuario());
        safeUser.setNombre(user.getNombre());
        safeUser.setEmail(user.getEmail());
        safeUser.setActivo(user.isActivo());

        // obtener nombre del rol
        RoleDAO roleDAO = new RoleDAO();
        Role rol = roleDAO.getRoleById(user.getIdRol());
        if (rol != null) {
            safeUser.setRolNombre(rol.getNombre());
        }

        Response response = new Response(true, "login exitoso. bienvenido, " + user.getNombre());
        response.setData(safeUser);
        return response;
    }

    // helpers para extraer parametros de forma segura
    private int getIntParam(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        throw new IllegalArgumentException("parametro requerido: " + key);
    }

    private String getStringParam(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Object value = payload.get(key);
            if (value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }

    // cierra el socket de forma segura
    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "error al cerrar socket", e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

}