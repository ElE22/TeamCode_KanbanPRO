/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Group;
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

    //CONSULTAS BÁSICAS
    private static final String INSERT_GROUP
            = "INSERT INTO grupo (nombre, descripcion) VALUES (?, ?)";

    private static final String SELECT_GROUP_BY_ID
            = "SELECT id_grupo, nombre, descripcion FROM grupo WHERE id_grupo = ?";

    private static final String SELECT_ALL_GROUPS
            = "SELECT id_grupo, nombre, descripcion FROM grupo ORDER BY nombre";

    private static final String UPDATE_GROUP
            = "UPDATE grupo SET nombre = ?, descripcion = ? WHERE id_grupo = ?";

    private static final String DELETE_GROUP
            = "DELETE FROM grupo WHERE id_grupo = ?";

    // CONSULTAS DE RELACIONES
    private static final String SELECT_GROUPS_BY_PROJECT
            = "SELECT g.id_grupo, g.nombre, g.descripcion "
            + "FROM grupo g "
            + "JOIN proyecto_grupo pg ON g.id_grupo = pg.id_grupo "
            + "WHERE pg.id_proyecto = ? "
            + "ORDER BY g.nombre";

    private static final String SELECT_GROUPS_BY_USER
            = "SELECT g.id_grupo, g.nombre, g.descripcion "
            + "FROM grupo g "
            + "JOIN usuario_grupo ug ON g.id_grupo = ug.id_grupo "
            + "WHERE ug.id_usuario = ? "
            + "ORDER BY g.nombre";

    // Operaciones N:M
    private static final String ADD_USER_TO_GROUP
            = "INSERT INTO usuario_grupo (id_usuario, id_grupo) VALUES (?, ?)";

    private static final String REMOVE_USER_FROM_GROUP
            = "DELETE FROM usuario_grupo WHERE id_usuario = ? AND id_grupo = ?";

    private static final String ASSIGN_GROUP_TO_PROJECT
            = "INSERT INTO proyecto_grupo (id_proyecto, id_grupo) VALUES (?, ?)";

    private static final String REMOVE_GROUP_FROM_PROJECT
            = "DELETE FROM proyecto_grupo WHERE id_proyecto = ? AND id_grupo = ?";

    private static final String CHECK_GROUP_PROJECT_EXISTS
            = "SELECT COUNT(*) FROM proyecto_grupo WHERE id_proyecto = ? AND id_grupo = ?";

    //Mapea una fila del ResultSet a un objeto Group 
    private Group mapRowToGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setIdGrupo(rs.getInt("id_grupo"));
        group.setNombre(rs.getString("nombre"));
        group.setDescripcion(rs.getString("descripcion"));
        return group;
    }

    //OPERACIONES CRUD   
    public int insertGroup(Group group) {
        int generatedId = -1;
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(INSERT_GROUP, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, group.getNombre());
            ps.setString(2, group.getDescripcion());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al insertar grupo: " + e.getMessage());
            e.printStackTrace();
        }
        return generatedId;
    }

    public Group selectGroupById(int groupId) {
        Group group = null;
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(SELECT_GROUP_BY_ID)) {

            ps.setInt(1, groupId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    group = mapRowToGroup(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al obtener grupo: " + e.getMessage());
            e.printStackTrace();
        }
        return group;
    }

    public List<Group> selectAllGroups() {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(SELECT_ALL_GROUPS); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                groups.add(mapRowToGroup(rs));
            }
        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al obtener todos los grupos: " + e.getMessage());
            e.printStackTrace();
        }
        return groups;
    }

    public boolean updateGroup(Group group) {
        boolean updated = false;
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(UPDATE_GROUP)) {

            ps.setString(1, group.getNombre());
            ps.setString(2, group.getDescripcion());
            ps.setInt(3, group.getIdGrupo());

            updated = ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al actualizar grupo: " + e.getMessage());
            e.printStackTrace();
        }
        return updated;
    }

    public boolean deleteGroup(int groupId) {
        boolean deleted = false;
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_GROUP)) {

            ps.setInt(1, groupId);
            deleted = ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al eliminar grupo: " + e.getMessage());
            e.printStackTrace();
        }
        return deleted;
    }

    public List<Group> selectGroupsByProjectId(int projectId) {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(SELECT_GROUPS_BY_PROJECT)) {

            ps.setInt(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    groups.add(mapRowToGroup(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al obtener grupos del proyecto: " + e.getMessage());
            e.printStackTrace();
        }
        return groups;
    }

    public List<Group> selectGroupsByUserId(int userId) {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(SELECT_GROUPS_BY_USER)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    groups.add(mapRowToGroup(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al obtener grupos del usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return groups;
    }

    public boolean addUserToGroup(int userId, int groupId) {
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(ADD_USER_TO_GROUP)) {

            ps.setInt(1, userId);
            ps.setInt(2, groupId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            // Puede fallar si ya existe la relación (PK duplicada)
            System.err.println("[GroupDAO] Error al añadir usuario a grupo: " + e.getMessage());
            return false;
        }
    }

    public boolean removeUserFromGroup(int userId, int groupId) {
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(REMOVE_USER_FROM_GROUP)) {

            ps.setInt(1, userId);
            ps.setInt(2, groupId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al remover usuario de grupo: " + e.getMessage());
            return false;
        }
    }

    public boolean assignGroupToProject(int projectId, int groupId) {
        // Primero verificar si ya existe la relación
        if (isGroupAssignedToProject(projectId, groupId)) {
            System.out.println("[GroupDAO] El grupo " + groupId
                    + " ya está asignado al proyecto " + projectId);
            return true; // Ya existe, consideramos éxito
        }

        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(ASSIGN_GROUP_TO_PROJECT)) {

            ps.setInt(1, projectId);
            ps.setInt(2, groupId);

            boolean success = ps.executeUpdate() > 0;

            if (success) {
                System.out.println("[GroupDAO] Grupo " + groupId
                        + " asignado al proyecto " + projectId);
            }

            return success;

        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al asignar grupo a proyecto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeGroupFromProject(int projectId, int groupId) {
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(REMOVE_GROUP_FROM_PROJECT)) {

            ps.setInt(1, projectId);
            ps.setInt(2, groupId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al remover grupo de proyecto: " + e.getMessage());
            return false;
        }
    }

    public boolean isGroupAssignedToProject(int projectId, int groupId) {
        try (Connection connection = DBUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(CHECK_GROUP_PROJECT_EXISTS)) {

            ps.setInt(1, projectId);
            ps.setInt(2, groupId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("[GroupDAO] Error al verificar asignación: " + e.getMessage());
        }
        return false;
    }
}
