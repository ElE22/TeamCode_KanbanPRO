/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
/**
 *
 * @author Emanuel
 */
public class KanbanColumnPanel extends JPanel {
    
    private final String columnName;
    private final Color headerColor;
    private final KanbanBoardView parentView;

    public KanbanColumnPanel(String columnName, Color headerColor, KanbanBoardView parentView) {
        this.columnName = columnName;
        this.headerColor = headerColor;
        this.parentView = parentView;
        
        initializePanel();
        createHeader();
        setupTransferHandler();
    }

    private void initializePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        setBackground(new Color(250, 250, 250));
    }

    private void createHeader() {
        JLabel titleLabel = new JLabel(columnName, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(8, 5, 8, 5));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(headerColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(Box.createVerticalGlue()); // Espacio flexible al final
    }

    private void setupTransferHandler() {
        setTransferHandler(new ColumnTransferHandler(this, parentView));
    }

    /**
     * Agrega una tarea a la columna
     */
    public void addTask(KanbanTaskPanel taskPanel) {
        // Remover el glue temporal
        Component glue = getComponent(getComponentCount() - 1);
        remove(glue);
        
        // Agregar la tarea
        taskPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(taskPanel);
        
        // Volver a agregar el glue
        add(glue);
        
        revalidate();
        repaint();
    }

    /**
     * Remueve una tarea de la columna
     */
    public void removeTask(KanbanTaskPanel taskPanel) {
        Component[] components = getComponents();
        int taskIndex = -1;
        
        // Buscar el indice de la tarea
        for (int i = 0; i < components.length; i++) {
            if (components[i] == taskPanel) {
                taskIndex = i;
                break;
            }
        }

        if (taskIndex != -1) {
            // Remover el espaciador antes de la tarea si existe
            if (taskIndex > 0 && components[taskIndex - 1] instanceof Box.Filler) {
                remove(taskIndex - 1);
            }
            // Remover la tarea
            remove(taskPanel);
            
            revalidate();
            repaint();
        }
    }

    public String getColumnName() {
        return columnName;
    }

    //Verifica si una tarea ya está en esta columna
    public boolean containsTask(KanbanTaskPanel taskPanel) {
        return taskPanel.getParent() == this;
    }
}
