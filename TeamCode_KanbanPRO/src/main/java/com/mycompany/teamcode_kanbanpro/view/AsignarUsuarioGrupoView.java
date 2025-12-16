/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * @author salaz
 */
public class AsignarUsuarioGrupoView extends JFrame {

    private JLabel lblTituloCuadro;
    private JLabel lblGrupo;
    private JComboBox<String> cmbUsuarios;
    private JTable tablaMiembros;
    private DefaultTableModel modeloMiembros;
    
    private JButton btnAgregar;
    private JButton btnQuitar;
    private JButton btnCerrar;
    
    private int idGrupo;
    private String nombreGrupo;

    public AsignarUsuarioGrupoView() {
        setTitle("Asignar Usuarios a Grupo");
        setSize(520, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        initComponentes();
    }

    private void initComponentes() {
        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(240, 248, 255));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        
        lblTituloCuadro = new JLabel("Asignar Usuarios a Grupo", SwingConstants.CENTER);
        lblTituloCuadro.setForeground(new Color(25, 118, 210));
        lblTituloCuadro.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloCuadro.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelPrincipal.add(lblTituloCuadro, BorderLayout.NORTH);

        
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BorderLayout(10, 10));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // --- Panel Superior (Grupo + Selector) ---
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(Color.WHITE);

     
        lblGrupo = new JLabel("Grupo: ");
        lblGrupo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGrupo.setForeground(new Color(25, 118, 210));
        lblGrupo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSuperior.add(lblGrupo);
        panelSuperior.add(Box.createVerticalStrut(8));

        // Separador
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSuperior.add(separator);
        panelSuperior.add(Box.createVerticalStrut(12));

        // Panel selector usuario
        JPanel panelSelector = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelSelector.setBackground(Color.WHITE);
        panelSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lblUsuario = new JLabel("Seleccionar Usuario: ");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbUsuarios = new JComboBox<>();
        cmbUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbUsuarios.setPreferredSize(new Dimension(220, 30));
        cmbUsuarios.addItem("-- Seleccionar Usuario --");

        panelSelector.add(lblUsuario);
        panelSelector.add(Box.createHorizontalStrut(10));
        panelSelector.add(cmbUsuarios);

        panelSuperior.add(panelSelector);
        panelSuperior.add(Box.createVerticalStrut(12));

        // Botón Agregar
        JPanel panelBtnAgregar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelBtnAgregar.setBackground(Color.WHITE);
        panelBtnAgregar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBtnAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnAgregar = new JButton("Agregar al Grupo");
        btnAgregar.setBackground(new Color(56, 142, 60));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAgregar.setFocusPainted(false);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setPreferredSize(new Dimension(150, 32));
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBtnAgregar.add(btnAgregar);
        panelSuperior.add(panelBtnAgregar);
        panelSuperior.add(Box.createVerticalStrut(15));

        // Label Miembros
        JLabel lblMiembros = new JLabel("Miembros Actuales del Grupo:");
        lblMiembros.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMiembros.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSuperior.add(lblMiembros);
        panelSuperior.add(Box.createVerticalStrut(8));

        panelCentral.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de miembros
        String[] columnas = {"ID", "Usuario", "Nombre"};
        modeloMiembros = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMiembros = new JTable(modeloMiembros);
        tablaMiembros.setFillsViewportHeight(true);
        tablaMiembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMiembros.setRowHeight(28);
        tablaMiembros.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaMiembros.setGridColor(new Color(230, 230, 230));
        tablaMiembros.setSelectionBackground(new Color(255, 205, 210));
        tablaMiembros.setSelectionForeground(Color.BLACK);

        // Header
        JTableHeader header = tablaMiembros.getTableHeader();
        header.setBackground(new Color(25, 118, 210));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setReorderingAllowed(false);

        // Centrar columna ID
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaMiembros.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // Anchos
        tablaMiembros.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaMiembros.getColumnModel().getColumn(0).setMaxWidth(60);
        tablaMiembros.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaMiembros.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane scrollMiembros = new JScrollPane(tablaMiembros);
        scrollMiembros.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        scrollMiembros.setPreferredSize(new Dimension(450, 160));

        panelCentral.add(scrollMiembros, BorderLayout.CENTER);

        //Boton quitar
        JPanel panelBtnQuitar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panelBtnQuitar.setBackground(Color.WHITE);

        btnQuitar = new JButton("Quitar del Grupo");
        btnQuitar.setBackground(new Color(198, 40, 40));
        btnQuitar.setForeground(Color.WHITE);
        btnQuitar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuitar.setFocusPainted(false);
        btnQuitar.setBorderPainted(false);
        btnQuitar.setPreferredSize(new Dimension(150, 32));
        btnQuitar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBtnQuitar.add(btnQuitar);
        panelCentral.add(panelBtnQuitar, BorderLayout.SOUTH);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        // PanelInferior - Cerrar
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(new Color(240, 248, 255));

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCerrar.setPreferredSize(new Dimension(100, 35));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBotones.add(btnCerrar);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    public JComboBox<String> getCmbUsuarios() {
        return cmbUsuarios;
    }

    public JTable getTablaMiembros() {
        return tablaMiembros;
    }

    public DefaultTableModel getModeloMiembros() {
        return modeloMiembros;
    }

    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    public JButton getBtnQuitar() {
        return btnQuitar;
    }

    public JButton getBtnCerrar() {
        return btnCerrar;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }


    public void setGrupo(int idGrupo, String nombreGrupo) {
        this.idGrupo = idGrupo;
        this.nombreGrupo = nombreGrupo;
        lblGrupo.setText("Grupo: " + nombreGrupo);
        setTitle("Asignar Usuarios - " + nombreGrupo);
    }

    public void setTituloConGrupo(String nombreGrupo) {
        setTitle("Asignar Usuarios - " + nombreGrupo);
        lblTituloCuadro.setText("Gestionar Miembros: " + nombreGrupo);
        lblGrupo.setText("Grupo: " + nombreGrupo);
    }
}

