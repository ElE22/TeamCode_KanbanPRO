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
 * @author salaz
 */
public class ProyectosView extends JPanel {

    // Componentes del formulario
    private JTextField txtNombreProyecto;
    private JButton btnCrearProyecto;
    private JButton btnCrearSprint;
    private JTable tablaProyectos;
    private JTable tablaSprints;

    // Modelos de las tablas (para mejor control)
    private DefaultTableModel modeloProyectos;
    private DefaultTableModel modeloSprints;

    public ProyectosView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 248, 255));

        initComponentes();
    }

    private void initComponentes() {

        JLabel lblTitulo = new JLabel("Gestión de Proyectos (Scrum Master)", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(25, 118, 210));
        add(lblTitulo, BorderLayout.NORTH);

        // anel dividido (Proyectos,Sprints)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.5);
        splitPane.setBackground(Color.WHITE);

        //PANEL IZQUIERDO: PROYECTOS
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

        // Columnas de proyectos
        String[] columnasProyectos = {"ID", "Nombre", "Descripción", "Grupos", "Fecha Creación"};

        // Modelo de tabla para proyectos
        modeloProyectos = new DefaultTableModel(columnasProyectos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };

        tablaProyectos = new JTable(modeloProyectos);
        tablaProyectos.setFillsViewportHeight(true);
        tablaProyectos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProyectos.setSelectionBackground(new Color(187, 222, 251));
        tablaProyectos.setSelectionForeground(Color.BLACK);
        tablaProyectos.setRowHeight(25);

        // Configurar ancho de columnas
        tablaProyectos.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tablaProyectos.getColumnModel().getColumn(1).setPreferredWidth(120); // Nombre
        tablaProyectos.getColumnModel().getColumn(2).setPreferredWidth(150); // Descripción
        tablaProyectos.getColumnModel().getColumn(3).setPreferredWidth(100); // Grupos
        tablaProyectos.getColumnModel().getColumn(4).setPreferredWidth(100); // Fecha

        JScrollPane scrollProyectos = new JScrollPane(tablaProyectos);
        panelProyectos.add(scrollProyectos, BorderLayout.CENTER);

        // Botón Crear Proyecto
        btnCrearProyecto = new JButton("Crear Proyecto");
        btnCrearProyecto.setBackground(new Color(25, 118, 210));
        btnCrearProyecto.setForeground(Color.WHITE);
        btnCrearProyecto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCrearProyecto.setFocusPainted(false);
        btnCrearProyecto.setBorder(new LineBorder(Color.WHITE, 2, true));
        btnCrearProyecto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelProyectos.add(btnCrearProyecto, BorderLayout.SOUTH);

        //PANEL DERECHO: SPRINTS
        JPanel panelSprints = new JPanel(new BorderLayout(8, 8));
        panelSprints.setBackground(Color.WHITE);
        panelSprints.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                "Sprints del Proyecto Seleccionado",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(25, 118, 210)
        ));
        String[] columnasSprints = {"ID", "Nombre", "Estado", "Fecha Inicio", "Fecha Fin"};
        modeloSprints = new DefaultTableModel(columnasSprints, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaSprints = new JTable(modeloSprints);
        tablaSprints.setFillsViewportHeight(true);
        tablaSprints.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSprints.setSelectionBackground(new Color(187, 222, 251));
        tablaSprints.setSelectionForeground(Color.BLACK);
        tablaSprints.setRowHeight(25);

        // Configurar ancho de columnas de sprints
        tablaSprints.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tablaSprints.getColumnModel().getColumn(1).setPreferredWidth(120); // Nombre
        tablaSprints.getColumnModel().getColumn(2).setPreferredWidth(80);  // Estado
        tablaSprints.getColumnModel().getColumn(3).setPreferredWidth(90);  // Fecha Inicio
        tablaSprints.getColumnModel().getColumn(4).setPreferredWidth(90);  // Fecha Fin

        JScrollPane scrollSprints = new JScrollPane(tablaSprints);
        panelSprints.add(scrollSprints, BorderLayout.CENTER);

        // Panel inferior con instrucción y botón
        JPanel panelInferiorSprints = new JPanel(new BorderLayout(5, 5));
        panelInferiorSprints.setBackground(Color.WHITE);

        JLabel lblInstruccion = new JLabel("Seleccione un proyecto para ver sus sprints", SwingConstants.CENTER);
        lblInstruccion.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInstruccion.setForeground(Color.GRAY);
        panelInferiorSprints.add(lblInstruccion, BorderLayout.NORTH);

        // Botón Crear Sprint
        btnCrearSprint = new JButton("Crear Sprint");
        btnCrearSprint.setBackground(new Color(76, 175, 80)); // Verde para diferenciar
        btnCrearSprint.setForeground(Color.WHITE);
        btnCrearSprint.setFocusPainted(false);
        btnCrearSprint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCrearSprint.setBorder(new LineBorder(Color.WHITE, 2, true));
        btnCrearSprint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelInferiorSprints.add(btnCrearSprint, BorderLayout.SOUTH);

        panelSprints.add(panelInferiorSprints, BorderLayout.SOUTH);

        //Agregar paneles al SplitPane
        splitPane.setLeftComponent(panelProyectos);
        splitPane.setRightComponent(panelSprints);
        add(splitPane, BorderLayout.CENTER);
    }

    public JTextField getTxtNombreProyecto() {
        return txtNombreProyecto;
    }

    public JButton getBtnCrearProyecto() {
        return btnCrearProyecto;
    }

    public JButton getBtnCrearSprint() {
        return btnCrearSprint;
    }

    public JTable getTablaProyectos() {
        return tablaProyectos;
    }

    public JTable getTablaSprints() {
        return tablaSprints;
    }

    public DefaultTableModel getModeloProyectos() {
        return modeloProyectos;
    }

    public DefaultTableModel getModeloSprints() {
        return modeloSprints;
    }

    public void limpiarTablaSprints() {
        modeloSprints.setRowCount(0);
    }

    //Muestra un mensaje cuando no hay sprints
    public void mostrarMensajeSinSprints() {
        modeloSprints.setRowCount(0);
        // Opcionalmente podrías agregar una fila con mensaje
    }

    //Obtiene el ID del proyecto seleccionado en la tabla
    public int getIdProyectoSeleccionado() {
        int filaSeleccionada = tablaProyectos.getSelectedRow();
        if (filaSeleccionada != -1) {
            return (int) modeloProyectos.getValueAt(filaSeleccionada, 0);
        }
        return -1;
    }
}
