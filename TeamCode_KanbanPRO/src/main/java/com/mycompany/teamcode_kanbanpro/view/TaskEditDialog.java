package com.mycompany.teamcode_kanbanpro.view;

import com.mycompany.teamcode_kanbanpro.model.Task;
import com.mycompany.teamcode_kanbanpro.model.Comment;
import com.mycompany.teamcode_kanbanpro.model.User;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaskEditDialog extends JDialog {

    // componentes de edicion
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<PriorityItem> priorityCombo;
    private JDateChooser dueDateChooser;
    JButton btnSave;
    
    // paneles dinamicos
    private JPanel assignedUsersPanel;
    private JPanel subtasksPanel;
    private JPanel commentsListPanel;
    private JTextArea newCommentArea;
    
    // datos
    private final Task mainTask;
    private List<Comment> comments;
    private boolean confirmed = false;
    private java.util.function.Consumer<Comment> onDeleteComment; // callback para eliminar comentarios

    private final Color HEADER_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color PRIMARY_BUTTON = new Color(46, 204, 113);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color TEXT_LIGHT = new Color(127, 140, 141);
    private final Color BORDER_COLOR = new Color(220, 223, 230);

    public TaskEditDialog(Frame parent, Task task, List<Comment> initialComments) {
        super(parent, "Gestion de Tarea", true);
        this.mainTask = task;

        if (initialComments != null) {
            this.comments = initialComments;
        } else {
            this.comments = new ArrayList<>();
        }

        initializeComponents();
        setupLayout();
        loadTaskData();
        renderAllSections();

        setSize(750, 700);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        titleField = createStyledTextField();
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        priorityCombo = new JComboBox<>();
        priorityCombo.addItem(new PriorityItem(1, "Baja", new Color(46, 204, 113)));
        priorityCombo.addItem(new PriorityItem(2, "Media", new Color(241, 196, 15)));
        priorityCombo.addItem(new PriorityItem(3, "Alta", new Color(230, 126, 34)));
        priorityCombo.addItem(new PriorityItem(4, "Critica", new Color(214, 4, 4)));
        priorityCombo.setRenderer(new PriorityRenderer());

        dueDateChooser = new JDateChooser();
        dueDateChooser.setDateFormatString("dd/MM/yyyy");
        dueDateChooser.setMinSelectableDate(new java.util.Date()); // no permite seleccionar fechas anteriores a hoy

        assignedUsersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        assignedUsersPanel.setBackground(CARD_COLOR);
        
        subtasksPanel = new JPanel();
        subtasksPanel.setLayout(new BoxLayout(subtasksPanel, BoxLayout.Y_AXIS));
        subtasksPanel.setBackground(CARD_COLOR);

        commentsListPanel = new JPanel();
        commentsListPanel.setLayout(new BoxLayout(commentsListPanel, BoxLayout.Y_AXIS));
        commentsListPanel.setBackground(CARD_COLOR);

        newCommentArea = new JTextArea(2, 20);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));
        JLabel lblTitle = new JLabel("Tarea #" + mainTask.getIdTarea() + ": " + mainTask.getTitulo());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // cuerpo con scroll
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 25, 20, 25));
        content.setBackground(BACKGROUND_COLOR);

        // 1. info principal
        JPanel infoCard = createCard("Informacion de la Tarea");
        infoCard.add(createLabel("Titulo"));
        infoCard.add(titleField);
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(createLabel("Descripcion"));
        infoCard.add(new JScrollPane(descriptionArea));
        
        JPanel grid = new JPanel(new GridLayout(1, 2, 15, 0));
        grid.setOpaque(false);
        grid.add(createFieldGroup("Prioridad", priorityCombo));
        grid.add(createFieldGroup("Vencimiento", dueDateChooser));
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(grid);
        content.add(infoCard);

        // 2. usuarios asignados (tarea principal) - ocultamos por ahora ya que no se guardara
        // content.add(Box.createVerticalStrut(15));
        // content.add(createCard("Responsables Principal", assignedUsersPanel));

        // 3. subtareas sin checkbox, solo visual
        content.add(Box.createVerticalStrut(15));
        JPanel subtaskHeader = new JPanel(new BorderLayout());
        subtaskHeader.setOpaque(false);
        
        JButton btnCreateSubtask = new JButton("+ Crear Subtarea");
        btnCreateSubtask.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnCreateSubtask.setBackground(new Color(52, 152, 219));
        btnCreateSubtask.setForeground(Color.WHITE);
        btnCreateSubtask.setFocusPainted(false);
        btnCreateSubtask.setBorder(new EmptyBorder(5, 15, 5, 15));
        btnCreateSubtask.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateSubtask.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "La funcionalidad de crear subtareas aun no esta implementada.");
        });
        
        subtaskHeader.add(new JLabel("SUBTAREAS (solo lectura)"), BorderLayout.WEST);
        subtaskHeader.add(btnCreateSubtask, BorderLayout.EAST);
        
        JPanel subCard = createCard("", subtasksPanel);
        JPanel subContainer = new JPanel(new BorderLayout());
        subContainer.setOpaque(false);
        subContainer.add(subtaskHeader, BorderLayout.NORTH);
        subContainer.add(subCard, BorderLayout.CENTER);
        content.add(subContainer);

        // 4. comentarios
        content.add(Box.createVerticalStrut(15));
        JPanel commentInputBox = new JPanel(new BorderLayout(10, 0));
        commentInputBox.setOpaque(false);
        commentInputBox.add(new JScrollPane(newCommentArea), BorderLayout.CENTER);
        
        JButton btnAdd = new JButton("Comentar");
        btnAdd.setBackground(PRIMARY_BUTTON);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> handleAddComment());
        commentInputBox.add(btnAdd, BorderLayout.EAST);

        JPanel commentContainer = new JPanel(new BorderLayout(0, 10));
        commentContainer.setOpaque(false);
        commentContainer.add(commentInputBox, BorderLayout.NORTH);
        commentContainer.add(commentsListPanel, BorderLayout.CENTER);
        content.add(createCard("Conversacion", commentContainer));

        mainPanel.add(new JScrollPane(content), BorderLayout.CENTER);

        // footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(BACKGROUND_COLOR);
        JButton btnClose = createButton("Cerrar", new Color(189, 195, 199));
        btnSave = createButton("Guardar Todo", PRIMARY_BUTTON);
        
        btnClose.addActionListener(e -> dispose());
        
        footer.add(btnClose);
        footer.add(btnSave);
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void renderAllSections() {
        // renderizar usuarios principal (oculto por ahora)
        assignedUsersPanel.removeAll();
        List<User> users = mainTask.getUsuariosAsignados();
        if (users == null || users.isEmpty()) {
            assignedUsersPanel.add(new JLabel("Sin responsables."));
        } else {
            for (User u : users) {
                assignedUsersPanel.add(createTag(u.getNombre()));
            }
        }

        // renderizar subtareas sin checkbox, solo lectura
        subtasksPanel.removeAll();
        List<Task> subs = mainTask.getSubtareas();
        if (subs == null || subs.isEmpty()) {
            subtasksPanel.add(new JLabel("No hay subtareas registradas."));
        } else {
            for (Task sub : subs) {
                subtasksPanel.add(createSubtaskRow(sub));
            }
        }

        renderComments();
    }

    private JPanel createSubtaskRow(Task sub) {
        JPanel row = new JPanel(new BorderLayout(10, 5));
        row.setBackground(CARD_COLOR);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // titulo de la subtarea
        JLabel titleLabel = new JLabel(sub.getTitulo());
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_DARK);

        // usuarios de la subtarea
        JPanel subUsers = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        subUsers.setBackground(CARD_COLOR);
        if (sub.getUsuariosAsignados() != null && !sub.getUsuariosAsignados().isEmpty()) {
            for (User u : sub.getUsuariosAsignados()) {
                JLabel avatar = new JLabel(u.getNombre());
                avatar.setToolTipText(u.getNombre());
                avatar.setOpaque(true);
                avatar.setBackground(new Color(236, 240, 241));
                avatar.setHorizontalAlignment(SwingConstants.CENTER);
                avatar.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                avatar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                avatar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    new EmptyBorder(3, 8, 3, 8)
                ));
                subUsers.add(avatar);
            }
        } else {
            JLabel noUsers = new JLabel("Sin asignar");
            noUsers.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            noUsers.setForeground(TEXT_LIGHT);
            subUsers.add(noUsers);
        }

        row.add(titleLabel, BorderLayout.CENTER);
        row.add(subUsers, BorderLayout.EAST);
        return row;
    }

    // recolecta solo los datos basicos de la tarea: titulo, descripcion, prioridad y fecha
    public Task getUpdatedTask() {
        // 1. datos basicos
        mainTask.setTitulo(titleField.getText().trim());
        mainTask.setDescripcion(descriptionArea.getText().trim());
        
        // 2. prioridad
        PriorityItem selected = (PriorityItem) priorityCombo.getSelectedItem();
        if (selected != null) {
            mainTask.setIdPrioridad(selected.getId());
            mainTask.setNombrePrioridad(selected.toString());
        }

        // 3. fecha de vencimiento
        if (dueDateChooser.getDate() != null) {
            mainTask.setFechaVencimiento(new java.sql.Date(dueDateChooser.getDate().getTime()));
        }

        return mainTask;
    }

    private void renderComments() {
        commentsListPanel.removeAll();
        for (Comment c : comments) {
            JPanel item = new JPanel(new BorderLayout(5, 5));
            item.setBackground(new Color(252, 252, 252));
            item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
            ));

            JLabel userLbl = new JLabel(c.getNombreUsuario() != null ? c.getNombreUsuario() : "Usuario");
            userLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            userLbl.setForeground(HEADER_COLOR);

            JButton btnDel = new JButton("Eliminar");
            btnDel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnDel.setForeground(Color.RED);
            btnDel.setBorder(null);
            btnDel.setContentAreaFilled(false);
            btnDel.addActionListener(e -> {
                if (onDeleteComment != null) {
                    onDeleteComment.accept(c);
                } else {
                    JOptionPane.showMessageDialog(this, "Funcion de eliminar comentarios aun no implementada.");
                }
            });

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(userLbl, BorderLayout.WEST);
            top.add(btnDel, BorderLayout.EAST);

            JLabel msg = new JLabel("<html><body style='width: 450px'>" + c.getContenido() + "</body></html>");
            item.add(top, BorderLayout.NORTH);
            item.add(msg, BorderLayout.CENTER);
            commentsListPanel.add(item);
            commentsListPanel.add(Box.createVerticalStrut(5));
        }
        commentsListPanel.revalidate();
        commentsListPanel.repaint();
    }

    private void handleAddComment() {
        String text = newCommentArea.getText().trim();
        if (!text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La funcionalidad de comentarios se manejara por separado.");
            newCommentArea.setText("");
        }
    }

    private void loadTaskData() {
        titleField.setText(mainTask.getTitulo());
        descriptionArea.setText(mainTask.getDescripcion());
        if (mainTask.getFechaVencimiento() != null) {
            dueDateChooser.setDate(mainTask.getFechaVencimiento());
        }
        for (int i = 0; i < priorityCombo.getItemCount(); i++) {
            if (priorityCombo.getItemAt(i).getId() == mainTask.getIdPrioridad()) {
                priorityCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    // --- estilos ---
    private JLabel createTag(String text) {
        JLabel tag = new JLabel(text);
        tag.setOpaque(true);
        tag.setBackground(new Color(235, 245, 251));
        tag.setForeground(HEADER_COLOR);
        tag.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        tag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return tag;
    }

    private JPanel createCard(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD_COLOR);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        if(!title.isEmpty()){
            JLabel lbl = new JLabel(title.toUpperCase());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(TEXT_LIGHT);
            lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
            p.add(lbl);
        }
        return p;
    }

    private JPanel createCard(String title, Component content) {
        JPanel p = createCard(title);
        p.add(content);
        return p;
    }

    private JTextField createStyledTextField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(251, 251, 251));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return f;
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JPanel createFieldGroup(String label, Component comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(createLabel(label), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public boolean isConfirmed() { return confirmed; }
    
    // setter para el callback de eliminar comentarios
    public void setOnDeleteComment(java.util.function.Consumer<Comment> callback) {
        this.onDeleteComment = callback;
    }

    public JButton getBtnSave() {
        return btnSave;
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
        @Override public String toString() { return name; }
        public Color getColor() { return color; }
    }

    private static class PriorityRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof PriorityItem item) {
                label.setText(item.name);
                if (!isSelected) label.setForeground(item.getColor());
            }
            return label;
        }
    }
}