/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 *
 * @author salaz
 */
public class CrearProyectoView extends JFrame {
    // Componentes del formulario

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JList<String> listGruposDisponibles;
    private JList<String> listGruposAsignados;
    private DefaultListModel<String> modelGruposDisponibles;
    private DefaultListModel<String> modelGruposAsignados;
    private JButton btnAgregarGrupo;
    private JButton btnQuitarGrupo;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // Para almacenar los IDs de los grupos (paralelo a los nombres)
    private java.util.List<Integer> idsGruposDisponibles;
    private java.util.List<Integer> idsGruposAsignados;

    public CrearProyectoView() {
        setTitle("Crear Nuevo Proyecto");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        idsGruposDisponibles = new java.util.ArrayList<>();
        idsGruposAsignados = new java.util.ArrayList<>();

        initComponentes();
    }

    private void initComponentes() {
        // Panel principal con padding
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(new Color(240, 248, 255));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panelPrincipal.setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Crear Nuevo Proyecto", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(25, 118, 210));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

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

        JLabel lblNombre = new JLabel("Nombre del Proyecto: *");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelFormulario.add(lblNombre, gbc);

        txtNombre = new JTextField(30);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setToolTipText("Ingrese un nombre único para el proyecto");
        gbc.gridy = 1;
        panelFormulario.add(txtNombre, gbc);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        panelFormulario.add(lblDescripcion, gbc);

        txtDescripcion = new JTextArea(3, 30);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setToolTipText("Descripción opcional del proyecto");
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        gbc.gridy = 3;
        panelFormulario.add(scrollDescripcion, gbc);

        JLabel lblGrupos = new JLabel("Asignar Grupos al Proyecto: *");
        lblGrupos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGrupos.setForeground(new Color(25, 118, 210));
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 8, 8, 8);
        panelFormulario.add(lblGrupos, gbc);
        gbc.insets = new Insets(8, 8, 8, 8);

        JPanel panelGrupos = new JPanel(new GridBagLayout());
        panelGrupos.setBackground(Color.WHITE);
        GridBagConstraints gbcG = new GridBagConstraints();
        gbcG.insets = new Insets(5, 5, 5, 5);

        JLabel lblDisponibles = new JLabel("Grupos Disponibles:");
        lblDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbcG.gridx = 0;
        gbcG.gridy = 0;
        panelGrupos.add(lblDisponibles, gbcG);

        modelGruposDisponibles = new DefaultListModel<>();
        listGruposDisponibles = new JList<>(modelGruposDisponibles);
        listGruposDisponibles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listGruposDisponibles.setVisibleRowCount(5);
        listGruposDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane scrollDisponibles = new JScrollPane(listGruposDisponibles);
        scrollDisponibles.setPreferredSize(new Dimension(180, 100));
        gbcG.gridy = 1;
        panelGrupos.add(scrollDisponibles, gbcG);

        JPanel panelBotonesGrupos = new JPanel(new GridLayout(2, 1, 5, 10));
        panelBotonesGrupos.setBackground(Color.WHITE);

        btnAgregarGrupo = new JButton(">>");
        btnAgregarGrupo.setToolTipText("Agregar grupo(s) seleccionado(s)");
        btnAgregarGrupo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAgregarGrupo.setPreferredSize(new Dimension(60, 30));

        btnQuitarGrupo = new JButton("<<");
        btnQuitarGrupo.setToolTipText("Quitar grupo(s) seleccionado(s)");
        btnQuitarGrupo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuitarGrupo.setPreferredSize(new Dimension(60, 30));

        panelBotonesGrupos.add(btnAgregarGrupo);
        panelBotonesGrupos.add(btnQuitarGrupo);

        gbcG.gridx = 1;
        gbcG.gridy = 1;
        panelGrupos.add(panelBotonesGrupos, gbcG);

        // Lista de grupos asignados
        JLabel lblAsignados = new JLabel("Grupos Asignados:");
        lblAsignados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbcG.gridx = 2;
        gbcG.gridy = 0;
        panelGrupos.add(lblAsignados, gbcG);

        modelGruposAsignados = new DefaultListModel<>();
        listGruposAsignados = new JList<>(modelGruposAsignados);
        listGruposAsignados.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listGruposAsignados.setVisibleRowCount(5);
        listGruposAsignados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane scrollAsignados = new JScrollPane(listGruposAsignados);
        scrollAsignados.setPreferredSize(new Dimension(180, 100));
        gbcG.gridy = 1;
        panelGrupos.add(scrollAsignados, gbcG);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        panelFormulario.add(panelGrupos, gbc);

        // Nota sobre campos obligatorios
        JLabel lblNota = new JLabel("* Campos obligatorios. Debe asignar al menos un grupo.");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(lblNota, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        //PANEL DE BOTONES 
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBotones.setBackground(new Color(240, 248, 255));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGuardar = new JButton("Crear Proyecto");
        btnGuardar.setBackground(new Color(25, 118, 210));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(140, 35));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);

        // Configurar eventos internos de mover grupos
        //  configurarEventosGrupos();
    }

    /*
    // Configura los eventos para mover grupos entre listas
     
    private void configurarEventosGrupos() {
        // Agregar grupos seleccionados
        btnAgregarGrupo.addActionListener(e -> {
            int[] indices = listGruposDisponibles.getSelectedIndices();
            // Recorrer en orden inverso para no afectar los índices
            for (int i = indices.length - 1; i >= 0; i--) {
                int idx = indices[i];
                String nombre = modelGruposDisponibles.get(idx);
                int id = idsGruposDisponibles.get(idx);
                
                // Mover a asignados
                modelGruposAsignados.addElement(nombre);
                idsGruposAsignados.add(id);
                
                // Quitar de disponibles
                modelGruposDisponibles.remove(idx);
                idsGruposDisponibles.remove(idx);
            }
        });
     */
    // Quitar grupos seleccionados
    /*
        btnQuitarGrupo.addActionListener(e -> {
            int[] indices = listGruposAsignados.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                int idx = indices[i];
                String nombre = modelGruposAsignados.get(idx);
                int id = idsGruposAsignados.get(idx);
                
                // Mover a disponibles
                modelGruposDisponibles.addElement(nombre);
                idsGruposDisponibles.add(id);
                
                // Quitar de asignados
                modelGruposAsignados.remove(idx);
                idsGruposAsignados.remove(idx);
            }
        });
     */
    // Doble clic para agregar
    /*
        listGruposDisponibles.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnAgregarGrupo.doClick();
                }
            }
        });
        
        // Doble clic para quitar
        listGruposAsignados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnQuitarGrupo.doClick();
                }
            }
        });
    }
    */
    public JTextField getTxtNombre() { 
        return txtNombre; 
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
    /*
    public DefaultListModel<String> getModelGruposDisponibles() {
        return modelGruposDisponibles;
    }
    
    public DefaultListModel<String> getModelGruposAsignados() {
        return modelGruposAsignados;
    }
    
    public java.util.List<Integer> getIdsGruposDisponibles() {
        return idsGruposDisponibles;
    }
    
    public java.util.List<Integer> getIdsGruposAsignados() {
        return idsGruposAsignados;
    }
     */
 /*
    public void cargarGruposDisponibles(List<com.mycompany.teamcode_kanbanpro.model.Group> grupos) {
        modelGruposDisponibles.clear();
        idsGruposDisponibles.clear();
        modelGruposAsignados.clear();
        idsGruposAsignados.clear();
        
        if (grupos != null) {
            for (com.mycompany.teamcode_kanbanpro.model.Group g : grupos) {
                modelGruposDisponibles.addElement(g.getNombre());
                idsGruposDisponibles.add(g.getIdGrupo());
            }
        }
    }

    /*
    public java.util.List<Integer> getGruposAsignadosIds() {
        return new java.util.ArrayList<>(idsGruposAsignados);
    }
    
     */
    public void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        // Mover todos los grupos asignados de vuelta a disponibles
        while (modelGruposAsignados.size() > 0) {
            String nombre = modelGruposAsignados.get(0);
            int id = idsGruposAsignados.get(0);
            modelGruposDisponibles.addElement(nombre);
            idsGruposDisponibles.add(id);
            modelGruposAsignados.remove(0);
            idsGruposAsignados.remove(0);
        }
        txtNombre.requestFocus();
    }
}
/**
 * Verifica si hay al menos un grupo asignado
 */
/*
    public boolean tieneGruposAsignados() {
        return !idsGruposAsignados.isEmpty();
    }
 */
