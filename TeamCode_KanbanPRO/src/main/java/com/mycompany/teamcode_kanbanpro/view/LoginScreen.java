/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

/**
 *
 * @author diana
 */
import javax.swing.*;
import java.awt.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("Iniciar Sesión - KanbanPro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null); 

        // Panel principal con margen
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Bienvenido al Sistema Kanban", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Campos de entrada
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);

        // Etiqueta de Usuario
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Usuario:"), gbc);

        // Campo de Usuario
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(userField, gbc);

        // Etiqueta de Contraseña
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);

        // Campo de Contraseña
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passField, gbc);

        // Botón
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setBackground(new Color(66, 133, 244)); 
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        // Acción del botón 
        loginButton.addActionListener(e -> {
            
            this.dispose(); // Cerramos la ventana de login
            new KanbanBoardScreen().setVisible(true); // Abre la pizarra kanban
        });

        add(panel);
    }
}