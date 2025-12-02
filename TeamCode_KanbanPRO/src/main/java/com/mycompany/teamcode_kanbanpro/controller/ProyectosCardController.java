package com.mycompany.teamcode_kanbanpro.controller;


import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Emanuel
 */

public class ProyectosCardController {
    private ProyectosView view;
    private ClientConnector connector;

    // el controlador requiere la vista para interactuar con ella
    // y el conector para hablar con el servidor
    public ProyectosCardController(ProyectosView view, ClientConnector connector) {
        this.view = view;
        this.connector = connector;
        initialize();
        cargarProyectosIniciales(); // llama a la logica de negocio al iniciar el controlador
    }

    private void initialize() {
        // manejar el evento de crear proyecto
        this.view.getBtnCrearProyecto().addActionListener(e -> crearNuevoProyecto());
    }

    private void crearNuevoProyecto() {
        // logica para enviar el proyecto nuevo al servidor
        String nombre = view.getTxtNombreProyecto().getText();
        String descripcion = view.getTxtDescripcion().getText();
        
        // Aqui va el codigo para construir el objeto Request 
        // y usar this.connector.sendRequest()
        
        System.out.println("Intentando crear proyecto: " + nombre);
    }
    
    public void cargarProyectosIniciales() {
        // lgica para solicitar proyectos al servidor y actualizar la tabla (vista)
        try {
            Request req = new Request();
            req.setAction("getProjectsByUser");
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", connector.getUserID());
            req.setPayload(payload);
            Response resp = connector.sendRequest(req);
            if (resp.isSuccess()) {
                // aqui se asumiria que resp.getPayload() trae la lista de proyectos
                // el controlador procesa los datos y llama a un metodo en la vista
                //TODO para actualizar la tabla (ej: view.actualizarTabla(datos);) 
                
                System.out.println("Proyectos cargados exitosamente.\n"+ resp.getData());
                
            } else {
                 JOptionPane.showMessageDialog(null, "Error al cargar proyectos: " + resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error de comunicación con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

