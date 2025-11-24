/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response; 
import com.mycompany.teamcode_kanbanpro.dao.RoleDAO;
import com.mycompany.teamcode_kanbanpro.dao.UserDAO;
import com.mycompany.teamcode_kanbanpro.model.User;
import com.mycompany.teamcode_kanbanpro.server.handler.RoleServerHandler;
import com.mycompany.teamcode_kanbanpro.server.handler.UserServerHandler;
import com.mycompany.teamcode_kanbanpro.util.BCryptUtil;
import java.io.*;
import java.net.Socket;
import java.util.Map;
/**
 *
 * @author Emanuel
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    //DAOs
    private UserDAO userDAO = new UserDAO(); 
    private final RoleDAO roleDAO = new RoleDAO();
    private final UserServerHandler userHandler;
    private final RoleServerHandler roleHandler;
    
    public ClientHandler(Socket socket) { 
        this.socket = socket; 
        this.userHandler = new UserServerHandler(userDAO, roleDAO);
        this.roleHandler = new RoleServerHandler(roleDAO);

    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Object obj;
            // leemos seguidos los request del cliente, por eso entra en el bucle
            //no modificar este bucle
            while ((obj = in.readObject()) != null) {
                if (!(obj instanceof Request)) continue;
                
                Request req = (Request) obj;
                Response resp = handleRequest(req);
                
                // Envía la respuesta al cliente
                out.writeObject(resp);
                out.flush();
            }
        } catch (EOFException eof) {
            System.out.println("Cliente desconectado: " + socket.getInetAddress().getHostAddress());
        } catch (Exception e) {
            System.err.println("Error en la comunicacion con el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { 
                if (!socket.isClosed()) {
                    socket.close(); 
                }
            } catch (Exception ex) {
                System.err.println("Error al cerrar el socket: " + ex.getMessage());
            }
        }
    }
    
    private Response handleRequest(Request req) {
        String action = req.getAction();
       
        System.out.println("At: "+action.toLowerCase());
        switch (action.toLowerCase()) {
            case "login":
                // return loginHandler.handleLogin(req); // version a futuro esto no se puede quedar local
                return handleLogin(req); 
                
            case "register":
                return  userHandler.handleRegister(req);
                
            case "getroles": 
                return roleHandler.handleGetRoles();

            default:
                return new Response(false, "Acción no soportada: " + action);
        }
    }
    
    private Response handleLogin(Request req) {
        try {
            Map<String, Object> p = req.getPayload();

            String credencial = (String) p.get("usaurio");
            String clavePlain = (String) p.get("clave");
            System.out.println("User" + credencial + " clave" + clavePlain + "p" + p);
            if (credencial == null || clavePlain == null) {
                return new Response(false, "Faltan credenciales de acceso.");
            }

            // consultamos por el usuario o email que coincida
            User u = userDAO.selectUserByUsernameOrEmail(credencial);

            if (u == null) {
                return new Response(false, "Usuario o contraseña incorrectos. from U");
            }

            // si no esta activa
            if (!u.isActivo()) {
                return new Response(false, "La cuenta está inactiva.");
            }

            //Se verificamos si las claves son las mismas textoplano vs Hash
            boolean ok = BCryptUtil.checkPwd(clavePlain, u.getPassword());
            System.out.println("ok  :" + ok);
            if (ok) {
                Response r = new Response(true, "Login correcto.");

                //eliminamos la clave
                User safeUser = new User();
                safeUser.setIdUsuario(u.getIdUsuario());
                safeUser.setIdRol(u.getIdRol());
                safeUser.setUsuario(u.getUsuario());
                safeUser.setNombre(u.getNombre());
                safeUser.setEmail(u.getEmail());
                safeUser.setActivo(u.isActivo());
                //devolvemos el usuario en el setData
                r.setData(safeUser);
                return r;
            } else {
                return new Response(false, "Usuario o contraseña incorrectos. from isnot OK");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno del servidor: " + ex.getMessage());
        }
    }
}
