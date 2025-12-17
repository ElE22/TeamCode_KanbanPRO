/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import java.util.List;

import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.CommentDAO;
import com.mycompany.teamcode_kanbanpro.model.Comment;

/**
 *
 * @author Emanuel
 */
public class CommentServerHandler {
    private CommentDAO commentDAO;

    public CommentServerHandler(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    public Response handleGetCommentsByTask(int taskId) {
       
        try {
            List<Comment> comment = commentDAO.getCommentsByTask(taskId);

            if(comment.isEmpty()){
                Response r = new Response(true, "Comentarios no encontrados para la tarea id: " + taskId);
                return r;
            }
            Response response = new Response(true, "Comentarios cargados exitosamente.");
            response.setData(comment);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error interno al cargar comentarios: " + e.getMessage());

        }
    }
    
    
     
}
