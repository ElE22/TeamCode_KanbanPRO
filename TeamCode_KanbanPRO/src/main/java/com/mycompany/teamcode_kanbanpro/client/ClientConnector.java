package com.mycompany.teamcode_kanbanpro.client; // Paquete del conector cliente

import com.mycompany.teamcode_kanbanpro.controller.KanbanBoardController; // Controller para actualizar la UI Kanban
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JOptionPane;

/**
 *
 * @author Emanuel
 */

public class ClientConnector implements AutoCloseable { 

    private Socket socket; // Socket TCP hacia el servidor
    private ObjectOutputStream out; // Stream de salida para Requests
    private ObjectInputStream in; // Stream de entrada para Responses
    private Integer UserID; // Id del usuario autenticado
    private String UserName; // Nombre del usuario autenticado
    private String userRole; // Rol del usuario
    private BlockingQueue<Response> responseQueue = new LinkedBlockingQueue<>(); // Cola para respuestas sin bloqueo
    private KanbanBoardController kanbanController; // Referencia opcional al controller Kanban para broadcasts


    public ClientConnector(String host, int port) throws Exception { // Constructor que abre socket y streams
        socket = new Socket(host, port); // Conectar al servidor
        out = new ObjectOutputStream(socket.getOutputStream()); // Inicializar stream de salida
        in  = new ObjectInputStream(socket.getInputStream()); // Inicializar stream de entrada
        startListening(); // Iniciar hilo escuchador de mensajes entrantes
    }


    private void startListening() { // Inicia un hilo daemon que procesa respuestas del servidor
        Thread t = new Thread(() -> {
            try {
                // Bucle de lectura de objetos desde el servidor
                while (true) {
                    Object obj = in.readObject(); // Leer objeto serializado

                    if (!(obj instanceof Response resp)) { // Verificar tipo esperado
                        System.out.println("Objeto NO es Response, clase=" + obj.getClass().getName()); 
                        JOptionPane.showMessageDialog(null, "Objeto recibido del servidor no es del tipo esperado: " + obj.getClass().getName(),
                                "Error de comunicacion", JOptionPane.ERROR_MESSAGE);
                        continue; // Ignorar y seguir escuchando
                    }

                    if (resp.isBroadcast()) { // Si es una notificacion broadcast
                        if (kanbanController != null) {
                            kanbanController.handleIncomingTaskMovedNotification(resp); // Delegar al controller Kanban
                        }
                        continue; // No colocar en la cola de respuestas normales
                    }

                    responseQueue.put(resp); // Colocar la respuesta en la cola para sendRequest()
                }
            } catch (Exception e) {
                System.err.println("Error listener: " + e.getMessage()); // Log de error
                e.printStackTrace(); // Traza para depuración
            }
        });

        t.setDaemon(true); // Hilo daemon para no bloquear cierre de la app
        t.start(); // Iniciar el hilo
    }

    // Setter del controller
    public void setKanbanController(KanbanBoardController controller) { this.kanbanController = controller; } 

    public Response sendRequest(Request req) throws Exception { // Enviar request y esperar respuesta correspondiente
        out.writeObject(req); // Serializar y enviar request
        out.flush(); // Forzar envío
        return responseQueue.take(); // Bloquear hasta recibir la respuesta asociada
    }

    public void setUserID(Integer UserID) { this.UserID = UserID; } // Setter id usuario
    public void setUserRole(String userRole) { this.userRole = userRole; } // Setter rol usuario
    public void setUserName(String UserName) { this.UserName = UserName; } // Setter nombre usuario
    public Integer getUserID() { return UserID; } // Getter id usuario
    public String getUserRole() { return userRole; } // Getter rol usuario
    public String getUserName() { return UserName; } // Getter nombre usuario


    @Override
    public void close() throws Exception { socket.close(); } // Cerrar socket al finalizar
}

