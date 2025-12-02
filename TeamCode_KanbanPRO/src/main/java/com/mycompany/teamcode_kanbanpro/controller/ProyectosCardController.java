package com.mycompany.teamcode_kanbanpro.controller;


import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Project;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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
    private DefaultTableModel modeloProyectos;

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
        
        modeloProyectos = this.view.getModeloProyectos();
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
                List<Project> listProjects = (List<Project>) resp.getData();
                actualizarTablaProyectos(listProjects); 
                
                System.out.println("Proyectos cargados exitosamente. Total: " + listProjects.size());
                
            } else {
                 JOptionPane.showMessageDialog(null, "Error al cargar proyectos: " + resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error de comunicación con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTablaProyectos(List<Project> proyectos) {
        modeloProyectos.setRowCount(0);

        for (Project p : proyectos) {
            
            Object[] fila = new Object[5];
            fila[0] = p.getIdProyecto();
            fila[1] = p.getNombre();
            fila[2] = p.getDescripcion();
            fila[3] = p.getGruposPertenencia(); // Asumimos que el Project Model tiene este getter String
            fila[4] = p.getFechaCreacion();
            
            modeloProyectos.addRow(fila);
        }
    }
}

