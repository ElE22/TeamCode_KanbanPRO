/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.User;
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
public class UserDAO {
    private static final String INSERT_USER_SQL = "INSERT INTO usuario (id_rol, usuario, nombre, email, password, activo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT id_usuario, id_rol, usuario, nombre, email, password, activo, creado_en FROM usuario WHERE id_usuario = ?";
    private static final String SELECT_USER_BY_EMAIL = "SELECT id_usuario, id_rol, usuario, nombre, email, password, activo, creado_en FROM usuario WHERE email = ?";
    private static final String SELECT_USER_BY_CREDENTIAL = "SELECT id_usuario, id_rol, usuario, nombre, email, password, activo, creado_en FROM usuario WHERE usuario = ? OR email = ?";
    private static final String SELECT_ALL_USERS = "SELECT id_usuario, id_rol, usuario, nombre, email, password, activo, creado_en FROM usuario";
    private static final String UPDATE_USER_SQL = "UPDATE usuario SET id_rol = ?, usuario = ?, nombre = ?, email = ?, activo = ? WHERE id_usuario = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM usuario WHERE id_usuario = ?";

    //Mapea un ResultSet a un objeto User.
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setIdUsuario(rs.getInt("id_usuario"));
        user.setIdRol(rs.getInt("id_rol"));
        user.setUsuario(rs.getString("usuario"));
        user.setNombre(rs.getString("nombre"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setActivo(rs.getBoolean("activo"));
        user.setCreadoEn(rs.getTimestamp("creado_en"));
        return user;
    }


    //Inserta un nuevo usuario en la base de datos y donde obetenemos el id del usuario creado
    public boolean insertUser(User user) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, user.getIdRol());
            preparedStatement.setString(2, user.getUsuario());
            preparedStatement.setString(3, user.getNombre());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setBoolean(6, user.isActivo());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {return false;}
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                  user.setIdUsuario(rs.getInt(1));
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    //Obtiene un usuario por su ID
    public User selectUserById(int idUsuario) {
        User user = null;
        try (Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, idUsuario);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // Obtiene una lista de todos los usuarios
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
            ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    //Actualiza los datos de un usuario existente
    public boolean updateUser(User user) {
        boolean rowUpdated = false;
        try (Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL)) {
            preparedStatement.setInt(1, user.getIdRol());
            preparedStatement.setString(2, user.getUsuario());
            preparedStatement.setString(3, user.getNombre());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setBoolean(6, user.isActivo());
            preparedStatement.setInt(7, user.getIdUsuario());

           rowUpdated = preparedStatement.executeUpdate() > 0; // aqui donde retornamos true si el update fue exitosa, false en caso contrario
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    //Elimina un usuario por su ID
    public boolean deleteUser(int idUsuario) {
        boolean rowDeleted = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL)) {
            
            preparedStatement.setInt(1, idUsuario);
            rowDeleted = preparedStatement.executeUpdate() > 0; 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
    
    
    //Obtiene un usuario por su nombre de usuario O su email
    public User selectUserByUsernameOrEmail(String credential) {
        User user = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_CREDENTIAL)) {
            preparedStatement.setString(1, credential); // usuario 
            preparedStatement.setString(2, credential); // o el email
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
 
}
