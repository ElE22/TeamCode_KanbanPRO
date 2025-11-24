package com.mycompany.teamcode_kanbanpro.controller;

/**
 *
 * @author Emanuel
 */

import com.mycompany.teamcode_kanbanpro.view.RegisterUserView;
import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request; 
import com.mycompany.teamcode_kanbanpro.client.Response;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List; 
import java.util.regex.Pattern;


public class RegisterUserController {
    private final RegisterUserView view;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3001;
    private ClientConnector connector;

    // Estado de la validacion
    private boolean isPasswordStrong = false;
    private boolean isPasswordMatch = false;

    // niveles de seguridad de la clave
    private enum PasswordStrength {
        MALA, MEDIA, ALTA
    }
    // Regex para la validacion pwd
    private static final Pattern HIGH_STRENGTH = Pattern.compile("^(?=.{8,}$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\p{Punct}).*$");
    private static final Pattern MEDIUM_STRENGTH = Pattern.compile("^(?=.{8,}$)(?:(?=.*[A-Z])(?=.*[a-z])|(?=.*[A-Z])(?=.*\\d)|(?=.*[a-z])(?=.*\\d)|(?=.*\\p{Punct})(?=.*[A-Za-z])).*$");

    public RegisterUserController(RegisterUserView view) {
        this.view = view;
        try {
            this.connector = new ClientConnector(SERVER_HOST, SERVER_PORT);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "No se pudo conectar al servidor: " + e.getMessage(), "Error Crítico de Conexión", JOptionPane.ERROR_MESSAGE);
            // Si la conexion falla, no podemos continuar.
            this.connector = null; 
            view.getBtnRegister().setEnabled(false);
        }
        this.view.setDefaultCloseOperation(RegisterUserView.DISPOSE_ON_CLOSE);
        this.view.setVisible(true);

        //cargar roles
        loadRolesFromServer();

        // Eventos
        attachListeners();
        this.view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                closeConnector();
            }
        });

        view.getBtnRegister().setEnabled(false);
    }

    //cargar roles
    private void loadRolesFromServer() {
        if (this.connector == null) {
            view.setRoleList(List.of("Error de Conexión"));
            return;
        }
        try {
            //olicitud para obtener roles
            Request req = new Request();
            req.setAction("getRoles");
            
            //enviar la solicitud 
            Response response = this.connector.sendRequest(req);
            
            if (response.isSuccess() && response.getData() instanceof List) {
                List<String> roleNames = (List<String>) response.getData();
                view.setRoleList(roleNames);
            } else {
                JOptionPane.showMessageDialog(view, 
                    "Error al obtener roles del servidor: " + response.getMessage(),
                    "Error de Carga", JOptionPane.WARNING_MESSAGE);
                view.setRoleList(List.of("Error de Roles"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, 
                "Error de comunicación al solicitar roles: " + e.getMessage(),
                "Error de Red", JOptionPane.ERROR_MESSAGE);
            view.setRoleList(List.of("Error de Red"));
        }
    }

    //Escuhar los eventos
    private void attachListeners() {
        // Listeners para las claves
        view.getTxtPassword().getDocument().addDocumentListener(new PasswordDocumentListener());
        view.getTxtConfirmPassword().getDocument().addDocumentListener(new PasswordDocumentListener());

        // Listeners para los botones
        view.getBtnRegister().addActionListener(e -> this.handleRegister());
        view.getBtnCancel().addActionListener(e -> this.handleCancel());
    }

    // validacion de clave
    private PasswordStrength checkPasswordStrength(String password) {
         System.out.println("HIGH " + HIGH_STRENGTH.matcher(password).matches());
         System.out.println("MEDIUM " + MEDIUM_STRENGTH.matcher(password).matches());
         if (HIGH_STRENGTH.matcher(password).matches()) {
            return PasswordStrength.ALTA;
        }
        else if (MEDIUM_STRENGTH.matcher(password).matches()) {
            return PasswordStrength.MEDIA;
        }
        else {
            return PasswordStrength.MALA;
        }
    }

    //DocumentListener para verificacio en tiempo real
    private class PasswordDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkPasswords();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkPasswords();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkPasswords();
        }
    }

    private void checkPasswords() {
        String password = new String(view.getTxtPassword().getPassword());
        String confirmPassword = new String(view.getTxtConfirmPassword().getPassword());
        PasswordStrength strength;

        //verificar fuerza de la clave
        strength = checkPasswordStrength(password);
        updateStrengthLabel(strength);

        //verificar coincidencia
        if (password.isEmpty() && confirmPassword.isEmpty()) {
            view.getLblPasswordMatch().setText("Coincidencia: -");
            view.getLblPasswordMatch().setForeground(Color.BLACK);
            isPasswordMatch = false;
        } else if (password.equals(confirmPassword) && !password.isEmpty()) {
            view.getLblPasswordMatch().setText("Coincidencia: ¡Claves iguales!");
            view.getLblPasswordMatch().setForeground(new Color(0, 150, 0));
            isPasswordMatch = true;
        } else {
            view.getLblPasswordMatch().setText("Coincidencia: ¡NO COINCIDEN!");
            view.getLblPasswordMatch().setForeground(Color.RED);
            isPasswordMatch = false;
        }

        // habilitar/deshabilitar el boton de registro
        isPasswordStrong = (strength == PasswordStrength.MEDIA || strength == PasswordStrength.ALTA);
        view.getBtnRegister().setEnabled(isPasswordStrong && isPasswordMatch);
    }

    private void updateStrengthLabel(PasswordStrength strength) {
        JLabel label = view.getLblPasswordStrength();

        switch (strength) {
            case ALTA:
                label.setText("Seguridad: ALTA (¡Excelente!)");
                label.setForeground(new Color(0, 150, 0));
                break;
            case MEDIA:
                label.setText("Seguridad: MEDIA (Aceptable)");
                label.setForeground(Color.ORANGE.darker());
                break;
            case MALA:
            default:
                label.setText("Seguridad: MALA (Mínimo 8, Letras/Números/Símbolos)");
                label.setForeground(Color.RED);
                break;
        }
    }

    //manejador del boton de registro
    private void handleRegister() {
        if (this.connector == null) {
             JOptionPane.showMessageDialog(view, "No hay conexión activa con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        //extrear los datos
        String nombre = view.getTxtNombre().getText().trim();
        String usuario = view.getTxtUsuario().getText().trim();
        String email = view.getTxtEmail().getText().trim();
        String password = new String(view.getTxtPassword().getPassword());
        String rolNombre = (String) view.getCmbRol().getSelectedItem();

        //validaciones finales de cliente finales
        if (nombre.isEmpty() || usuario.isEmpty() || email.isEmpty() || password.isEmpty() || rolNombre == null || rolNombre.equals("No hay roles disponibles")) {
            JOptionPane.showMessageDialog(view, "Por favor, completa todos los campos y selecciona un Rol válido.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // crear el payload de la solicitud
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", nombre);
        payload.put("usuario", usuario);
        payload.put("email", email);
        payload.put("clave", password);
        payload.put("rolNombre", rolNombre);
        
        Request req = new Request();
        req.setAction("register");
        req.setPayload(payload);

        try  {
            Response response = this.connector.sendRequest(req);

            // manejar respuesyas
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(view,
                        "¡Registro Exitoso! Ahora puedes iniciar sesión.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                view.clearFields();
                view.dispose();
            } else {
                JOptionPane.showMessageDialog(view,
                        "Error del Servidor: " + response.getMessage(),
                        "Error de Registro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error de comunicación con el servidor: " + ex.getMessage() + "\nVerifique que el servidor esté activo en el puerto " + SERVER_PORT + ".",
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        Arrays.fill(view.getTxtPassword().getPassword(), '0');
        Arrays.fill(view.getTxtConfirmPassword().getPassword(), '0');
    }

    private void handleCancel() {
        view.dispose();
    }

    private void closeConnector() {
        if (this.connector != null) {
            try {
                this.connector.close();
                this.connector = null;
                System.out.println("Conexión del registro cerrada.");
            } catch (Exception e) {
                System.err.println("Error al cerrar el conector: " + e.getMessage());
            }
        }
    }

}
