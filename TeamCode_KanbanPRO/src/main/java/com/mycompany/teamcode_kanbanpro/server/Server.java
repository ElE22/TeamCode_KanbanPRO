package com.mycompany.teamcode_kanbanpro.server;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import java.net.ServerSocket;
import java.net.Socket;

public class Server implements AutoCloseable {
    private static final int PORT = 3001;
    private static ServerSocket serverSocketClose;
     public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            serverSocketClose = serverSocket;
            System.out.println("Servidor iniciado en el puerto " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al iniciar el servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            threadPool.shutdown();
            System.out.println("Servidor ha sido detenido.");
        }

    }
	 @Override
	 public void close() throws Exception {
          serverSocketClose.close();
		
	 }
}
