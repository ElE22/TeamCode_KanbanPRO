/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response; 
import com.mycompany.teamcode_kanbanpro.dao.UserDAO;
import com.mycompany.teamcode_kanbanpro.model.User;
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
    // Instanciamos el UserDAO de tu proyecto Kanban
    private UserDAO userDAO = new UserDAO(); 

    public ClientHandler(Socket socket) { 
        this.socket = socket; 
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Object obj;
            // leemos seguidos los request del cliente, por eso entra en el bucle
            while ((obj = in.readObject()) != null) {
                if (!(obj instanceof Request)) continue;
                
                Request req = (Request) obj;
                Response resp = handleRequest(req);
                
                // Envía la respuesta al cliente
                out.writeObject(resp);
                out.flush();
            }
        } catch (EOFException eof) {
            // El cliente cerro la conexioon
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
        try {
            String action = req.getAction();
            Map<String, Object> p = req.getPayload();
            
            //si es login
            if ("login".equalsIgnoreCase(action)) {
                String credencial = (String) p.get("usaurio"); 
                String clavePlain = (String) p.get("clave");
                System.out.println("User"+credencial+" clave"+clavePlain+ "p"+p);
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
                System.out.println("ok  :"+ok);
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
                    
                    r.setData(safeUser); 
                    return r;
                } else {
                    return new Response(false, "Usuario o contraseña incorrectos. from isnot OK");
                }
            } else {
                return new Response(false, "Accion no soportada: " + action);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno del servidor: " + ex.getMessage());
        }
    }
}
