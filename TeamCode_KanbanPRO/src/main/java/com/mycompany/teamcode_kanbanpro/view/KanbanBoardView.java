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
    private JPanel kanbanColumnsPanel;
    public KanbanBoardView() {
        initializeFrame();
        createTopPanel();
        createKanbanBoardLayout();
    }

    private void initializeFrame() {
        setTitle("Pizarra Kanban - Proyecto Actual");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private void createTopPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel superior con título y botón
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Pizarra Kanban");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 50));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        createTaskButton = new JButton("+ Nueva Tarea");
        createTaskButton.setFont(new Font("Arial", Font.BOLD, 13));
        createTaskButton.setForeground(Color.WHITE);
        createTaskButton.setBackground(new Color(100, 149, 237));
        createTaskButton.setFocusPainted(false);
        createTaskButton.setBorderPainted(false);
        createTaskButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createTaskButton.setPreferredSize(new Dimension(150, 38));
        topPanel.add(createTaskButton, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Agregar el panel de columnas
        JPanel boardPanel = createBoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return boardPanel;
    }

//    private void createKanbanBoard() {
//        JPanel boardPanel = (JPanel) ((JPanel) getContentPane()).getComponent(1);
//        
//        // Crear columnas con colores pasteles
//        backlogColumn = new KanbanColumnPanel("Backlog", new Color(255, 214, 230), this);
//        inProgressColumn = new KanbanColumnPanel("In Progress", new Color(200, 230, 201), this);
//        reviewColumn = new KanbanColumnPanel("Review", new Color(187, 222, 251), this);
//        doneColumn = new KanbanColumnPanel("Done", new Color(225, 190, 231), this);
//
//        // Agregar tareas de ejemplo
//        addExampleTasks();
//
//        // Agregar columnas al tablero
//        boardPanel.add(backlogColumn);
//        boardPanel.add(inProgressColumn);
//        boardPanel.add(reviewColumn);
//        boardPanel.add(doneColumn);
//    }
//
//    private void addExampleTasks() {
//        backlogColumn.addTask(new KanbanTaskPanel(
//            "Implementar Login", 
//            "ALTA", 
//            "Backend, Frontend", 
//            "Sistema de autenticación de usuarios con JWT",
//            this
//        ));
//        
//        backlogColumn.addTask(new KanbanTaskPanel(
//            "Diseño de la Pizarra", 
//            "MEDIA", 
//            "Frontend, Diseño", 
//            "Crear mockups y prototipos de la interfaz Kanban",
//            this
//        ));
//        
//        inProgressColumn.addTask(new KanbanTaskPanel(
//            "Revisar Diagrama E-R", 
//            "BAJA", 
//            "Backend, Logística", 
//            "Validar relaciones y normalización de base de datos",
//            this
//        ));
//    }
    
    private void createKanbanBoardLayout() {
        // Inicializar el panel con un layout horizontal (Box o Flow)
        kanbanColumnsPanel = new JPanel();
        // Usamos BoxLayout para que las columnas se apilen horizontalmente
        kanbanColumnsPanel.setLayout(new BoxLayout(kanbanColumnsPanel, BoxLayout.X_AXIS)); 
        
        // El panel de columnas se envuelve en un JScrollPane para el scroll horizontal
        JScrollPane scrollPane = new JScrollPane(kanbanColumnsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
        
        // Agregar al JFrame (asumiendo que es el centro del layout principal)
        getContentPane().add(scrollPane, BorderLayout.CENTER); 
    }

    // Callback para el botón de crear tarea
    
    
    public void addColumn(KanbanColumnPanel columnPanel) {
        kanbanColumnsPanel.add(columnPanel); 
        columnsMap.put(columnPanel.getColumnName(), columnPanel); 
        // Refrescar el layout después de añadir
        kanbanColumnsPanel.revalidate();
        kanbanColumnsPanel.repaint();
    }

    // Método para notificar cuando se mueve una tarea
    public void onTaskMoved(String taskTitle, String newColumnName) {
        System.out.println("NOTIFICACION: la tarea '" + taskTitle + "' se movio a: " + newColumnName);
        // Aquí se conectará con el controller
    }

    // --- Métodos para Drag & Drop ---
    
    public Image getComponentDragImage(JComponent component) {
        Dimension size = component.getSize();
        if (size.width <= 0 || size.height <= 0) {
            size = component.getPreferredSize();
        }
        
        BufferedImage image = new BufferedImage(
            size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = image.createGraphics();
        g2d.setComposite(AlphaComposite.SrcOver.derive(0.7f)); 
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
            taskPanel = (KanbanTaskPanel) SwingUtilities.getAncestorOfClass(
                KanbanTaskPanel.class, component);
        }
        
        if (taskPanel == null) return;
        
        // Obtener el transferable usando el método público del TaskTransferHandler
        TaskTransferHandler handler = (TaskTransferHandler) taskPanel.getTransferHandler();
        Transferable transferable = handler.createTransferablePublic(taskPanel);
        
        if (transferable == null) return;
        
        Image dragImage = getComponentDragImage(taskPanel);
        Point dragOffset = dge.getDragOrigin();
        
        try {
            dge.startDrag(
                Cursor.getDefaultCursor(),
                dragImage,
                dragOffset,
                transferable,
                this
            );
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KanbanBoardView view = new KanbanBoardView();
            view.setVisible(true);
        });
    }
}