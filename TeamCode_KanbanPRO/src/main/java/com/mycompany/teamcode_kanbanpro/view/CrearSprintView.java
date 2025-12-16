/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
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
    private JButton btnCancelar;
    private JPanel panelSprint;

    public CrearSprintView() {
        setTitle("Crear Nuevo Sprint");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        initComponentes();
        establecerFechasPorDefecto();
    }

    private void initComponentes() {
        panelSprint = new JPanel();
        panelSprint.setBackground(new Color(240, 248, 255));
        panelSprint.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panelSprint.setLayout(new BorderLayout(10, 10));

        //TÍTULO
        lblTituloCuadro = new JLabel("Crear Nuevo Sprint", SwingConstants.CENTER);
        lblTituloCuadro.setForeground(new Color(25, 118, 210));
        lblTituloCuadro.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloCuadro.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        panelSprint.add(lblTituloCuadro, BorderLayout.NORTH);

        //PANEL DE FORMULARIO
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        lblNombre = new JLabel("Nombre del Sprint: *");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panelFormulario.add(lblNombre, gbc);

        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setToolTipText("Ingrese un nombre descriptivo para el sprint (ej: Sprint 1 - Login)");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        panelFormulario.add(txtNombre, gbc);

        lblFechaInicio = new JLabel("Fecha de Inicio: *");
        lblFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panelFormulario.add(lblFechaInicio, gbc);

        txtFechaInicio = new JTextField(12);
        txtFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFechaInicio.setToolTipText("Formato: YYYY-MM-DD (ejemplo: 2024-12-01)");
        agregarPlaceholder(txtFechaInicio, "YYYY-MM-DD");
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelFormulario.add(txtFechaInicio, gbc);

        lblFechaFin = new JLabel("Fecha de Fin: *");
        lblFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(lblFechaFin, gbc);

        txtFechaFin = new JTextField(12);
        txtFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFechaFin.setToolTipText("Formato: YYYY-MM-DD (ejemplo: 2024-12-15)");
        agregarPlaceholder(txtFechaFin, "YYYY-MM-DD");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelFormulario.add(txtFechaFin, gbc);

        // ----- Nota sobre formato -----
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panelFormulario.add(lblNota, gbc);

        // ----- Campo: Descripción -----
        lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelFormulario.add(lblDescripcion, gbc);

        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setToolTipText("Descripción opcional del sprint");
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(scrollDescripcion, gbc);

        panelSprint.add(panelFormulario, BorderLayout.CENTER);

        // ========== PANEL DE BOTONES ==========
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBotones.setBackground(new Color(240, 248, 255));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // El ActionListener lo agrega el controlador

        btnGuardar = new JButton("Guardar Sprint");
        btnGuardar.setBackground(new Color(25, 118, 210));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(140, 35));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        // El ActionListener lo agrega el controlador

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        panelSprint.add(panelBotones, BorderLayout.SOUTH);

        add(panelSprint, BorderLayout.CENTER);
    }

    //Establece las fechas por defecto: - Fecha inicio: Hoy - Fecha fin: Hoy +   
    private void establecerFechasPorDefecto() {
        LocalDate hoy = LocalDate.now();
        LocalDate finSprint = hoy.plusDays(14); // Sprint típico de 2 semanas

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        txtFechaInicio.setText(hoy.format(formatter));
        txtFechaFin.setText(finSprint.format(formatter));

        // Cambiar color a negro porque ya tienen valor
        txtFechaInicio.setForeground(Color.BLACK);
        txtFechaFin.setForeground(Color.BLACK);
    }

    //Agrega efecto de placeholder a un campo de texto
    private void agregarPlaceholder(JTextField campo, String placeholder) {
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtFechaInicio() {
        return txtFechaInicio;
    }

    public JTextField getTxtFechaFin() {
        return txtFechaFin;
    }

    public JTextArea getTxtDescripcion() {
        return txtDescripcion;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        establecerFechasPorDefecto();
        txtNombre.requestFocus();
    }

    public void setTituloConProyecto(String nombreProyecto) {
        setTitle("Crear Sprint - " + nombreProyecto);
        lblTituloCuadro.setText("Nuevo Sprint para: " + nombreProyecto);
    }
}