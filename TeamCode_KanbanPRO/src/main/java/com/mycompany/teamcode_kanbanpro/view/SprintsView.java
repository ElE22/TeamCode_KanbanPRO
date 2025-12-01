/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
/**
 *
 * @author salaz
 */
public class SprintsView extends JPanel{
    private JTable tablaSprints;
    private JTable tablaBacklog;
    private JButton btnCrearSprint;
    private JButton btnAsignarTareas;
    private JButton btnCrearTareas;
    
    public SprintsView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        

        // ---------- Título superior ----------
        JLabel lblTitulo = new JLabel("Gestión de Sprints", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(25, 118, 210));
        add(lblTitulo, BorderLayout.NORTH);

        // ---------- Panel dividido ----------
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.5);
        splitPane.setBackground(Color.WHITE);

        // ---------- Panel Izquierdo: Lista de Sprints ----------
        JPanel panelSprints = new JPanel(new BorderLayout(8, 8));
        panelSprints.setBackground(Color.WHITE);
        panelSprints.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                "Sprints del Proyecto",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(25, 118, 210)
        ));

        String[] columnasSprints = {"ID", "Nombre", "Estado", "Inicio", "Fin"};
        Object[][] datosSprints = {
                {1, "Sprint 1 - Login", "Activo", "2024-09-01", "2024-09-15"},
                {2, "Sprint 2 - Dashboard", "Planificado", "2024-09-16", "2024-09-30"}
        };

        tablaSprints = new JTable(new DefaultTableModel(datosSprints, columnasSprints));
        tablaSprints.setFillsViewportHeight(true);
        tablaSprints.setSelectionBackground(new Color(187, 222, 251));
        tablaSprints.setSelectionForeground(Color.BLACK);
        JScrollPane scrollSprints = new JScrollPane(tablaSprints);

        btnCrearSprint = new JButton("Crear Sprint");
        btnCrearSprint.setBackground(new Color(25, 118, 210));
        btnCrearSprint.setForeground(Color.WHITE);
        btnCrearSprint.setFocusPainted(false);
        btnCrearSprint.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCrearSprint.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        btnCrearSprint.addActionListener(e -> {
            CrearSprintView crearSprint = new CrearSprintView();
            crearSprint.setVisible(true);
        });

        panelSprints.add(scrollSprints, BorderLayout.CENTER);
        panelSprints.add(btnCrearSprint, BorderLayout.SOUTH);

        // ---------- Panel Derecho: Backlog ----------
        JPanel panelBacklog = new JPanel(new BorderLayout(8, 8));
        panelBacklog.setBackground(Color.WHITE);
        panelBacklog.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                "Backlog (Tareas sin Sprint)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(25, 118, 210)
        ));

        String[] columnasBacklog = {"ID", "Título", "Prioridad", "Descripción"};
        Object[][] datosBacklog = {
                {200, "Diseñar pantalla de login", "Alta", "Creación de interfaz inicial"},
                {201, "Validar datos del usuario", "Media", "Validación en backend"},
                {202, "Agregar recuperación de contraseña", "Baja", "Función opcional"}
        };

        tablaBacklog = new JTable(new DefaultTableModel(datosBacklog, columnasBacklog));
        tablaBacklog.setFillsViewportHeight(true);
        tablaBacklog.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaBacklog.setSelectionBackground(new Color(232, 245, 233));
        tablaBacklog.setSelectionForeground(Color.BLACK);
        JScrollPane scrollBacklog = new JScrollPane(tablaBacklog);

        btnAsignarTareas = new JButton("Asignar al Sprint seleccionado");
        btnAsignarTareas.setBackground(new Color(67, 160, 71));
        btnAsignarTareas.setForeground(Color.WHITE);
        btnAsignarTareas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAsignarTareas.setFocusPainted(false);
        btnAsignarTareas.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        btnCrearTareas = new JButton("Crear tarea");
        btnCrearTareas.setBackground(new Color(25, 118, 210));
        btnCrearTareas.setForeground(Color.WHITE);
        btnCrearTareas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCrearTareas.setFocusPainted(false);
        btnCrearTareas.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        btnCrearTareas.addActionListener(e -> {
        CrearTareaView crearTarea = new CrearTareaView();
        crearTarea.setVisible(true);
        });

        panelBacklog.add(scrollBacklog, BorderLayout.CENTER);
        panelBacklog.add(btnAsignarTareas, BorderLayout.SOUTH);
        panelBacklog.add(btnCrearTareas, BorderLayout.NORTH);
        
        

        // ---------- Unir los paneles ----------
        splitPane.setLeftComponent(panelSprints);
        splitPane.setRightComponent(panelBacklog);

        add(splitPane, BorderLayout.CENTER);
    }
}
