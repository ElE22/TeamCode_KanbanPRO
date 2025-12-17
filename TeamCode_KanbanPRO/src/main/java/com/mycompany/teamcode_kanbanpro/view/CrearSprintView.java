/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import com.toedter.calendar.JDateChooser;

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
    private JDateChooser dateChooserInicio;
    private JDateChooser dateChooserFin;
    private JTextArea txtDescripcion;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JPanel panelSprint;

    // Colores del tema
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color INPUT_BG = new Color(249, 249, 249);
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Color BORDER_COLOR = new Color(200, 200, 200);

    public CrearSprintView() {
        setTitle("Crear Nuevo Sprint");
        setSize(500, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        initComponentes();
        establecerFechasPorDefecto();
    }

    private void initComponentes() {
        panelSprint = new JPanel();
        panelSprint.setBackground(BACKGROUND_COLOR);
        panelSprint.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panelSprint.setLayout(new BorderLayout(10, 10));

        //TÍTULO
        lblTituloCuadro = new JLabel("Crear Nuevo Sprint", SwingConstants.CENTER);
        lblTituloCuadro.setForeground(PRIMARY_COLOR);
        lblTituloCuadro.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloCuadro.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        panelSprint.add(lblTituloCuadro, BorderLayout.NORTH);

        //PANEL DE FORMULARIO
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(CARD_COLOR);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
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
        txtNombre.setBackground(INPUT_BG);
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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

        dateChooserInicio = new JDateChooser();
        dateChooserInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooserInicio.setDateFormatString("dd/MM/yyyy"); 
        // dateChooserInicio.setDateFormatString("yyyy-MM-dd"); // para usar formato yyyy-MM-dd
        dateChooserInicio.setMinSelectableDate(new Date()); 
        dateChooserInicio.setBackground(INPUT_BG); 
        dateChooserInicio.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        dateChooserInicio.setPreferredSize(new Dimension(200, 35));// 
        dateChooserInicio.setToolTipText("Seleccione la fecha de inicio del sprint");
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelFormulario.add(dateChooserInicio, gbc);


        lblFechaFin = new JLabel("Fecha de Fin: *");
        lblFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(lblFechaFin, gbc);

        dateChooserFin = new JDateChooser();
        dateChooserFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooserFin.setDateFormatString("dd/MM/yyyy");
        // dateChooserFin.setDateFormatString("yyyy-MM-dd"); // para usar formato yyyy-MM-dd
        dateChooserFin.setMinSelectableDate(new Date()); 
        dateChooserFin.setBackground(INPUT_BG);
        dateChooserFin.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        dateChooserFin.setPreferredSize(new Dimension(200, 35));
        dateChooserFin.setToolTipText("Seleccione la fecha de fin del sprint");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelFormulario.add(dateChooserFin, gbc);

        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panelFormulario.add(lblNota, gbc);

        JLabel lblInfo = new JLabel("Duración mínima recomendada: 8 días");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(100, 100, 100));
        gbc.gridx = 1;
        gbc.gridy = 4;
        panelFormulario.add(lblInfo, gbc);

        lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelFormulario.add(lblDescripcion, gbc);

        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setToolTipText("Descripción opcional del sprint");
        txtDescripcion.setBackground(INPUT_BG);
        txtDescripcion.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollDescripcion.setBackground(INPUT_BG);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(scrollDescripcion, gbc);

        panelSprint.add(panelFormulario, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBotones.setBackground(BACKGROUND_COLOR);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setFocusPainted(false);

        btnGuardar = new JButton("Guardar Sprint");
        btnGuardar.setBackground(PRIMARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(140, 35));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setBorderPainted(false);

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        panelSprint.add(panelBotones, BorderLayout.SOUTH);

        add(panelSprint, BorderLayout.CENTER);
    }

    //Por defecto pone 15 dias de duracion al sprint
    private void establecerFechasPorDefecto() {
        LocalDate hoy = LocalDate.now();
        LocalDate finSprint = hoy.plusDays(14); 

        // convertir LocalDate a date para jdatechooser
        Date fechaInicio = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaFin = Date.from(finSprint.atStartOfDay(ZoneId.systemDefault()).toInstant());

        dateChooserInicio.setDate(fechaInicio);
        dateChooserFin.setDate(fechaFin);
    }

    
    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JDateChooser getDateChooserInicio() {
        return dateChooserInicio;
    }

    public JDateChooser getDateChooserFin() {
        return dateChooserFin;
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