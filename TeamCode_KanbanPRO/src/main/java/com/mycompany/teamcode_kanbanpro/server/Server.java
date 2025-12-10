package com.mycompany.teamcode_kanbanpro.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int PORT = 3001;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 30;
    
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public Server() {
        this.threadPool = Executors.newCachedThreadPool();
        this.running = false;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        running = true;
        
        LOGGER.info("servidor iniciado en el puerto " + PORT);
        
        // hook para shutdown graceful
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("señal de cierre recibida, deteniendo servidor...");
            try {
                close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "error durante shutdown", e);
            }
        }));
        
        acceptClients();
    }

    // bucle principal que acepta conexiones
    private void acceptClients() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("nuevo cliente conectado: " + clientSocket.getInetAddress());
                
                threadPool.submit(new ClientHandler(clientSocket));
                
            } catch (IOException e) {
                if (running) {
                    LOGGER.log(Level.WARNING, "error aceptando cliente", e);
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (!running) {
            return;
        }
        
        running = false;
        
        // cerrar server socket para detener aceptacion de nuevas conexiones
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        
        // shutdown graceful del thread pool
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                LOGGER.warning("timeout esperando cierre de hilos, forzando shutdown");
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        LOGGER.info("servidor detenido correctamente");
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "error fatal al iniciar servidor", e);
            System.exit(1);
        }
    }
}
