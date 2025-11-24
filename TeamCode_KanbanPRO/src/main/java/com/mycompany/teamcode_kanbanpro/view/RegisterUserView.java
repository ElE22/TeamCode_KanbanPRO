package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author Emanuel
 */
public class RegisterUserView extends JFrame {
    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> cmbRol;
    private JButton btnRegister;
    private JButton btnCancel;
    private JLabel lblPasswordStrength;
     private JLabel lblPasswordMatch; 
    

    public RegisterUserView() {
        super("Registro de Nuevo Usuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400); 
        setLayout(new BorderLayout(15, 15));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(8, 2, 10, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        
        txtNombre = new JTextField(20);
        txtUsuario = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtConfirmPassword = new JPasswordField(20);
        lblPasswordStrength = new JLabel("Seguridad: -");
        lblPasswordStrength.setHorizontalAlignment(SwingConstants.LEFT);
        lblPasswordMatch = new JLabel("Coincidencia: -");
        lblPasswordMatch.setHorizontalAlignment(SwingConstants.LEFT);
        
        cmbRol = new JComboBox<>();
        
        btnRegister = new JButton("Registrar Usuario");
        btnCancel = new JButton("Cancelar");

        
        mainPanel.add(new JLabel("Nombre Completo:"));
        mainPanel.add(txtNombre);

        mainPanel.add(new JLabel("Usuario:"));
        mainPanel.add(txtUsuario);

        mainPanel.add(new JLabel("Email:"));
        mainPanel.add(txtEmail);

        
        // Fila 4: Contraseña
        mainPanel.add(new JLabel("Contraseña:"));
        mainPanel.add(txtPassword);

        // q tan fuerte es la claev 
        mainPanel.add(new JLabel(""));
        mainPanel.add(lblPasswordStrength);

        // Confirmar ambas claves
        mainPanel.add(new JLabel("Confirmar Clave:"));
        
        mainPanel.add(txtConfirmPassword);

        // Indicar coincidir 
        mainPanel.add(new JLabel(""));
        mainPanel.add(lblPasswordMatch);

        //Roles
        mainPanel.add(new JLabel("Rol:"));
        mainPanel.add(cmbRol);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnRegister);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
    }
    
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JPasswordField getTxtConfirmPassword() { return txtConfirmPassword; } 
    public JComboBox<String> getCmbRol() { return cmbRol; }
    public JButton getBtnRegister() { return btnRegister; }
    public JButton getBtnCancel() { return btnCancel; }
    public JLabel getLblPasswordStrength() { return lblPasswordStrength; }
    public JLabel getLblPasswordMatch() { return lblPasswordMatch; }
    
    public void setRoleList(List<String> roles) {
        cmbRol.removeAllItems();
        if (roles != null) {
            for (String role : roles) {
                cmbRol.addItem(role);
            }
        }
        if (cmbRol.getItemCount() == 0) {
            cmbRol.addItem("No hay roles disponibles");
        }
    }

    public void clearFields() {
        txtNombre.setText("");
        txtUsuario.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText(""); 
        cmbRol.setSelectedIndex(0);
    }

   
}
