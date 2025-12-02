package com.mycompany.teamcode_kanbanpro.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Emanuel
 */
public class ClientConnector implements AutoCloseable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Integer UserID;
    private String UserName;
    private String userRole;

    public ClientConnector(String host, int port) throws Exception {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }
    public Response sendRequest(Request req) throws Exception {
        out.writeObject(req);
        out.flush();
        Object resp = in.readObject();
        if (resp instanceof Response) return (Response) resp;
        throw new IllegalStateException("Respuesta inesperada del servidor");
    }

    public String getUserRole() {
        return userRole;
    }

    public Integer getUserID() {
        return UserID;
    }

    public void setUserID(Integer UserID) {
        this.UserID = UserID;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    @Override
    public void close() throws Exception {
        try { if (out != null) out.close(); } catch (Exception e) {}
        try { if (in != null) in.close(); } catch (Exception e) {}
        try { if (socket != null) socket.close(); } catch (Exception e) {}
    }

    
}
