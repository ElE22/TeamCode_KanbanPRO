package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.mycompany.teamcode_kanbanpro.view.KanbanBoardScreen;
import com.mycompany.teamcode_kanbanpro.view.LoginScreen;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Emanuel
 */
public class AuthController {
    LoginScreen loginView;
    private String host;
    private int port;

    public AuthController(LoginScreen ls){
        this.loginView = ls;
        this.host = "localhost";
        this.port = 3001;
        initialize();
    }

    private void initialize() {
        this.loginView.loginButton.addActionListener(e -> {authenticateUser();});
    }

    private void authenticateUser(){
        String user = loginView.userField.getText().trim();
        
        String pass = new String(loginView.passField.getPassword());
        try (ClientConnector conn = new ClientConnector(host, port)) {
            Request req = new Request();
            req.setAction("login");
            Map<String, Object> payload = new HashMap<>();
            payload.put("usaurio", user);
            payload.put("clave", pass);
            req.setPayload(payload); 
            Response resp = conn.sendRequest(req);
            if (resp.isSuccess()) {
                System.out.println("Autenticación exitosa: " + resp.getMessage());
                loginView.dispose(); // Cerramos la ventana de login
                new KanbanBoardScreen().setVisible(true);
            } else {
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
