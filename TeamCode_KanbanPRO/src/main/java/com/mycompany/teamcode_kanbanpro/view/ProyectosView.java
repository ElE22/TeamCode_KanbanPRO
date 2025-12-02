/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.TitledBorder;
/**
 *
 * @author salaz
 */
public class ProyectosView extends JPanel {
     // Componentes del formulario
    private JTextField txtNombreProyecto;
    private JTextArea txtDescripcion;
    private JButton btnCrearProyecto;
     private JButton btnCrearSprint;
    private JTable tablaProyectos;
    private JTable tablaSprints;

    public ProyectosView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 248, 255));

        initComponentes();
    }

    private void initComponentes() {
        // ---------- Título ----------
        JLabel lblTitulo = new JLabel("Gestión de Proyectos (Scrum Master)", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(25, 118, 210));
        add(lblTitulo, BorderLayout.NORTH);

        // ---------- Panel dividido (Formulario / Tabla) ----------
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);
        splitPane.setBackground(Color.WHITE);

        // ---------- Panel Izquierdo: Formulario ----------
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
        panelSprints.add(scrollSprints, BorderLayout.CENTER);

        btnCrearSprint = new JButton("Crear Sprint");
        btnCrearSprint.setBackground(new Color(25, 118, 210));
        btnCrearSprint.setForeground(Color.WHITE);
        btnCrearSprint.setFocusPainted(false);
        btnCrearSprint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCrearSprint.setBorder(new LineBorder(Color.WHITE, 2, true));
        panelSprints.add(btnCrearSprint, BorderLayout.SOUTH);
        
        btnCrearSprint.addActionListener(e -> {
            CrearSprintView crearSprint = new CrearSprintView();
            crearSprint.setVisible(true);
        });
        
        // ---------- Panel Derecho: Tabla ----------
        JPanel panelProyectos = new JPanel(new BorderLayout(8, 8));
        panelProyectos.setBackground(Color.WHITE);
        panelProyectos.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                "Proyectos Existentes",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(25, 118, 210)
        ));
        String[] columnas = {"ID", "Nombre", "Descripción","Grupos", "Fecha Creación"};
        Object[][] datos = {
            {0, "No hay datos del servidor", "No hay datos del servidor","No hay datos del servidor", "No hay datos del servidor"}
        };
        
        tablaProyectos = new JTable(new DefaultTableModel(datos, columnas));
        tablaProyectos.setFillsViewportHeight(true);
        tablaProyectos.setSelectionBackground(new Color(187, 222, 251));
        tablaProyectos.setSelectionForeground(Color.BLACK);
        JScrollPane scrollTabla = new JScrollPane(tablaProyectos);
       panelProyectos.add(scrollTabla, BorderLayout.CENTER);
        
        
        // Botón Guardar
        btnCrearProyecto = new JButton("Crear Proyecto");
        btnCrearProyecto.setBackground(new Color(25, 118, 210));
        btnCrearProyecto.setForeground(Color.WHITE);
        btnCrearProyecto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCrearProyecto.setFocusPainted(false);
        btnCrearProyecto.setBorder(new LineBorder(Color.WHITE, 2, true));
        panelProyectos.add(btnCrearProyecto, BorderLayout.SOUTH);
        

        // ---------- Agregar paneles al SplitPane ----------
        splitPane.setLeftComponent(panelProyectos);
        splitPane.setRightComponent(panelSprints);

        // ---------- Agregar todo al Panel Principal ----------
        add(splitPane, BorderLayout.CENTER);
    }
    

    // Getters para acceder a los componentes desde el controlador
    public JTextField getTxtNombreProyecto() { return txtNombreProyecto; }
    public JTextArea getTxtDescripcion() { return txtDescripcion; }
    public JButton getBtnCrearProyecto() { return btnCrearProyecto; }
    public JTable getTablaProyectos() { return tablaProyectos; }
    public DefaultTableModel getModeloProyectos() {
        // Asegúrate de que tablaProyectos se inicialice con DefaultTableModel en initComponentes()
        return (DefaultTableModel) tablaProyectos.getModel();
    }
}

