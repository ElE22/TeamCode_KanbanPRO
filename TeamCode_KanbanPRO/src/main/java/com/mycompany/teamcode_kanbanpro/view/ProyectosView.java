/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
/**
 *
 * @author salaz
 */
public class ProyectosView extends JPanel {
    // Componentes del formulario
    private JTextField txtNombreProyecto;
    private JTextArea txtDescripcion;
    private JButton btnCrearProyecto;
    private JTable tablaProyectos;

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
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.4);
        splitPane.setBackground(Color.WHITE);

        // ---------- Panel Izquierdo: Formulario ----------
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Nuevo Proyecto"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre del Proyecto
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre del Proyecto:"), gbc);
        txtNombreProyecto = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(txtNombreProyecto, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(scrollDescripcion, gbc);

        // Botón Guardar
        btnCrearProyecto = new JButton("Crear Proyecto");
        btnCrearProyecto.setBackground(new Color(25, 118, 210));
        btnCrearProyecto.setForeground(Color.WHITE);
        btnCrearProyecto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCrearProyecto.setFocusPainted(false);
        btnCrearProyecto.setBorder(new LineBorder(Color.WHITE, 2, true));
        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(btnCrearProyecto, gbc);

        // ---------- Panel Derecho: Tabla ----------
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createTitledBorder("Proyectos Existentes"));

        String[] columnas = {"ID", "Nombre", "Descripción", "Fecha Creación"};
        Object[][] datos = {
            {10, "Plataforma Web 3.0", "Desarrollo de nueva interfaz de usuario", "2024-09-10"},
            {11, "API Interna", "Optimización de microservicios internos", "2024-10-22"}
        };

        tablaProyectos = new JTable(new DefaultTableModel(datos, columnas));
        tablaProyectos.setFillsViewportHeight(true);
        tablaProyectos.setSelectionBackground(new Color(187, 222, 251));
        tablaProyectos.setSelectionForeground(Color.BLACK);
        JScrollPane scrollTabla = new JScrollPane(tablaProyectos);

        panelTabla.add(scrollTabla, BorderLayout.CENTER);

        // ---------- Agregar paneles al SplitPane ----------
        splitPane.setLeftComponent(panelFormulario);
        splitPane.setRightComponent(panelTabla);

        // ---------- Agregar todo al Panel Principal ----------
        add(splitPane, BorderLayout.CENTER);
    }

    // Getters para acceder a los componentes desde el controlador
    public JTextField getTxtNombreProyecto() { return txtNombreProyecto; }
    public JTextArea getTxtDescripcion() { return txtDescripcion; }
    public JButton getBtnCrearProyecto() { return btnCrearProyecto; }
    public JTable getTablaProyectos() { return tablaProyectos; }

}
