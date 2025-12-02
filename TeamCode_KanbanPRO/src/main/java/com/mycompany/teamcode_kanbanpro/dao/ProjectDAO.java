/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Project;
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
public class ProjectDAO {
    
    // consultas basicas
    private static final String INSERT_PROJECT = "INSERT INTO proyecto (id_usuario_creador, nombre, descripcion) VALUES (?, ?, ?)";
    private static final String SELECT_PROJECT_BY_ID = "SELECT p.id_proyecto, p.id_usuario_creador, u.nombre as nombre_creador, p.nombre, p.descripcion, p.fecha_creacion " +
            "FROM proyecto p JOIN usuario u ON p.id_usuario_creador = u.id_usuario WHERE p.id_proyecto = ?";
    private static final String SELECT_ALL_PROJECTS = "SELECT p.id_proyecto, p.id_usuario_creador, u.nombre as nombre_creador, p.nombre, p.descripcion, p.fecha_creacion " +
            "FROM proyecto p JOIN usuario u ON p.id_usuario_creador = u.id_usuario ORDER BY p.fecha_creacion DESC";
    private static final String DELETE_PROJECT = "DELETE FROM proyecto WHERE id_proyecto = ?";
    private static final String UPDATE_PROJECT = "UPDATE proyecto SET nombre = ?, descripcion = ? WHERE id_proyecto = ?";
            
    // consulta para obtener proyectos basados en la membresia del usuario a un grupo relacionado al proyecto
    private static final String SELECT_PROJECTS_BY_USER_ID = 
            "SELECT DISTINCT p.id_proyecto, p.id_usuario_creador, u.nombre as nombre_creador, p.nombre, p.descripcion, p.fecha_creacion " +
            "FROM proyecto p " +
            "JOIN usuario u ON p.id_usuario_creador = u.id_usuario " +
            "JOIN proyecto_grupo pg ON p.id_proyecto = pg.id_proyecto " + // relacion n:m con grupo
            "JOIN usuario_grupo ug ON pg.id_grupo = ug.id_grupo " + // membresia del usuario al grupo
            "WHERE ug.id_usuario = ?";

    private static final String SELECT_PROJECTS_AND_GROUPS_BY_USER_ID = 
            "SELECT p.id_proyecto, p.nombre AS nombre, p.descripcion, p.fecha_creacion, " +
            "u_creator.nombre AS nombre_creador, p.id_usuario_creador, " +
            "GROUP_CONCAT(g.nombre SEPARATOR ', ') AS grupos_a_los_que_pertenezco " +
            "FROM usuario u " +
            "JOIN usuario_grupo ug ON u.id_usuario = ug.id_usuario " +
            "JOIN grupo g ON ug.id_grupo = g.id_grupo " +
            "JOIN proyecto_grupo pg ON g.id_grupo = pg.id_grupo " +
            "JOIN proyecto p ON pg.id_proyecto = p.id_proyecto " +
            "JOIN usuario u_creator ON p.id_usuario_creador = u_creator.id_usuario " +
            "WHERE u.id_usuario = ? " +
            "GROUP BY p.id_proyecto, p.nombre, p.descripcion, p.fecha_creacion, u_creator.nombre, p.id_usuario_creador " +
            "ORDER BY p.nombre";
    
    // mapea una fila del resultset a un objeto project
    private Project RowToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setIdProyecto(rs.getInt("id_proyecto"));
        project.setIdUsuarioCreador(rs.getInt("id_usuario_creador"));
        project.setNombre(rs.getString("nombre"));
        project.setDescripcion(rs.getString("descripcion"));
        project.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        // incluye el nombre del creador en el objeto project
        try {
            project.setNombreCreador(rs.getString("nombre_creador"));
        } catch (SQLException e) {
            // ignora si la columna nombre_creador no esta en el resultset
        }
        return project;
    }

    // inserta un nuevo proyecto
    public int insertProject(Project project) {
        int generatedId = -1;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROJECT, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setInt(1, project.getIdUsuarioCreador());
            preparedStatement.setString(2, project.getNombre());
            preparedStatement.setString(3, project.getDescripcion());
            
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

    // obtiene un proyecto por su id
    public Project selectProjectById(int projectId) {
        Project project = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROJECT_BY_ID)) {
            
            preparedStatement.setInt(1, projectId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    project = RowToProject(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }
    
    // obtiene todos los proyectos
    public List<Project> selectAllProjects() {
        List<Project> projects = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PROJECTS);
             ResultSet rs = preparedStatement.executeQuery()) {
            
            while (rs.next()) {
                projects.add(RowToProject(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projects;
    }
    
    // elimina un proyecto
    public boolean deleteProject(int projectId) {
        boolean rowDeleted = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROJECT)) {
            
            preparedStatement.setInt(1, projectId);
            rowDeleted = preparedStatement.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }

    // actualiza un proyecto
    public boolean updateProject(Project project) {
        boolean rowUpdated = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PROJECT)) {
            
            preparedStatement.setString(1, project.getNombre());
            preparedStatement.setString(2, project.getDescripcion());
            preparedStatement.setInt(3, project.getIdProyecto());
            
            rowUpdated = preparedStatement.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    // obtiene todos los proyectos a los que pertenece un usuario (via grupo)
    public List<Project> selectProjectsByUserId(int userId) {
        List<Project> projects = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROJECTS_AND_GROUPS_BY_USER_ID)) {
            
            preparedStatement.setInt(1, userId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Project project = RowToProject(rs);
                    
                    // AQUI EXTRAEMOS LA COLUMNA CONCATENADA Y LA GUARDAMOS EN UNA PROPIEDAD DEL MODELO
                    // Asumire que crearas un campo string en project.java como 'gruposPertenencia'
                    // pero ya que no puedo modificarlo, por ahora solo recuperamos el string y lo ignoramos
                    // si necesitas usar ese string, deberias modificar el modelo project.
                    
                    String gruposConcatenados = rs.getString("grupos_a_los_que_pertenezco");
                    
                    // Si modificas el modelo Project para incluir setGruposPertenencia(String), descomenta:
                    // project.setGruposPertenencia(gruposConcatenados);

                    project.setGruposPertenencia(gruposConcatenados);
                    
                    projects.add(project);
                }
                return projects;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projects;
    }
}
