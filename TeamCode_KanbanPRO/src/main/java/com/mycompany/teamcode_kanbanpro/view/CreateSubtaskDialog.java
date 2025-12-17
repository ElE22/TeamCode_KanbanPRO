/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import com.mycompany.teamcode_kanbanpro.model.Task;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 *
 * @author Emanuel
 */

public class CreateSubtaskDialog extends JDialog {
    private final Task parentTask;
    private JTextField tituloField;
    private JTextArea descripcionArea;
    private JComboBox<String> prioridadCombo;
    private JButton createButton;
    private JButton cancelButton;
    private boolean confirmed = false;

    public CreateSubtaskDialog(Frame parent, Task parentTask) {
        super(parent, "Crear Subtarea", true);
        this.parentTask = parentTask;
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout(10, 10));
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(63, 81, 181));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Nueva Subtarea de: " + parentTask.getTitulo());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(createFieldPanel("Titulo:", tituloField = new JTextField()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createDescriptionPanel());
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createPriorityPanel());

        return formPanel;
    }

    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 13));

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        panel.add(labelComponent, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Descripcion:");
        label.setFont(new Font("Arial", Font.BOLD, 13));

        descripcionArea = new JTextArea(4, 20);
        descripcionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);
        descripcionArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(descripcionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPriorityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Prioridad:");
        label.setFont(new Font("Arial", Font.BOLD, 13));

        prioridadCombo = new JComboBox<>(new String[]{"BAJA", "MEDIA", "ALTA"});
        prioridadCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        prioridadCombo.setPreferredSize(new Dimension(200, 30));

        panel.add(label, BorderLayout.NORTH);
        panel.add(prioridadCombo, BorderLayout.WEST);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        createButton = new JButton("Crear Subtarea");
        createButton.setFont(new Font("Arial", Font.BOLD, 12));
        createButton.setBackground(new Color(76, 175, 80));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setPreferredSize(new Dimension(140, 35));
        createButton.addActionListener(e -> {
            if (validateFields()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(158, 158, 158));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private boolean validateFields() {
        if (tituloField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "El titulo es obligatorio",
                "Error de Validacion",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getTitulo() {
        return tituloField.getText().trim();
    }

    public String getDescripcion() {
        return descripcionArea.getText().trim();
    }

    public String getPrioridad() {
        return (String) prioridadCombo.getSelectedItem();
    }

    public Task getParentTask() {
        return parentTask;
    }
}
