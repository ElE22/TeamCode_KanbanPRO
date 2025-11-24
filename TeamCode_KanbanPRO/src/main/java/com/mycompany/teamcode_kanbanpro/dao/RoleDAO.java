/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Role;
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
public class RoleDAO {
    private static final String SELECT_ALL_ROLES = "SELECT id_rol, nombre, descripcion FROM rol";
    private static final String SELECT_ROLE_ID_BY_NAME = "SELECT id_rol FROM rol WHERE nombre = ?";
    private static final String SELECT_ALL_ROLE_NAMES = "SELECT nombre FROM rol ORDER BY nombre";


    private Role RowToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setIdRol(rs.getInt("id_rol"));
        role.setNombre(rs.getString("nombre"));
        role.setDescripcion(rs.getString("descripcion"));
        return role;
    }

    //Obtiene todos los roles
    public List<Role> selectAllRoles() {
        List<Role> roles = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ROLES);
             ResultSet rs = preparedStatement.executeQuery()) {
            
            while (rs.next()) {
                roles.add(RowToRole(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    //Obtiene solo los nombres de todos los roles para llenar el JComboBox.
    public List<String> selectAllRoleNames() {
        List<String> names = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ROLE_NAMES);
             ResultSet rs = preparedStatement.executeQuery()) {
            
            while (rs.next()) {
                names.add(rs.getString("nombre"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

    //Busca el ID de un rol
    public int findRoleIdByName(String roleName) {
        int idRol = -1;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLE_ID_BY_NAME)) {

            preparedStatement.setString(1, roleName);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    idRol = rs.getInt("id_rol");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idRol;
    }
}
