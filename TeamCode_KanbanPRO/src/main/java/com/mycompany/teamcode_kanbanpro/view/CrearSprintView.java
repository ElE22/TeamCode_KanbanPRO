/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
/**
 *
 * @author salaz
 */
public class CrearSprintView extends JFrame {
     private JLabel lblTituloCuadro;
    private JLabel lblNombre;
    private JLabel lblFechaInicio;
    private JLabel lblFechaFin;
    private JLabel lblDescripcion;

    private JTextField txtNombre;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextArea txtDescripcion;
    private JButton btnGuardar;
    private JPanel panelSprint;

    public CrearSprintView() {
        setTitle("Gestión de Sprints");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // No cierra la app principal
        setLayout(new BorderLayout());
        initComponentes();
        setVisible(true);
    }

    private void initComponentes() {
        panelSprint = new JPanel();
        panelSprint.setBackground(new Color(230, 245, 255));
        panelSprint.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelSprint.setLayout(new BorderLayout(10, 10));

        // ---------- Título ----------
        lblTituloCuadro = new JLabel("Crear Nuevo Sprint", SwingConstants.CENTER);
        lblTituloCuadro.setForeground(new Color(25, 118, 210));
        lblTituloCuadro.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloCuadro.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panelSprint.add(lblTituloCuadro, BorderLayout.NORTH);

        // ---------- Panel de formulario ----------
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Campo: Nombre del Sprint
        lblNombre = new JLabel("Nombre del Sprint:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(lblNombre, gbc);

        txtNombre = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        panelFormulario.add(txtNombre, gbc);

        // Campo: Fecha de inicio
        lblFechaInicio = new JLabel("Fecha de Inicio (YYYY-MM-DD):");
        lblFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(lblFechaInicio, gbc);

        txtFechaInicio = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 1;
        panelFormulario.add(txtFechaInicio, gbc);

        // Campo: Fecha de fin
        lblFechaFin = new JLabel("Fecha de Fin (YYYY-MM-DD):");
        lblFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(lblFechaFin, gbc);

        txtFechaFin = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 2;
        panelFormulario.add(txtFechaFin, gbc);

        // Campo: Descripción
        lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(lblDescripcion, gbc);

        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        gbc.gridx = 1; gbc.gridy = 3;
        panelFormulario.add(scrollDescripcion, gbc);

        // Botón Guardar
        btnGuardar = new JButton("Guardar Sprint");
        btnGuardar.setBackground(new Color(25, 118, 210));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(new LineBorder(Color.WHITE, 2, true));
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(btnGuardar, gbc);

        panelSprint.add(panelFormulario, BorderLayout.CENTER);
        add(panelSprint, BorderLayout.CENTER);
    }
}
