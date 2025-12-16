/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author salaz
 */
public class PantallaPrincipal extends JFrame {
    private JPanel panelCentral;
    private CardLayout cardLayout;
    private ProyectosView panelProyectos;
    private GrupoView panelGrupos;
    
    JLabel lblUsuario;
    private JButton btnProyectos;

    private JButton btnGrupos;
    private JButton btnSalir;
    
    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(panelCentral, nombrePanel);
    }

    public JButton getBtnProyectos() {
        return btnProyectos;
    }


    public JButton getBtnGrupos() {
        return btnGrupos;
    }

    public JButton getBtnSalir() {
        return btnSalir;
    }

    public ProyectosView getPanelProyectos() {
        return panelProyectos;
    }

    // === NUEVA INTEGRACIÓN: Getter ===
    public GrupoView getPanelGrupos() {
        return panelGrupos;
    }

    public JLabel getLblUsuario() {
        return lblUsuario;
    }

    public void setLblUsuario(String lblUsuario) {
        this.lblUsuario.setText(lblUsuario);
    }

    public PantallaPrincipal() {
        setTitle("KanbanPro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);

        // Barra superior
        JPanel barraSuperior = new JPanel();
        barraSuperior.setBackground(new Color(25, 118, 210));
        barraSuperior.setLayout(new BorderLayout());
        barraSuperior.setPreferredSize(new Dimension(0, 50));
        add(barraSuperior, BorderLayout.NORTH);
        
        lblUsuario = new JLabel("");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        barraSuperior.add(lblUsuario, BorderLayout.EAST);
        
        JLabel lblTituloApp = new JLabel("KanbanPro");
        lblTituloApp.setForeground(Color.WHITE);
        lblTituloApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloApp.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        barraSuperior.add(lblTituloApp, BorderLayout.WEST);

        // menu lateral
        JPanel menuLateral = new JPanel();

        menuLateral.setLayout(new GridLayout(6, 1, 0, 10));
        menuLateral.setBackground(new Color(38, 50, 56));
        menuLateral.setPreferredSize(new Dimension(180, 0));
        menuLateral.setBorder(BorderFactory.createEmptyBorder(80, 20, 80, 20));
        btnProyectos = crearBotonMenu("Proyectos");
       
        btnGrupos = crearBotonMenu("Grupos");
        btnSalir = crearBotonMenu("Cerrar Sesión");
        menuLateral.add(btnProyectos);
       
        menuLateral.add(btnGrupos);
        menuLateral.add(btnSalir);
        
        add(menuLateral, BorderLayout.WEST);

        // panel central dinamico
        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);


        panelProyectos = new ProyectosView();
        
        // === NUEVA INTEGRACIÓN: Crear panel de grupos ===
        panelGrupos = new GrupoView();

        JPanel panelTareas = new JPanel();
        panelTareas.add(new JLabel("Gestión de Tareas"));

        // añadimos los paneles al CardLayout
        panelCentral.add(panelProyectos, "Proyectos");
        panelCentral.add(panelTareas, "Tareas");
        // === NUEVA INTEGRACIÓN: Registrar panel de grupos ===
        panelCentral.add(panelGrupos, "Grupos");

        add(panelCentral, BorderLayout.CENTER);
    }
    
    /* metodo auxiliar para crear botones del menu */
    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(55, 71, 79));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // efecto hover (al pasar el mouse)
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