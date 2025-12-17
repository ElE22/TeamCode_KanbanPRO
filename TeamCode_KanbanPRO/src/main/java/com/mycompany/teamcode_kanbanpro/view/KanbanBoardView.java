/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import com.mycompany.teamcode_kanbanpro.controller.KanbanBoardController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Emanuel
 */

public class KanbanBoardView extends JFrame implements DragGestureListener, DragSourceListener {
    
    private KanbanColumnPanel backlogColumn;
    private KanbanColumnPanel inProgressColumn;
    private KanbanColumnPanel reviewColumn;
    private KanbanColumnPanel doneColumn;
    private JButton createTaskButton;
    private KanbanBoardController controller;
    private final Map<String, KanbanColumnPanel> columnsMap = new HashMap<>();
    private JPanel boardPanel;
    private JLabel titleLabel; 
    private JPanel topPanel;
    
    public KanbanBoardView() {
        initializeFrame();
        createTopPanel();
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private void createTopPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel superior con titulo y boton
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(41, 128, 185));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        titleLabel = new JLabel("");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        createTaskButton = new JButton("+ Nueva Tarea");
        createTaskButton.setFont(new Font("Arial", Font.BOLD, 13));
        createTaskButton.setForeground(Color.WHITE);
        createTaskButton.setBackground(new Color(230, 126, 34));
        createTaskButton.setFocusPainted(false);
        createTaskButton.setBorderPainted(false);
        createTaskButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createTaskButton.setPreferredSize(new Dimension(150, 38));
        topPanel.add(createTaskButton, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Agregar el panel de columnas
        boardPanel = new JPanel();
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }

   public void addColumns(KanbanColumnPanel columnPanel) {
       this.boardPanel.add(columnPanel);
       // Registrar en el map 
       columnsMap.put(columnPanel.getColumnName(), columnPanel);
   }
    public void setLayoutBoard(int columns){
        boardPanel.setLayout(new GridLayout(1, columns, 10, 10));
    }
    
    public KanbanColumnPanel findColumnById(int columnId) {
        for (KanbanColumnPanel columnPanel : columnsMap.values()) {
            if (columnPanel.getColumnData().getIdColumna() == columnId) {
                return columnPanel;
            }
        }
        return null;
    }
    
    public KanbanTaskPanel findTaskPanelById(int taskId) {
        // Buscar tarea en todas las columnas
        for (KanbanColumnPanel columnPanel : columnsMap.values()) {
            // Usar el nuevo metodo de la columna para buscar la tarea por id
            KanbanTaskPanel taskPanel = columnPanel.getTaskPanel(taskId);
            if (taskPanel != null) return taskPanel;
        }
        return null;
    }
    
    public Image getComponentDragImage(JComponent component) {

        component.revalidate();
        component.doLayout();
        component.validate();

        Dimension size = component.getPreferredSize();

        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB_PRE);

        Graphics2D g2d = image.createGraphics();
        g2d.setComposite(AlphaComposite.SrcOver.derive(0.7f));
        component.setSize(size);
        component.paint(g2d);
        g2d.dispose();
        return image;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        Component component = dge.getComponent();

        // Si el drag viene de un componente hijo, buscar el KanbanTaskPanel padre
        KanbanTaskPanel taskPanel;
        if (component instanceof KanbanTaskPanel) {
            taskPanel = (KanbanTaskPanel) component;
        } else {
            taskPanel = (KanbanTaskPanel) SwingUtilities.getAncestorOfClass(KanbanTaskPanel.class, component);
        }

        if (taskPanel == null) {
            return;
        }

        Transferable t = ((TaskTransferHandler) taskPanel.getTransferHandler()).createTransferable(taskPanel);

        if (t == null)
            return;

        Image dragImage = getComponentDragImage(taskPanel);
        Point dragOffset = dge.getDragOrigin();

        try {
            dge.startDrag(
                    Cursor.getDefaultCursor(),
                    dragImage,
                    dragOffset,
                    t,
                    this);
        } catch (Exception ex) {
            System.err.println("Error al iniciar el arrastre: " + ex.getMessage());
        }
    }

    @Override public void dragEnter(DragSourceDragEvent dsde) {}
    @Override public void dragOver(DragSourceDragEvent dsde) {}
    @Override public void dropActionChanged(DragSourceDragEvent dsde) {}
    @Override public void dragExit(DragSourceEvent dse) {}
    @Override public void dragDropEnd(DragSourceDropEvent dsde) {}

    // --- Getters para acceso desde el controller ---
    
    public KanbanColumnPanel getBacklogColumn() {
        return backlogColumn;
    }

    public KanbanColumnPanel getInProgressColumn() {
        return inProgressColumn;
    }

    public KanbanColumnPanel getReviewColumn() {
        return reviewColumn;
    }

    public KanbanColumnPanel getDoneColumn() {
        return doneColumn;
    }

    public JButton getCreateTaskButton() {
        return createTaskButton;
    }
    
    public void setController(KanbanBoardController controller) {
        this.controller = controller;
    }
    
    public KanbanBoardController getController() {
        return controller;
    }
    
    public KanbanColumnPanel findColumnByName(String columnName) {
        return columnsMap.get(columnName);
    }

    public void setTitleLabel(String title) {
        this.titleLabel.setText(title);
    }

    public JPanel getTopPanel() {
        return topPanel;
    }

}