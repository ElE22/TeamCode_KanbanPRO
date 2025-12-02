/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Sprint;
import com.mycompany.teamcode_kanbanpro.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Emanuel
 */
public class SprintDAO {
    
    // consultas basicas
    private static final String INSERT_SPRINT = 
            "INSERT INTO sprint (id_proyecto, id_estado, nombre, descripcion, fecha_inicio, fecha_fin, limite_wip) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
    private static final String SELECT_SPRINT_BY_ID = 
            "SELECT s.id_sprint, s.id_proyecto, p.nombre AS nombre_proyecto, s.id_estado, es.nombre AS nombre_estado, " +
            "s.nombre, s.descripcion, s.fecha_inicio, s.fecha_fin, s.limite_wip, s.created_at, s.updated_at " +
            "FROM sprint s " +
            "JOIN proyecto p ON s.id_proyecto = p.id_proyecto " +
            "JOIN estado_sprint es ON s.id_estado = es.id_estado " +
            "WHERE s.id_sprint = ?";
            
    private static final String SELECT_ALL_SPRINTS_BY_PROJECT = 
            "SELECT s.id_sprint, s.id_proyecto, p.nombre AS nombre_proyecto, s.id_estado, es.nombre AS nombre_estado, " +
            "s.nombre, s.descripcion, s.fecha_inicio, s.fecha_fin, s.limite_wip, s.created_at, s.updated_at " +
            "FROM sprint s " +
            "JOIN proyecto p ON s.id_proyecto = p.id_proyecto " +
            "JOIN estado_sprint es ON s.id_estado = es.id_estado " +
            "WHERE s.id_proyecto = ? " +
            "ORDER BY s.fecha_inicio DESC";
            
    private static final String UPDATE_SPRINT = 
            "UPDATE sprint SET id_estado = ?, nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, limite_wip = ? " +
            "WHERE id_sprint = ?";
            
    private static final String DELETE_SPRINT = 
            "DELETE FROM sprint WHERE id_sprint = ?";
    
    // mapea una fila del resultset a un objeto sprint
    private Sprint RowToSprint(ResultSet rs) throws SQLException {
        Sprint sprint = new Sprint();
        sprint.setIdSprint(rs.getInt("id_sprint"));
        sprint.setIdProyecto(rs.getInt("id_proyecto"));
        sprint.setIdEstado(rs.getInt("id_estado"));
        sprint.setNombre(rs.getString("nombre"));
        sprint.setDescripcion(rs.getString("descripcion"));
        sprint.setFechaInicio(rs.getDate("fecha_inicio"));
        sprint.setFechaFin(rs.getDate("fecha_fin"));
        // usa getobject para manejar valores null en limite_wip
        sprint.setLimiteWip((Integer) rs.getObject("limite_wip")); 
        sprint.setCreatedAt(rs.getTimestamp("created_at"));
        sprint.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // campos extra
        sprint.setNombreProyecto(rs.getString("nombre_proyecto"));
        sprint.setNombreEstado(rs.getString("nombre_estado"));
        
        return sprint;
    }

    // inserta un nuevo sprint
    public int insertSprint(Sprint sprint) {
        int generatedId = -1;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SPRINT, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setInt(1, sprint.getIdProyecto());
            preparedStatement.setInt(2, sprint.getIdEstado());
            preparedStatement.setString(3, sprint.getNombre());
            preparedStatement.setString(4, sprint.getDescripcion());
            preparedStatement.setDate(5, sprint.getFechaInicio());
            preparedStatement.setDate(6, sprint.getFechaFin());
            
            // maneja el campo limite_wip que puede ser null
            if (sprint.getLimiteWip() != null) {
                preparedStatement.setInt(7, sprint.getLimiteWip());
            } else {
                preparedStatement.setNull(7, java.sql.Types.INTEGER);
            }
            
            preparedStatement.executeUpdate();
            
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    // obtiene un sprint por su id
    public Sprint selectSprintById(int sprintId) {
        Sprint sprint = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SPRINT_BY_ID)) {
            
            preparedStatement.setInt(1, sprintId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    sprint = RowToSprint(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sprint;
    }
    
    // obtiene todos los sprints de un proyecto
    public List<Sprint> selectSprintsByProjectId(int projectId) {
        List<Sprint> sprints = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SPRINTS_BY_PROJECT)) {
            
            preparedStatement.setInt(1, projectId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    sprints.add(RowToSprint(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sprints;
    }
    
    // actualiza un sprint
    public boolean updateSprint(Sprint sprint) {
        boolean rowUpdated = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SPRINT)) {
            
            preparedStatement.setInt(1, sprint.getIdEstado());
            preparedStatement.setString(2, sprint.getNombre());
            preparedStatement.setString(3, sprint.getDescripcion());
            preparedStatement.setDate(4, sprint.getFechaInicio());
            preparedStatement.setDate(5, sprint.getFechaFin());
            
            // maneja el campo limite_wip que puede ser null
            if (sprint.getLimiteWip() != null) {
                preparedStatement.setInt(6, sprint.getLimiteWip());
            } else {
                preparedStatement.setNull(6, java.sql.Types.INTEGER);
            }
            
            preparedStatement.setInt(7, sprint.getIdSprint());
            
            rowUpdated = preparedStatement.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    // elimina un sprint
    public boolean deleteSprint(int sprintId) {
        boolean rowDeleted = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SPRINT)) {
            
            preparedStatement.setInt(1, sprintId);
            rowDeleted = preparedStatement.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}