package com.mycompany.teamcode_kanbanpro.controller;


import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Project;
import com.mycompany.teamcode_kanbanpro.model.Sprint;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JTable;
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
    private DefaultTableModel modeloSprints;

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
        modeloSprints = this.view.getModeloSprints();
        
        this.view.getTablaProyectos().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // Obtener la JTable que disparo el evento
                JTable tabla = (JTable) e.getSource();

                // identificar la fila que fue seleccionada
                int filaSeleccionada = tabla.getSelectedRow();

                // Verificamos que se haya seleccionado una fila valida, donde -1 indica que no hay seleccion
                if (filaSeleccionada != -1) {

                    // obtenemos el id del proyecto de la fila seleccionada
                    int idProject = (Integer) modeloProyectos.getValueAt(filaSeleccionada, 0);

                    // cargar los sprints asociados a ese proyecto seleccionado
                    cargarSprintsParaProyecto(idProject);
                }
            }
        });
    }

    private void cargarSprintsParaProyecto(int projectID) {
        try {
            Request req = new Request();
            req.setAction("getSprintsByProject");
            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", projectID);
            req.setPayload(payload);
            Response resp = connector.sendRequest(req);
            if (resp.isSuccess()) {
                List<Sprint> listSprints = (List<Sprint>) resp.getData();
                actualizarTablaSprints(listSprints);
                
            } else {
                 JOptionPane.showMessageDialog(null, "Error al cargar sprints: " + resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error de comunicación con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
                List<Project> listProjects = (List<Project>) resp.getData();
                actualizarTablaProyectos(listProjects); 
                
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
            fila[3] = p.getGruposPertenencia();
            fila[4] = p.getFechaCreacion();
            
            modeloProyectos.addRow(fila);
        }
    }

    private void actualizarTablaSprints(List<Sprint> sprints) {
        // Similar a actualizarTablaProyectos, pero para sprints
        this.modeloSprints.setRowCount(0);
        for (Sprint s : sprints) {
            Object[] fila = new Object[5];
            fila[0] = s.getIdSprint();
            fila[1] = s.getNombre();
            fila[2] = s.getNombreEstado();
            fila[3] = s.getFechaInicio();
            fila[4] = s.getFechaFin();
            
            modeloSprints.addRow(fila);
        }
    }
}

