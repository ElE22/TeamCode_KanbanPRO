package com.mycompany.teamcode_kanbanpro.view;

import com.mycompany.teamcode_kanbanpro.model.Column;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;

/**
 * @author Emanuel
 */
public class KanbanColumnPanel extends JPanel {
    private final Column columnData;
    private final KanbanBoardView parentView;
    private JPanel tasksContainer;

    public KanbanColumnPanel(Column columnData, KanbanBoardView parentView) {
        this.columnData = columnData;
        this.parentView = parentView;
        
        initializePanel();
        createHeader();
        setupTasksContainer();
        setupTransferHandler();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        setBackground(new Color(250, 250, 250));
    }

    private void createHeader() {
        JLabel titleLabel = new JLabel(columnData.getNombre(), SwingConstants.CENTER);
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(8, 5, 8, 5));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.decode(columnData.getColor()));
        titleLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Agregar al norte del panel principal
        add(titleLabel, BorderLayout.NORTH);
    }

    private void setupTasksContainer() {
        // Este panel contendra las tareas y el Glue
        tasksContainer = new JPanel();
        tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));
        tasksContainer.setBackground(new Color(250, 250, 250));
        tasksContainer.setBorder(new EmptyBorder(8, 5, 8, 5));

        // El Glue inicial para empujar las tareas hacia arriba
        tasksContainer.add(Box.createVerticalGlue());

        // Configuramos el ScrollPane
        JScrollPane scrollPane = new JScrollPane(tasksContainer);
        scrollPane.setBorder(null); // Quitar borde
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Mejorar la velocidad del scroll con la rueda del ratón
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(30);

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(180, 180, 180);
                this.trackColor = new Color(240, 240, 240);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(0, 40); // No medirá menos de 40px de alto
        }

        @Override
        protected Dimension getMaximumThumbSize() {
            return new Dimension(0, 80); // No medirá más de 80px de alto (esto la hace pequeña)
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            // Dibujamos con bordes redondeados para estilo moderno
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
            g2.dispose();
        }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            
        });
        
        // Hacer que la barra vertical siempre sea visible o aparezca segun se necesite
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTransferHandler() {
        tasksContainer.setTransferHandler(new ColumnTransferHandler(this, parentView));
    }

    public void addTask(KanbanTaskPanel taskPanel) {
        // El glue siempre es el ultimo componente en tasksContainer
        Component glue = tasksContainer.getComponent(tasksContainer.getComponentCount() - 1);
        tasksContainer.remove(glue);
        
        taskPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Espaciador entre tareas
        tasksContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        tasksContainer.add(taskPanel);
        
        // Re-insertar el glue al final
        tasksContainer.add(glue);
        
        tasksContainer.revalidate();
        tasksContainer.repaint();
    }

    /**
     * Remueve una tarea del contenedor interno
     */
    public void removeTask(KanbanTaskPanel taskPanel) {
    Component[] components = tasksContainer.getComponents();
    int taskIndex = -1;
    
    for (int i = 0; i < components.length; i++) {
        if (components[i] == taskPanel) {
            taskIndex = i;
            break;
        }
    }

    if (taskIndex != -1) {
        // Remover espaciador
        if (taskIndex > 0 && components[taskIndex - 1] instanceof Box.Filler) {
            tasksContainer.remove(taskIndex - 1);
        }
        tasksContainer.remove(taskPanel);
        
        // REFRESCAR AMBOS
        tasksContainer.revalidate();
        tasksContainer.repaint();
        this.revalidate(); // Refresca el panel de la columna completa
        this.repaint();
    }
}

    public void clearTasks() {
        tasksContainer.removeAll();
        tasksContainer.add(Box.createVerticalGlue());
        tasksContainer.revalidate();
        tasksContainer.repaint();
    }

    public KanbanTaskPanel getTaskPanel(int taskId) {
        Component[] components = tasksContainer.getComponents();
        for (Component comp : components) {
            KanbanTaskPanel found = findTaskPanelRecursive(comp, taskId);
            if (found != null) return found;
        }
        return null;
    }

    private KanbanTaskPanel findTaskPanelRecursive(Component comp, int taskId) {
        if (comp instanceof KanbanTaskPanel) {
            KanbanTaskPanel taskPanel = (KanbanTaskPanel) comp;
            if (taskPanel.getTaskData() != null && taskPanel.getTaskData().getIdTarea() == taskId) {
                return taskPanel;
            }
            return null;
        }

        if (comp instanceof Container) {
            Component[] children = ((Container) comp).getComponents();
            for (Component child : children) {
                KanbanTaskPanel found = findTaskPanelRecursive(child, taskId);
                if (found != null) return found;
            }
        }
        return null;
    }

    // Getters
    public Column getColumnData() { return columnData; }
    public String getColumnName() { return columnData.getNombre(); }
    public JPanel getTasksContainer() { return tasksContainer; }

    public boolean containsTask(KanbanTaskPanel taskPanel) {
        return taskPanel.getParent() == tasksContainer;
    }
}