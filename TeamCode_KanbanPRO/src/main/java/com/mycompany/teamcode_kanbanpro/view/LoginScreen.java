package com.mycompany.teamcode_kanbanpro.view;

/**
 *
 * @author diana
 */

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
    public JButton loginButton;
    public JButton registerButton;
    public JTextField userField;
    public JPasswordField passField;

    public LoginScreen() {
        setTitle("Iniciar Sesión - KanbanPro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null); 

        // Panel principal con margen
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titulo
        JLabel titleLabel = new JLabel("Bienvenido al Sistema Kanban", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Campos de entrada
        userField = new JTextField(15);
        passField = new JPasswordField(15);

        // Etiqueta de Usuario
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Usuario:"), gbc);

        // Campo de Usuario
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(userField, gbc);

        // Etiqueta de clave
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);

        // Campo de Contraseña
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passField, gbc);

        // Boton
        loginButton = new JButton("Iniciar Sesión");
        loginButton.setBackground(new Color(66, 133, 244)); 
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);
        
        //Boton registrar
        registerButton = new JButton("Registrarse");
        registerButton.setBackground(new Color(34, 163, 63)); 
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(registerButton, gbc);
        add(panel);
    }
}