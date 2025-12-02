/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;


import com.mycompany.teamcode_kanbanpro.model.Group;
import com.mycompany.teamcode_kanbanpro.model.User; 
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

public class GroupDAO {
    
    // consultas basicas de grupo
    private static final String INSERT_GROUP = "INSERT INTO grupo (nombre, descripcion) VALUES (?, ?)";
    private static final String SELECT_GROUP_BY_ID = "SELECT id_grupo, nombre, descripcion FROM grupo WHERE id_grupo = ?";
    private static final String SELECT_ALL_GROUPS = "SELECT id_grupo, nombre, descripcion FROM grupo ORDER BY nombre";
    private static final String UPDATE_GROUP = "UPDATE grupo SET nombre = ?, descripcion = ? WHERE id_grupo = ?";
    private static final String DELETE_GROUP = "DELETE FROM grupo WHERE id_grupo = ?";
    
    // consultas para las relaciones n:m
    private static final String SELECT_GROUPS_BY_PROJECT = 
            "SELECT g.id_grupo, g.nombre, g.descripcion " +
            "FROM grupo g " +
            "JOIN proyecto_grupo pg ON g.id_grupo = pg.id_grupo " +
            "WHERE pg.id_proyecto = ?";
    
    private static final String SELECT_GROUPS_BY_USER = 
            "SELECT g.id_grupo, g.nombre, g.descripcion " +
            "FROM grupo g " +
            "JOIN usuario_grupo ug ON g.id_grupo = ug.id_grupo " +
            "WHERE ug.id_usuario = ?";
    
    private static final String ADD_USER_TO_GROUP = "INSERT INTO usuario_grupo (id_usuario, id_grupo) VALUES (?, ?)";
    private static final String REMOVE_USER_FROM_GROUP = "DELETE FROM usuario_grupo WHERE id_usuario = ? AND id_grupo = ?";
    private static final String ASSIGN_GROUP_TO_PROJECT = "INSERT INTO proyecto_grupo (id_proyecto, id_grupo) VALUES (?, ?)";
    private static final String REMOVE_GROUP_FROM_PROJECT = "DELETE FROM proyecto_grupo WHERE id_proyecto = ? AND id_grupo = ?";


    // mapea una fila del resultset a un objeto group
    private Group RowToGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setIdGrupo(rs.getInt("id_grupo"));
        group.setNombre(rs.getString("nombre"));
        group.setDescripcion(rs.getString("descripcion"));
        return group;
    }

    // inserta un nuevo grupo
    public int insertGroup(Group group) {
        int generatedId = -1;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GROUP, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setString(1, group.getNombre());
            preparedStatement.setString(2, group.getDescripcion());
            
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

    // obtiene un grupo por su id
    public Group selectGroupById(int groupId) {
        Group group = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUP_BY_ID)) {
            
            preparedStatement.setInt(1, groupId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    group = RowToGroup(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }
    
    // obtiene todos los grupos
    public List<Group> selectAllGroups() {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_GROUPS);
             ResultSet rs = preparedStatement.executeQuery()) {
            
            while (rs.next()) {
                groups.add(RowToGroup(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }
    
    // actualiza un grupo
    public boolean updateGroup(Group group) {
        boolean rowUpdated = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GROUP)) {
            
            preparedStatement.setString(1, group.getNombre());
            preparedStatement.setString(2, group.getDescripcion());
            preparedStatement.setInt(3, group.getIdGrupo());
            
            rowUpdated = preparedStatement.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    // elimina un grupo
    public boolean deleteGroup(int groupId) {
        boolean rowDeleted = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GROUP)) {
            
            preparedStatement.setInt(1, groupId);
            rowDeleted = preparedStatement.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
    
    // --- metodos de relacion ---

    // obtiene los grupos asignados a un proyecto
    public List<Group> selectGroupsByProjectId(int projectId) {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUPS_BY_PROJECT)) {
            
            preparedStatement.setInt(1, projectId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    groups.add(RowToGroup(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }
    
    // obtiene los grupos a los que pertenece un usuario
    public List<Group> selectGroupsByUserId(int userId) {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUPS_BY_USER)) {
            
            preparedStatement.setInt(1, userId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    groups.add(RowToGroup(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    // añade un usuario a un grupo
    public boolean addUserToGroup(int userId, int groupId) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_USER_TO_GROUP)) {
            
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, groupId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // asigna un grupo a un proyecto
    public boolean assignGroupToProject(int projectId, int groupId) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN_GROUP_TO_PROJECT)) {
            
            preparedStatement.setInt(1, projectId);
            preparedStatement.setInt(2, groupId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
