package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.User;
import com.mycompany.teamcode_kanbanpro.util.ImageLoader;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import com.mycompany.teamcode_kanbanpro.view.LoginScreen;
import com.mycompany.teamcode_kanbanpro.view.RegisterUserView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author Emanuel
 */
public class AuthController {
    LoginScreen loginView;
    RegisterUserView registerView;
    private String host;
    private int port;
    ClientConnector conn = null;

    public AuthController(){
        this.loginView = new LoginScreen();
        this.registerView = new RegisterUserView();
        loginView.setIconImage(ImageLoader.loadImage());
        loginView.setVisible(true);
        this.host = "localhost";
        this.port = 3001;
        initialize();
    }


    private void initialize() {
        this.loginView.loginButton.addActionListener(e -> {authenticateUser();});
        this.loginView.registerButton.addActionListener(e -> new RegisterUserController(this.registerView));
        // manejamos el evento cuando se da enter en el password
        this.loginView.passField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //lamanmos al mismo metodo del boton de iniciar sesion
                authenticateUser(); 
            }
        });
    }

    private void authenticateUser(){
        String user = loginView.userField.getText().trim();
        
        String pass = new String(loginView.passField.getPassword());
        try  {
            conn = new ClientConnector(host, port);
            Request req = new Request();
            req.setAction("login");
            Map<String, Object> payload = new HashMap<>();
            payload.put("usaurio", user);
            payload.put("clave", pass);
            req.setPayload(payload); 
            Response resp = conn.sendRequest(req);
            if (resp.isSuccess()) {
                User userResp = (User) resp.getData();
                conn.setUserID(userResp.getIdUsuario());
                conn.setUserRole(userResp.getRolNombre());
                conn.setUserName(userResp.getNombre());
                loginView.dispose();
                new PantallaPrincipalController(conn);
            } else {
                conn.close();
                System.out.println("Error en la autenticacion: " + resp.getMessage());
                JOptionPane.showMessageDialog(loginView, "Error de autenticación: " + resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                this.loginView.userField.setText("");
                this.loginView.passField.setText("");
                
            }
            
        } catch (Exception e) {
           
            e.printStackTrace();
            JOptionPane.showMessageDialog(loginView, "Error de conexión al servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
