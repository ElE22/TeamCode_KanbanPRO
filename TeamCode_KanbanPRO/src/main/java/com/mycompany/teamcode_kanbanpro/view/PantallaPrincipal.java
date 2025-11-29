/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
/**
 *
 * @author salaz
 */
public class PantallaPrincipal extends JFrame {
    // Componentes principales
    private JPanel barraSuperior;
    private JPanel menuLateral;
    private JPanel panelContenido;

    private JLabel lblTituloApp;
    private JLabel lblUsuario;
    private JButton btnInicio;
    private JButton btnTareas;
    private JButton btnProyectos;
    private JButton btnSprints;
    private JButton btnBacklog;
    private JButton btnSalir;

    public PantallaPrincipal(String nombreUsuario, String rolUsuario) {
        // Título de la ventana
        setTitle("Dashboard - Pizarra Kanban");
        // Tamaño de la ventana
        setSize(1000, 600);
        // Centra la ventana en pantalla
        setLocationRelativeTo(null);
        // Cierra el programa al cerrar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Usaremos BorderLayout para dividir en regiones (NORTH, WEST, CENTER)
        setLayout(new BorderLayout());

        // Inicializamos los componentes visuales
        inicializarComponentes(nombreUsuario, rolUsuario);

        // Hace visible la ventana
        setVisible(true);
    }

    private void inicializarComponentes(String nombreUsuario, String rolUsuario) {

        /* ---------- BARRA SUPERIOR ---------- */
        barraSuperior = new JPanel();
        barraSuperior.setBackground(new Color(30, 136, 229)); // Azul moderno
        barraSuperior.setLayout(new BorderLayout());
        barraSuperior.setPreferredSize(new Dimension(0, 50)); // Altura de 50 px

        lblTituloApp = new JLabel("Kanban System", SwingConstants.LEFT);
        lblTituloApp.setForeground(Color.WHITE);
        lblTituloApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloApp.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); // margen izquierdo

        lblUsuario = new JLabel(nombreUsuario + " (" + rolUsuario + ")", SwingConstants.RIGHT);
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); // margen derecho

        barraSuperior.add(lblTituloApp, BorderLayout.WEST);
        barraSuperior.add(lblUsuario, BorderLayout.EAST);

        /* ---------- MENÚ LATERAL ---------- */
        menuLateral = new JPanel();
        menuLateral.setBackground(new Color(38, 50, 56)); // Gris oscuro
        menuLateral.setLayout(new GridLayout(7, 1, 0, 5));
        menuLateral.setPreferredSize(new Dimension(180, 0));

        btnInicio = crearBotonMenu("Inicio");
        btnTareas = crearBotonMenu("Tareas");
        btnProyectos = crearBotonMenu("Proyectos");
        btnSprints = crearBotonMenu("Sprints");
        btnBacklog = crearBotonMenu("Backlog");
        btnSalir = crearBotonMenu("🚪Cerrar Sesión");

        menuLateral.add(btnInicio);
        menuLateral.add(btnTareas);
        menuLateral.add(btnProyectos);
        menuLateral.add(btnSprints);
        menuLateral.add(btnBacklog);
        menuLateral.add(btnSalir);

        /* ---------- PANEL DE CONTENIDO ---------- */
        panelContenido = new JPanel();
        panelContenido.setBackground(new Color(250, 250, 250));
        panelContenido.setLayout(null); // diseño absoluto (usaremos coordenadas)
        
        JLabel lblBienvenida = new JLabel("Bienvenido al Panel Principal de la Pizarra Kanban");
        lblBienvenida.setBounds(250, 100, 600, 40);
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBienvenida.setForeground(new Color(69, 90, 100));
        panelContenido.add(lblBienvenida);

        // "Tarjetas" o botones grandes tipo acceso rápido
        JButton cardKanban = crearTarjeta("Pizarra Kanban", new Color(187, 222, 251), 250, 180);
        JButton cardBacklog = crearTarjeta("Ver Backlog", new Color(200, 230, 201), 550, 180);
        JButton cardProyecto = crearTarjeta("Crear Proyecto", new Color(255, 249, 196), 250, 300);
        JButton cardSprint = crearTarjeta("Ver Sprints", new Color(255, 224, 178), 550, 300);

        panelContenido.add(cardKanban);
        panelContenido.add(cardBacklog);
        panelContenido.add(cardProyecto);
        panelContenido.add(cardSprint);

        /* ---------- AGREGAR TODO A LA VENTANA ---------- */
        add(barraSuperior, BorderLayout.NORTH);
        add(menuLateral, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        /* ---------- EVENTOS ---------- */
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
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover (al pasar el mouse)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(69, 90, 100));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(55, 71, 79));
            }
        });
        return btn;
    }

    /* Método auxiliar para crear "tarjetas" coloridas en el contenido */
    private JButton crearTarjeta(String titulo, Color colorFondo, int x, int y) {
        JButton tarjeta = new JButton(titulo);
        tarjeta.setBounds(x, y, 250, 80);
        tarjeta.setBackground(colorFondo);
        tarjeta.setForeground(new Color(38, 50, 56));
        tarjeta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tarjeta.setFocusPainted(false);
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(176, 190, 197), 2, true));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return tarjeta;
    }
}
