/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author salaz
 */
public class GrupoView extends JPanel{
    private JButton btnCrearGrupo;
    private JButton btnUnirAGrupo;
    private JTable tablaGrupos;
    private JPanel panelTabla;
    private JPanel panelBotones;
    
  
    private DefaultTableModel modeloGrupos;


    public GrupoView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 248, 255));
        
        initComponentes();
    }
    
    private void initComponentes() {
        
    
        JLabel lblTitulo = new JLabel("Gestión de Grupos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(25, 118, 210));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);
        
        //Panel Central
        panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
      
        String[] columnasGrupos = {"ID", "Nombre", "Descripción", "Miembros"};
        
    
        modeloGrupos = new DefaultTableModel(columnasGrupos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Configuración de la tabla
        tablaGrupos = new JTable(modeloGrupos);
        tablaGrupos.setFillsViewportHeight(true);
        tablaGrupos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaGrupos.setSelectionBackground(new Color(187, 222, 251));
        tablaGrupos.setSelectionForeground(Color.BLACK);
        tablaGrupos.setRowHeight(28);
        tablaGrupos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaGrupos.setGridColor(new Color(230, 230, 230));
        tablaGrupos.setShowVerticalLines(true);
        tablaGrupos.setShowHorizontalLines(true);
        
      
        JTableHeader header = tablaGrupos.getTableHeader();
        header.setBackground(new Color(25, 118, 210));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setReorderingAllowed(false);
        
     
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaGrupos.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaGrupos.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
     
        tablaGrupos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaGrupos.getColumnModel().getColumn(0).setMaxWidth(60);
        tablaGrupos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaGrupos.getColumnModel().getColumn(2).setPreferredWidth(300);
        tablaGrupos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaGrupos.getColumnModel().getColumn(3).setMaxWidth(100);
        
        // ScrollPane para la tabla
        JScrollPane scrollGrupos = new JScrollPane(tablaGrupos);
        scrollGrupos.setBorder(BorderFactory.createEmptyBorder());
        scrollGrupos.getViewport().setBackground(Color.WHITE);
        
        panelTabla.add(scrollGrupos, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);
        
        // Panel Botones
        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setOpaque(false);
        
        // Botón Crear Grupo
        btnCrearGrupo = new JButton("Crear Grupo");
        btnCrearGrupo.setBackground(new Color(25, 118, 210));
        btnCrearGrupo.setForeground(Color.WHITE);
        btnCrearGrupo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCrearGrupo.setFocusPainted(false);
        btnCrearGrupo.setBorderPainted(false);
        btnCrearGrupo.setPreferredSize(new Dimension(150, 40));
        btnCrearGrupo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Botón Asignar Usuarios
        btnUnirAGrupo = new JButton("Asignar Usuarios");
        btnUnirAGrupo.setBackground(new Color(56, 142, 60));
        btnUnirAGrupo.setForeground(Color.WHITE);
        btnUnirAGrupo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUnirAGrupo.setFocusPainted(false);
        btnUnirAGrupo.setBorderPainted(false);
        btnUnirAGrupo.setPreferredSize(new Dimension(170, 40));
        btnUnirAGrupo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panelBotones.add(btnCrearGrupo);
        panelBotones.add(btnUnirAGrupo);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    

    
    public JTable getTablaGrupos() {
        return tablaGrupos;
    }
    
    public DefaultTableModel getModeloGrupos() {
        return modeloGrupos;
    }
    
    public JButton getBtnCrearGrupo() {
        return btnCrearGrupo;
    }
    
    public JButton getBtnUnirAGrupo() {
        return btnUnirAGrupo;
    }
    
     public JPanel getPanelTabla() {
        return panelTabla;
    }

    public JPanel getPanelBotones() {
        return panelBotones;
    }
    
    //Obtiene el ID del proyecto seleccionado en la tabla
    public int getIdGrupoSeleccionado() {
        int filaSeleccionada = tablaGrupos.getSelectedRow();
        if (filaSeleccionada != -1) {
            return (int) modeloGrupos.getValueAt(filaSeleccionada, 0);
        }
        return -1;
    }
}

