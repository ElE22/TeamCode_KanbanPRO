package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.model.Task;
import com.mycompany.teamcode_kanbanpro.client.*;
import com.mycompany.teamcode_kanbanpro.model.Comment;
import com.mycompany.teamcode_kanbanpro.view.TaskEditDialog;

import javax.swing.*;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskEditController {

    private final ClientConnector connector;
    private final Frame parentView;
    private final int taskId;
    private TaskEditDialog dialog;

    public TaskEditController(ClientConnector connector, Frame parentView, int taskId) {
        this.connector = connector;
        this.parentView = parentView;
        this.taskId = taskId;
        initiateTaskEdit();
    }

    public void initiateTaskEdit() {
        try {
            Task task = getTaskDetailsFromServer();
            List<Comment> comments = getCommentsFromServer();

            if (task == null) {
                JOptionPane.showMessageDialog(parentView, "Error: No se pudo recuperar la informacion de la tarea.");
                return;
            }
            
            dialog = new TaskEditDialog(parentView, task, comments);
            
            // configurar el callback para eliminar comentarios
            dialog.setOnDeleteComment(comment -> handleDeleteComment(comment));
            
            // configurar accion del boton guardar
            dialog.getBtnSave().addActionListener(e -> handleSave());
            
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentView, "Error al gestionar la tarea: " + e.getMessage());
        }
    }

    private void handleSave() {
        try {
            // obtener la tarea actualizada con titulo, descripcion, prioridad y fecha
            Task updatedTask = dialog.getUpdatedTask();
            
            // enviar al servidor
            boolean success = updateTaskOnServer(updatedTask);
            
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Tarea actualizada correctamente.");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error: No se pudo actualizar la tarea en el servidor.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error al guardar la tarea: " + e.getMessage());
        }
    }

    private void handleDeleteComment(Comment comment) {
        // mostrar mensaje de que aun no esta implementado
        JOptionPane.showMessageDialog(dialog, "La funcionalidad de eliminar comentarios aun no esta implementada.");
        
        // cuando este listo, aqui iria la logica para eliminar en servidor
        // ejemplo:
        // deleteCommentOnServer(comment.getIdComentario());
    }

    private Task getTaskDetailsFromServer() throws Exception {
        Request req = new Request();
        req.setAction("gettaskbyid");

        Map<String, Object> payload = new HashMap<>();
        payload.put("idTarea", taskId);
        req.setPayload(payload);

        Response resp = connector.sendRequest(req);

        if (resp.isSuccess()) {
            return (Task) resp.getData();
        }
        return null;
    }

    private List<Comment> getCommentsFromServer() throws Exception {
        Request req = new Request();
        req.setAction("getcommentsbytask");

        Map<String, Object> payload = new HashMap<>();
        payload.put("idTarea", taskId);
        req.setPayload(payload);

        Response resp = connector.sendRequest(req);

        if (resp.isSuccess()) {
            return (List<Comment>) resp.getData();
        }
        return new ArrayList<>(); 
    }

    private boolean updateTaskOnServer(Task task) {
        try {
            Request req = new Request();
            req.setAction("updatetask");

            Map<String, Object> payload = new HashMap<>();
            payload.put("idTarea", task.getIdTarea());
            payload.put("titulo", task.getTitulo());
            payload.put("descripcion", task.getDescripcion());
            payload.put("idPrioridad", task.getIdPrioridad());
            payload.put("fechaVencimiento", task.getFechaVencimiento());
            req.setPayload(payload);

            Response resp = connector.sendRequest(req);

            if (resp.isSuccess()) {
                System.out.println("[TaskEditController] Tarea actualizada en servidor correctamente.");
                return true;
            } else {
                System.err.println("[TaskEditController] Error al actualizar: " + resp.getMessage());
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // metodo para eliminar comentarios (implementacion futura)
    private boolean deleteCommentOnServer(int commentId) {
        try {
            Request req = new Request();
            req.setAction("deletecomment");

            Map<String, Object> payload = new HashMap<>();
            payload.put("idComentario", commentId);
            req.setPayload(payload);

            Response resp = connector.sendRequest(req);

            return resp.isSuccess();
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}