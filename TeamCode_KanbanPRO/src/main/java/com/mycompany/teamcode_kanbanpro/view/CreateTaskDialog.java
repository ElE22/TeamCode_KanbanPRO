/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;
import com.mycompany.teamcode_kanbanpro.model.Column;
import com.mycompany.teamcode_kanbanpro.model.Task;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.toedter.calendar.JDateChooser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


/**
 *
 * @author escobe11
 */
public class CreateTaskDialog extends JDialog {
    
    // componentes del formulario
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<PriorityItem> priorityCombo;
    private JDateChooser dueDateChooser;
    private JButton saveButton;
    private JButton cancelButton;
    
    // datos
    private Task createdTask;
    private boolean confirmed = false;
    private Column backlogColumn;
    
    // colores del tema (basados en tu aplicacion)
    private final Color HEADER_COLOR = new Color(41, 128, 185); // azul del header
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); // gris claro
    private final Color CARD_COLOR = Color.WHITE;
    private final Color INPUT_BG = new Color(249, 249, 249);
    private final Color PRIMARY_BUTTON = new Color(46, 204, 113); // verde
    private final Color PRIMARY_BUTTON_HOVER = new Color(39, 174, 96);
    private final Color SECONDARY_BUTTON = new Color(189, 195, 199); // gris
    private final Color SECONDARY_BUTTON_HOVER = new Color(149, 165, 166);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color TEXT_LIGHT = new Color(127, 140, 141);
    private final Color BORDER_COLOR = new Color(220, 223, 230);
    
    public CreateTaskDialog(Frame parent, List<Column> columns) {
        super(parent, "Nueva Tarea", true);
        
        this.backlogColumn = findBacklogColumn(columns);
        
        if (this.backlogColumn == null) {
            JOptionPane.showMessageDialog(parent,
                "No se encontró la columna Backlog. No se puede crear la tarea.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initializeComponents();
        setupLayout();
        attachListeners();
        
        setSize(620, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(false);
    }
    
    private Column findBacklogColumn(List<Column> columns) {
        for (Column col : columns) {
            if (col.getNombre().equalsIgnoreCase("BACKLOG") || 
                col.getNombre().equalsIgnoreCase("TO DO") ||
                col.getNombre().equalsIgnoreCase("TODO")) {
                return col;
            }
        }
        return columns.isEmpty() ? null : columns.get(0);
    }
    
    private void initializeComponents() {
        // campo de titulo
        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setBackground(INPUT_BG);
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        
        // area de descripcion
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setBackground(INPUT_BG);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // combo de prioridad
        priorityCombo = new JComboBox<>();
        priorityCombo.addItem(new PriorityItem(1, "Baja", new Color(46, 204, 113)));
        priorityCombo.addItem(new PriorityItem(2, "Media", new Color(241, 196, 15)));
        priorityCombo.addItem(new PriorityItem(3, "Alta", new Color(230, 126, 34)));
        priorityCombo.addItem(new PriorityItem(4, "Crítica", new Color(214, 4, 4)));
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priorityCombo.setRenderer(new PriorityRenderer());
        priorityCombo.setSelectedIndex(1);
        priorityCombo.setBackground(INPUT_BG);
        priorityCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        // selector de fecha
        dueDateChooser = new JDateChooser();
        dueDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dueDateChooser.setDateFormatString("dd/MM/yyyy");
        dueDateChooser.setMinSelectableDate(new java.util.Date());
        dueDateChooser.setBackground(INPUT_BG);
        dueDateChooser.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        // boton guardar (verde)
        saveButton = new JButton("Crear Tarea");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(PRIMARY_BUTTON);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(160, 44));
        
        // efecto hover para boton guardar
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                saveButton.setBackground(PRIMARY_BUTTON_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                saveButton.setBackground(PRIMARY_BUTTON);
            }
        });
        
        // boton cancelar (gris)
        cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(SECONDARY_BUTTON);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(130, 44));
        
        // efecto hover para boton cancelar
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(SECONDARY_BUTTON_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(SECONDARY_BUTTON);
            }
        });
    }
    
    private void setupLayout() {
        // panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // header azul
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel headerTitle = new JLabel("Nueva Tarea", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // panel de contenido
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // panel del formulario (tarjeta blanca)
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(CARD_COLOR);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        // campo titulo
        formCard.add(createLabel("Título de la Tarea *"));
        formCard.add(Box.createVerticalStrut(8));
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        formCard.add(titleField);
        formCard.add(Box.createVerticalStrut(20));
        
        // campo descripcion
        formCard.add(createLabel("Descripción"));
        formCard.add(Box.createVerticalStrut(8));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        descScroll.setBackground(INPUT_BG);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        formCard.add(descScroll);
        formCard.add(Box.createVerticalStrut(20));
        
        // grid para prioridad y fecha
        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        gridPanel.setBackground(CARD_COLOR);
        gridPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // columna prioridad
        JPanel priorityPanel = new JPanel();
        priorityPanel.setLayout(new BoxLayout(priorityPanel, BoxLayout.Y_AXIS));
        priorityPanel.setBackground(CARD_COLOR);
        priorityPanel.add(createLabel("Prioridad *"));
        priorityPanel.add(Box.createVerticalStrut(8));
        priorityCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        priorityPanel.add(priorityCombo);
        
        // columna fecha
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
        datePanel.setBackground(CARD_COLOR);
        datePanel.add(createLabel("Fecha de Vencimiento"));
        datePanel.add(Box.createVerticalStrut(8));
        dueDateChooser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        datePanel.add(dueDateChooser);
        
        gridPanel.add(priorityPanel);
        gridPanel.add(datePanel);
        
        formCard.add(gridPanel);
        formCard.add(Box.createVerticalStrut(22));
        
        // panel info de la columna
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(212, 239, 223));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            new EmptyBorder(12, 16, 12, 16)
        ));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        
       
        
        JLabel infoText = new JLabel("La tarea se creará en la columna: " + backlogColumn.getNombre());
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoText.setForeground(new Color(39, 174, 96));
        
        JPanel infoContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        infoContent.setBackground(new Color(212, 239, 223));
        infoContent.add(infoText);
        
        infoPanel.add(infoContent, BorderLayout.WEST);
        
        formCard.add(infoPanel);
        
        contentPanel.add(formCard, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    private void attachListeners() {
        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> handleCancel());
        titleField.addActionListener(e -> handleSave());
    }
    
    private void handleSave() {
        String title = titleField.getText().trim();
        
        if (title.isEmpty()) {
            showError("El título es obligatorio", "Campo requerido");
            titleField.requestFocus();
            return;
        }
        
        if (title.length() > 255) {
            showError("El título no puede exceder 255 caracteres", "Título muy largo");
            return;
        }
        
        createdTask = new Task();
        createdTask.setTitulo(title);
        createdTask.setDescripcion(descriptionArea.getText().trim());
        
        PriorityItem priority = (PriorityItem) priorityCombo.getSelectedItem();
        createdTask.setIdPrioridad(priority.getId());
        createdTask.setNombrePrioridad(priority.getName());
        
        createdTask.setIdColumna(backlogColumn.getIdColumna());
        createdTask.setNombreColumna(backlogColumn.getNombre());
        
        if (dueDateChooser.getDate() != null) {
            java.util.Date utilDate = dueDateChooser.getDate();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            createdTask.setFechaVencimiento(sqlDate);
        }
        
        confirmed = true;
        dispose();
    }
    
    private void handleCancel() {
        confirmed = false;
        dispose();
    }
    
    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Task getCreatedTask() {
        return createdTask;
    }
    
    private static class PriorityItem {
        private final int id;
        private final String name;
        private final Color color;
        
        public PriorityItem(int id, String name, Color color) {
            this.id = id;
            this.name = name;
            this.color = color;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public Color getColor() { return color; }
        
        @Override
        public String toString() { return name; }
    }
    
    private static class PriorityRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            
                if (value instanceof PriorityItem) {
                    PriorityItem item = (PriorityItem) value;
                    label.setText( item.getName());
                if (!isSelected) {
                    label.setForeground(item.getColor());
                }
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setBorder(new EmptyBorder(8, 10, 8, 10));
            }
            
            return label;
        }
    }
}