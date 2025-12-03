/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import com.mycompany.teamcode_kanbanpro.model.Task;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
/**
 *
 * @author Emanuel
 */

public class KanbanTaskPanel extends JPanel {
    private final Task taskData;
    private final KanbanBoardView parentView;

    public KanbanTaskPanel(Task tData, KanbanBoardView parentView) {
        this.taskData = tData;
        this.parentView = parentView;
        
        initializePanel();
        createTaskComponents();
        setupDragAndDrop();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(8, 8));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        setPreferredSize(new Dimension(250, 122));
        
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getPriorityColor(this.taskData.getNombrePrioridad()), 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        setBackground(new Color(248, 249, 250));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void createTaskComponents() {
        // Titulo
        JLabel titleLabel = new JLabel("<html><b>" + this.taskData.getTitulo() + "</b></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(new Color(40, 40, 40));
        add(titleLabel, BorderLayout.NORTH);

        // Descripcion en el centro
        JTextArea descArea = createDescriptionArea();
        add(descArea, BorderLayout.CENTER);

        // Panel inferior con prioridad y grupos
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JTextArea createDescriptionArea() {
        JTextArea descArea = new JTextArea(this.taskData.getDescripcion());
        descArea.setFont(new Font("Arial", Font.PLAIN, 11));
        descArea.setForeground(new Color(90, 90, 90));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFocusable(false);
        descArea.setBorder(new EmptyBorder(2, 0, 2, 0));
        
        // Registrar drag & drop también en el JTextArea
        registerDragGesture(descArea);
        
        return descArea;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        JLabel priorityLabel = new JLabel("Prioridad: " + this.taskData.getNombrePrioridad());
        priorityLabel.setFont(new Font("Arial", Font.BOLD, 10));
        priorityLabel.setForeground(new Color(100, 100, 100));

        JLabel groupsLabel = createGroupsLabel();

        bottomPanel.add(priorityLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        bottomPanel.add(groupsLabel);

        return bottomPanel;
    }

    private JLabel createGroupsLabel() {
        JLabel groupsLabel = new JLabel(" " + this.taskData.getGruposAsignados() + " ");
        groupsLabel.setBackground(new Color(220, 240, 230)); 
        groupsLabel.setForeground(new Color(60, 100, 80));  
        groupsLabel.setBorder(new EmptyBorder(3, 8, 3, 8));
        groupsLabel.setOpaque(true);
        groupsLabel.setFont(new Font("Arial", Font.BOLD, 10));
        
        return groupsLabel;
    }

    private void setupDragAndDrop() {
        setTransferHandler(new TaskTransferHandler());
        
        // Registrar el drag gesture en el panel principal
        registerDragGesture(this);
    }

    private void registerDragGesture(Component component) {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
            component,
            DnDConstants.ACTION_MOVE,
            parentView  // El listener está en KanbanBoardView
        );
    }

    private Color getPriorityColor(String priority) {
        return switch (priority) {
            case "ALTA" -> new Color(255, 64, 129);   // Rojo/Rosa
            case "MEDIA" -> new Color(255, 179, 0);   // Naranja/Amarillo
            case "BAJA" -> new Color(30, 136, 229);   // Azul
            default -> Color.LIGHT_GRAY;
        };
    }

    public Task getTaskData() {
        return taskData;
    }
    
    public String getTitle() {
         return taskData.getTitulo();
    }

    //Obtiene la columna padre donde esta esta tarea
    public KanbanColumnPanel getParentColumn() {
        Container parent = getParent();
        if (parent instanceof KanbanColumnPanel) {
            return (KanbanColumnPanel) parent;
        }
        return null;
    }
}
