/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import com.mycompany.teamcode_kanbanpro.model.Group;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * 
 * @author salaz
 */
public class CrearProyectoView extends JFrame {
    
    // Componentes del formulario
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<String> cmbGrupos;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // Para almacenar los IDs de los grupos (paralelo al ComboBox)
    private List<Integer> idsGrupos;

    public CrearProyectoView() {
        setTitle("Crear Nuevo Proyecto");
        setSize(480, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        idsGrupos = new ArrayList<>();

        initComponentes();
    }

    private void initComponentes() {
        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(new Color(240, 248, 255));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panelPrincipal.setLayout(new BorderLayout(10, 10));

        // Título
        JLabel lblTitulo = new JLabel("Crear Nuevo Proyecto", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(25, 118, 210));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel del formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        // Campo: Nombre del Proyecto
        JLabel lblNombre = new JLabel("Nombre del Proyecto: *");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(lblNombre, gbc);

        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setPreferredSize(new Dimension(380, 32));
        gbc.gridy = 1;
        panelFormulario.add(txtNombre, gbc);

        // Campo: Descripción
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        panelFormulario.add(lblDescripcion, gbc);

        txtDescripcion = new JTextArea(3, 25);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridy = 3;
        panelFormulario.add(txtDescripcion, gbc);

        // Campo: Grupo
        JLabel lblGrupo = new JLabel("Grupo: *");
        lblGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 4;
        panelFormulario.add(lblGrupo, gbc);

        cmbGrupos = new JComboBox<>();
        cmbGrupos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbGrupos.setPreferredSize(new Dimension(380, 32));
        cmbGrupos.addItem("-- Seleccionar Grupo --");
        gbc.gridy = 5;
        panelFormulario.add(cmbGrupos, gbc);

        // Nota
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        gbc.gridy = 6;
        gbc.insets = new Insets(12, 8, 5, 8);
        panelFormulario.add(lblNota, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
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
        btnGuardar.setBorderPainted(false);
        btnGuardar.setPreferredSize(new Dimension(130, 35));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextArea getTxtDescripcion() {
        return txtDescripcion;
    }

    public JComboBox<String> getCmbGrupos() {
        return cmbGrupos;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    // Carga los grupos en el ComboBox
    public void cargarGrupos(List<Group> grupos) {
        cmbGrupos.removeAllItems();
        idsGrupos.clear();
        
        cmbGrupos.addItem("-- Seleccionar Grupo --");
        idsGrupos.add(-1);

        if (grupos != null) {
            for (Group g : grupos) {
                cmbGrupos.addItem(g.getNombre());
                idsGrupos.add(g.getIdGrupo());
            }
        }
    }

    public int getGrupoSeleccionadoId() {
        int index = cmbGrupos.getSelectedIndex();
        if (index > 0 && index < idsGrupos.size()) {
            return idsGrupos.get(index);
        }
        return -1;
    }

    public boolean tieneGrupoSeleccionado() {
        return cmbGrupos.getSelectedIndex() > 0;
    }

    public void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        cmbGrupos.setSelectedIndex(0);
        txtNombre.requestFocus();
    }
}
