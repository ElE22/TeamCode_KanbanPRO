/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Group;
import com.mycompany.teamcode_kanbanpro.model.User; // === NUEVA INTEGRACIÓN: Import añadido ===
import com.mycompany.teamcode_kanbanpro.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Emanuel
 */
public class GroupDAO {

    // Consultas básicas de grupo
    private static final String INSERT_GROUP = "INSERT INTO grupo (nombre, descripcion) VALUES (?, ?)";
    private static final String SELECT_GROUP_BY_ID = "SELECT id_grupo, nombre, descripcion FROM grupo WHERE id_grupo = ?";
    private static final String SELECT_GROUP_BY_NAME = "SELECT id_grupo, nombre, descripcion FROM grupo WHERE nombre = ?";
    private static final String SELECT_ALL_GROUPS = "SELECT id_grupo, nombre, descripcion FROM grupo ORDER BY nombre";
    private static final String UPDATE_GROUP = "UPDATE grupo SET nombre = ?, descripcion = ? WHERE id_grupo = ?";
    private static final String DELETE_GROUP = "DELETE FROM grupo WHERE id_grupo = ?";

    // Consultas para las relaciones N:M
    private static final String SELECT_GROUPS_BY_PROJECT
            = "SELECT g.id_grupo, g.nombre, g.descripcion "
            + "FROM grupo g "
            + "JOIN proyecto_grupo pg ON g.id_grupo = pg.id_grupo "
            + "WHERE pg.id_proyecto = ?";

    private static final String SELECT_GROUPS_BY_USER
            = "SELECT g.id_grupo, g.nombre, g.descripcion "
            + "FROM grupo g "
            + "JOIN usuario_grupo ug ON g.id_grupo = ug.id_grupo "
            + "WHERE ug.id_usuario = ?";

    // Consulta añadida para obtener usuarios de un grupo 
    private static final String SELECT_USERS_BY_GROUP
            = "SELECT u.id_usuario, u.id_rol, u.usuario, u.nombre, u.email, u.activo, u.creado_en "
            + "FROM usuario u "
            + "JOIN usuario_grupo ug ON u.id_usuario = ug.id_usuario "
            + "WHERE ug.id_grupo = ? "
            + "ORDER BY u.nombre";

    private static final String ADD_USER_TO_GROUP = "INSERT INTO usuario_grupo (id_usuario, id_grupo) VALUES (?, ?)";
    private static final String REMOVE_USER_FROM_GROUP = "DELETE FROM usuario_grupo WHERE id_usuario = ? AND id_grupo = ?";
    
    // Consulta añadida para verificar membresía
    private static final String CHECK_USER_IN_GROUP = "SELECT COUNT(*) FROM usuario_grupo WHERE id_usuario = ? AND id_grupo = ?";
    
    private static final String ASSIGN_GROUP_TO_PROJECT = "INSERT INTO proyecto_grupo (id_proyecto, id_grupo) VALUES (?, ?)";
    private static final String REMOVE_GROUP_FROM_PROJECT = "DELETE FROM proyecto_grupo WHERE id_proyecto = ? AND id_grupo = ?";

    // Mapea una fila del ResultSet a un objeto Group
    private Group rowToGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setIdGrupo(rs.getInt("id_grupo"));
        group.setNombre(rs.getString("nombre"));
        group.setDescripcion(rs.getString("descripcion"));
        return group;
    }


     //Mapea una fila del ResultSet a un objeto User (sin password)
    private User rowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setIdUsuario(rs.getInt("id_usuario"));
        user.setIdRol(rs.getInt("id_rol"));
        user.setUsuario(rs.getString("usuario"));
        user.setNombre(rs.getString("nombre"));
        user.setEmail(rs.getString("email"));
        user.setActivo(rs.getBoolean("activo"));
        user.setCreadoEn(rs.getTimestamp("creado_en"));
        // No se incluye password por seguridad
        return user;
    }

    // Inserta un nuevo grupo
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
                    group.setIdGrupo(generatedId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    // Obtiene un grupo por su ID
    public Group selectGroupById(int groupId) {
        Group group = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUP_BY_ID)) {

            preparedStatement.setInt(1, groupId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    group = rowToGroup(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }

    // Obtiene un grupo por su nombre
    public Group selectGroupByName(String nombre) {
        Group group = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_GROUP_BY_NAME)) {
            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    group = rowToGroup(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }

    // Obtiene todos los grupos
    public List<Group> selectAllGroups() {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_GROUPS);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                groups.add(rowToGroup(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    // Actualiza un grupo
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

    // Elimina un grupo
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

    // --- Métodos de relación ---

    // Obtiene los grupos asignados a un proyecto
    public List<Group> selectGroupsByProjectId(int projectId) {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUPS_BY_PROJECT)) {

            preparedStatement.setInt(1, projectId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    groups.add(rowToGroup(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    // Obtiene los grupos a los que pertenece un usuario
    public List<Group> selectGroupsByUserId(int userId) {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUPS_BY_USER)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    groups.add(rowToGroup(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

   
    
    // Obtiene los usuarios que pertenecen a un grupo
    public List<User> selectUsersByGroupId(int groupId) {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_USERS_BY_GROUP)) {

            ps.setInt(1, groupId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(rowToUser(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

  
    //Verifica si un usuario ya es miembro de un grupo
    public boolean isUserInGroup(int userId, int groupId) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(CHECK_USER_IN_GROUP)) {

            ps.setInt(1, userId);
            ps.setInt(2, groupId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // Añade un usuario a un grupo
    public boolean addUserToGroup(int userId, int groupId) {
      //Verificar si ya es miembro
        if (isUserInGroup(userId, groupId)) {
            return false; // Ya es miembro
        }

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

    // Remueve un usuario de un grupo
    public boolean removeUserFromGroup(int userId, int groupId) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(REMOVE_USER_FROM_GROUP)) {

            ps.setInt(1, userId);
            ps.setInt(2, groupId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Asigna un grupo a un proyecto
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

    // Remueve un grupo de un proyecto
    public boolean removeGroupFromProject(int projectId, int groupId) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(REMOVE_GROUP_FROM_PROJECT)) {

            ps.setInt(1, projectId);
            ps.setInt(2, groupId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
