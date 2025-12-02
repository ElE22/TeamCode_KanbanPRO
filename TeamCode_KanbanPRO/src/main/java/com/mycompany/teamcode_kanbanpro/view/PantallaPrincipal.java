/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author salaz
 */
public class PantallaPrincipal extends JFrame {
    private JPanel panelCentral;
    private CardLayout cardLayout;
      
    private ProyectosView panelProyectos;
    private SprintsView panelSprint;
    
      
    private JButton btnInicio;
    private JButton btnKanbanBoard;
    private JButton btnProyectos;
    private JButton btnSprints;
    private JButton btnSalir;
    
    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(panelCentral, nombrePanel);
    }
    
    public JButton getBtnInicio() {
        return btnInicio;
    }

    public JButton getBtnKanbanBoard() {
        return btnKanbanBoard;
    }

    public JButton getBtnProyectos() {
        return btnProyectos;
    }

    public JButton getBtnSprints() {
        return btnSprints;
    }

    public JButton getBtnSalir() {
        return btnSalir;
    }

    public ProyectosView getPanelProyectos() {
        return panelProyectos;
    }

    public PantallaPrincipal() {
        setTitle("KanbanPro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);

        // 🔹 Barra superior
        //JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel barraSuperior = new JPanel();
        barraSuperior.setBackground(new Color(25, 118, 210));
        //barraSuperior.add(new JLabel("Usuario: Ana García"));
        barraSuperior.setLayout(new BorderLayout());
        barraSuperior.setPreferredSize(new Dimension(0, 50)); // Altura de 50 px
        add(barraSuperior, BorderLayout.NORTH);
        
        //JLabel lblUsuario = new JLabel(nombreUsuario + " (" + rolUsuario + ")", SwingConstants.RIGHT);
        JLabel lblUsuario = new JLabel("Usuario: Ana García");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); // margen derecho
        barraSuperior.add(lblUsuario, BorderLayout.EAST);
        
        //JLabel lblTituloApp = new JLabel("Kanban System", SwingConstants.LEFT);
        JLabel lblTituloApp = new JLabel("KanbanPro");
        lblTituloApp.setForeground(Color.WHITE);
        lblTituloApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloApp.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); // margen izquierdo
        barraSuperior.add(lblTituloApp, BorderLayout.WEST);
       

        // 🔹 Menú lateral
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(new GridLayout(5, 1, 0, 10));
        menuLateral.setBackground(new Color(38, 50, 56)); // Gris oscuro
        menuLateral.setPreferredSize(new Dimension(180, 0));
        menuLateral.setBorder(BorderFactory.createEmptyBorder(80, 20, 80, 20));

        btnInicio = crearBotonMenu("Inicio");
        btnKanbanBoard = crearBotonMenu("Kanban Board");
        btnProyectos = crearBotonMenu("Proyectos");
        btnSalir = crearBotonMenu("Cerrar Sesión");
        /*
        menuLateral.add(btnDashboard);
        menuLateral.add(btnProyectos);
        menuLateral.add(btnTareas);
        menuLateral.add(btnSprints);
        */
        menuLateral.add(btnInicio);
        menuLateral.add(btnKanbanBoard);
        menuLateral.add(btnProyectos);
        //menuLateral.add(btnSprints);
        //menuLateral.add(btnBacklog);
        menuLateral.add(btnSalir);
        
        add(menuLateral, BorderLayout.WEST);

        // 🔹 Panel central dinámico
        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);

        // Creamos distintos paneles de contenido
        JPanel panelDashboard = new JPanel();
        panelDashboard.add(new JLabel("Bienvenido al Dashboard"));

        //JPanel panelProyectos = new JPanel();
        //panelProyectos.add(new JLabel("Gestión de Proyectos"));
        panelProyectos = new ProyectosView();
        JPanel panelTareas = new JPanel();
        panelTareas.add(new JLabel("Gestión de Tareas"));
        
        
        SprintsView panelSprint= new SprintsView();
        
        
        
        // Añadimos los paneles al CardLayout
        panelCentral.add(panelDashboard, "Dashboard");
        panelCentral.add(panelProyectos, "Proyectos");
        panelCentral.add(panelTareas, "Tareas");
        panelCentral.add(panelSprint, "Sprints");

        add(panelCentral, BorderLayout.CENTER);

        // 🔹 Eventos del menú
        btnInicio.addActionListener((ActionEvent e) -> cardLayout.show(panelCentral, "Dashboard"));
        btnKanbanBoard.addActionListener((ActionEvent e) -> cardLayout.show(panelCentral, "Kanban Board"));
        btnProyectos.addActionListener(e -> cardLayout.show(panelCentral, "Proyectos"));
        //btnSprints.addActionListener((ActionEvent e) -> cardLayout.show(panelCentral, "Sprints"));
        btnSalir.addActionListener(e -> {
            int resp = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar sesión?", 
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                dispose();
                System.out.println("Sesión cerrada.");
            }
        });
    }   
    /* Método auxiliar para crear botones del menú */
    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(55, 71, 79));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover (al pasar el mouse)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(69, 90, 100));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(55, 71, 79));
            }
        });
        return btn;
    }
    
    

    
}
