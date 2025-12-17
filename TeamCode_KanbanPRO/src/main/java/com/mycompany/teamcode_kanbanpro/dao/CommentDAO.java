/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Comment;
import com.mycompany.teamcode_kanbanpro.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Emanuel
 */

public class CommentDAO {

    private static final String SELECT_BY_TASK = "SELECT * FROM comentario WHERE id_tarea = ? ORDER BY fecha DESC";
    private static final String INSERT_COMMENT = "INSERT INTO comentario (id_tarea, id_usuario, contenido) VALUES (?, ?, ?)";

    // mapea una fila de la base de datos al objeto modelo
    private Comment rowToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setIdComentario(rs.getInt("id_comentario"));
        comment.setIdTarea(rs.getInt("id_tarea"));
        comment.setIdUsuario(rs.getInt("id_usuario"));
        comment.setContenido(rs.getString("contenido"));
        comment.setFecha(rs.getTimestamp("fecha"));
        return comment;
    }

    // obtiene la lista de comentarios de una tarea especifica
    public List<Comment> getCommentsByTask(int taskId) throws Exception {
        List<Comment> comments = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_TASK)) {
            
            preparedStatement.setInt(1, taskId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    comments.add(rowToComment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("error al obtener comentarios de la tarea id: " + taskId);
            e.printStackTrace();
        }
        return comments;
    }

    // inserta un nuevo comentario en la base de datos
    public void insertComment(Comment comment) throws Exception {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COMMENT)) {
            
            preparedStatement.setInt(1, comment.getIdTarea());
            preparedStatement.setInt(2, comment.getIdUsuario());
            preparedStatement.setString(3, comment.getContenido());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error al insertar comentario en tarea id: " + comment.getIdTarea());
            e.printStackTrace();
        }
    }
}
