/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.JFrame;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author salaz
 */
public class CrearTareaView extends JFrame {
   /* private JLabel lblTituloCuadro;
    private JLabel lblTitulo;
    private JLabel lblDescripcion;
    private JLabel lblPrioridad;
    private JTextField txtTitulo;
    private JTextField txtDescripcion;
    private JComboBox<String> cmbPrioridad;
    private JButton btnGuardar;
    private JPanel panelTareas;;
    
    public CrearTareaView(){
         setTitle("Gestión de Tareas");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponentes();
        setVisible(true);
      
        
      
    }
    private void initComponentes(){   
        panelTareas = new JPanel();
        panelTareas.setBackground(new Color(173, 216, 230));
        panelTareas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelTareas.setLayout(new BorderLayout(10,10));
        
        lblTituloCuadro = new JLabel("Crear Nueva Tareas", SwingConstants.CENTER);
        lblTituloCuadro.setForeground(new Color(25, 118, 210));
        lblTituloCuadro.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloCuadro.setBorder(BorderFactory.createEmptyBorder(10,0,20,0));
        panelTareas.add(lblTituloCuadro, BorderLayout.NORTH);
        
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Campo Título
        lblTitulo = new JLabel("Título:");
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; 
        panelFormulario.add(lblTitulo, gbc);

        txtTitulo = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0; 
        panelFormulario.add(txtTitulo, gbc);

        // Campo Descripción
        lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; 
        panelFormulario.add(lblDescripcion, gbc);

        txtDescripcion = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; 
        panelFormulario.add(txtDescripcion, gbc);

        // Campo Prioridad
        lblPrioridad = new JLabel("Prioridad:");
        lblPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2; 
        panelFormulario.add(lblPrioridad, gbc);

        cmbPrioridad = new JComboBox<>(new String[]{"Baja", "Media", "Alta", "Crítica"});
        gbc.gridx = 1; gbc.gridy = 2; 
        panelFormulario.add(cmbPrioridad, gbc);

        // Botón Guardar
        btnGuardar = new JButton("Guardar Tarea");
        btnGuardar.setBackground(new Color(25, 118, 210));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(new LineBorder(Color.WHITE, 2, true));

        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(btnGuardar, gbc);

        // Agregar el formulario al panel principal
        panelTareas.add(panelFormulario, BorderLayout.CENTER);
        add(panelTareas, BorderLayout.CENTER);
    }
*/

}
