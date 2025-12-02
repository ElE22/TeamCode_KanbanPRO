package com.mycompany.teamcode_kanbanpro.controller;


import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Emanuel
 */

public class ProyectosController {
    private ProyectosView view;
    private ClientConnector connector;

    // El controlador requiere la vista para interactuar con ella
    // y el conector para hablar con el servidor.
    public ProyectosController(ProyectosView view, ClientConnector connector) {
        this.view = view;
        this.connector = connector;
        initialize();
        cargarProyectosIniciales(); // Llama a la lógica de negocio al iniciar el controlador
    }

    private void initialize() {
        // 1. Manejar el evento de crear proyecto
        this.view.getBtnCrearProyecto().addActionListener(e -> crearNuevoProyecto());
    }

    private void crearNuevoProyecto() {
        // Lógica para enviar el proyecto nuevo al servidor
        String nombre = view.getTxtNombreProyecto().getText();
        String descripcion = view.getTxtDescripcion().getText();
        
        // Aquí iría el código para construir el objeto Request 
        // y usar this.connector.sendRequest()
        
        System.out.println("Intentando crear proyecto: " + nombre);
    }
    
    private void cargarProyectosIniciales() {
        // 2. Lógica para solicitar proyectos al servidor y actualizar la tabla (vista)
        try {
            Request req = new Request();
            req.setAction("obtenerProyectos");
            // Puedes añadir el ID de usuario si es necesario en el payload
            
            Response resp = connector.sendRequest(req);
            
            if (resp.isSuccess()) {
                // Aquí se asumiría que resp.getPayload() trae la lista de proyectos
                // El controlador procesa los datos y llama a un método en la vista
                // para actualizar la tabla (ej: view.actualizarTabla(datos);)
                System.out.println("Proyectos cargados exitosamente.");
            } else {
                 JOptionPane.showMessageDialog(null, "Error al cargar proyectos: " + resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error de comunicación con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

