/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author salaz
 */
public class CrearGrupoView extends JFrame {
    
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JButton btnCrear;
    private JButton btnCancelar;

    public CrearGrupoView() {
        setTitle("Crear Nuevo Grupo");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponentes();
    }
    
    private void initComponentes() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);
        
        // Título
        JLabel lblTitulo = new JLabel("Nuevo Grupo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(25, 118, 210));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel del formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Label Nombre
        JLabel lblNombre = new JLabel("Nombre del grupo:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(lblNombre, gbc);
        
        // Campo Nombre
        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setPreferredSize(new Dimension(380, 35));
        gbc.gridy = 1;
        panelFormulario.add(txtNombre, gbc);
        
        // Label Descripción
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        panelFormulario.add(lblDescripcion, gbc);
        
        // Campo Descripción (sin scroll, tamaño fijo)
        txtDescripcion = new JTextArea(4, 30);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panelFormulario.add(txtDescripcion, gbc);
        
        mainPanel.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        
        // Botón Cancelar
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancelar.setPreferredSize(new Dimension(100, 38));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Botón Crear
        btnCrear = new JButton("Crear Grupo");
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCrear.setBackground(new Color(25, 118, 210));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFocusPainted(false);
        btnCrear.setBorderPainted(false);
        btnCrear.setPreferredSize(new Dimension(120, 38));
        btnCrear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnCrear);
        
        mainPanel.add(panelBotones, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // Getters
    public JTextField getTxtNombre() {
        return txtNombre;
    }
    
    public JTextArea getTxtDescripcion() {
        return txtDescripcion;
    }
    
    public JButton getBtnCrear() {
        return btnCrear;
    }
    
    public JButton getBtnCancelar() {
        return btnCancelar;
    }
}
