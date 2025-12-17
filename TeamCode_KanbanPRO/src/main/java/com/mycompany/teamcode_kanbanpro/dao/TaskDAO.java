/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Task;
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

public class TaskDAO {
    
    // consulta base que une tarea con todas sus fk y maneja los campos null
    private static final String BASE_SELECT = 
            "SELECT t.id_tarea, t.id_proyecto, p.nombre AS nombre_proyecto, t.id_sprint, s.nombre AS nombre_sprint, " +
            "t.id_columna, ck.nombre AS nombre_columna, t.id_prioridad, pr.nombre AS nombre_prioridad, t.titulo, " +
            "t.descripcion, t.fecha_creacion, t.fecha_vencimiento, t.fecha_modificacion, t.creado_por, uc.nombre AS nombre_creador, " +
            "t.id_tarea_padre, tp.titulo AS titulo_tarea_padre " +
            "FROM tarea t " +
            "JOIN proyecto p ON t.id_proyecto = p.id_proyecto " +
            "JOIN columna_kanban ck ON t.id_columna = ck.id_columna " +
            "JOIN prioridad pr ON t.id_prioridad = pr.id_prioridad " +
            "JOIN usuario uc ON t.creado_por = uc.id_usuario " + // creador
            "LEFT JOIN sprint s ON t.id_sprint = s.id_sprint " + // puede ser null
            "LEFT JOIN tarea tp ON t.id_tarea_padre = tp.id_tarea "; // puede ser null (subtarea)
    
    private static final String BASE_SELECT_WITH_GROUPS = """
        SELECT 
            t.id_tarea,
            t.id_proyecto,
            p.nombre AS nombre_proyecto,
            t.id_sprint,
            s.nombre AS nombre_sprint,
            t.id_columna,
            ck.nombre AS nombre_columna,
            t.id_prioridad,
            pr.nombre AS nombre_prioridad,
            t.titulo,
            t.descripcion,
            t.fecha_creacion,
            t.fecha_vencimiento,
            t.fecha_modificacion,
            t.creado_por,
            uc.nombre AS nombre_creador,
            t.id_tarea_padre,
            tp.titulo AS titulo_tarea_padre,
            GROUP_CONCAT(g.nombre SEPARATOR ', ') AS grupos
        FROM tarea t
        JOIN proyecto p ON t.id_proyecto = p.id_proyecto
        JOIN columna_kanban ck ON t.id_columna = ck.id_columna
        JOIN prioridad pr ON t.id_prioridad = pr.id_prioridad
        JOIN usuario uc ON t.creado_por = uc.id_usuario
        LEFT JOIN sprint s ON t.id_sprint = s.id_sprint
        LEFT JOIN tarea tp ON t.id_tarea_padre = tp.id_tarea
        LEFT JOIN proyecto_grupo pg ON t.id_proyecto = pg.id_proyecto
        LEFT JOIN grupo g ON pg.id_grupo = g.id_grupo
        WHERE t.id_sprint = ?
        GROUP BY t.id_tarea
    """;

    private static final String BASE_SELECT_WITH_GROUPS_BY_TaskID = """
            SELECT 
                t.id_tarea,
                t.id_proyecto,
                p.nombre AS nombre_proyecto,
                t.id_sprint,
                s.nombre AS nombre_sprint,
                t.id_columna,
                ck.nombre AS nombre_columna,
                t.id_prioridad,
                pr.nombre AS nombre_prioridad,
                t.titulo,
                t.descripcion,
                t.fecha_creacion,
                t.fecha_vencimiento,
                t.fecha_modificacion,
                t.creado_por,
                uc.nombre AS nombre_creador,
                t.id_tarea_padre,
                tp.titulo AS titulo_tarea_padre,
                GROUP_CONCAT(g.nombre SEPARATOR ', ') AS grupos
            FROM tarea t
            JOIN proyecto p ON t.id_proyecto = p.id_proyecto
            JOIN columna_kanban ck ON t.id_columna = ck.id_columna
            JOIN prioridad pr ON t.id_prioridad = pr.id_prioridad
            JOIN usuario uc ON t.creado_por = uc.id_usuario
            LEFT JOIN sprint s ON t.id_sprint = s.id_sprint
            LEFT JOIN tarea tp ON t.id_tarea_padre = tp.id_tarea
            LEFT JOIN proyecto_grupo pg ON t.id_proyecto = pg.id_proyecto
            LEFT JOIN grupo g ON pg.id_grupo = g.id_grupo
            WHERE t.id_tarea = ? 
            GROUP BY t.id_tarea
            """;
            
    // consultas CRUD
    private static final String INSERT_TASK = 
            "INSERT INTO tarea (id_proyecto, id_sprint, id_columna, id_prioridad, titulo, descripcion, fecha_vencimiento, creado_por, id_tarea_padre) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_TASK_BY_ID = BASE_SELECT + " WHERE t.id_tarea = ?";
    private static final String SELECT_TASKS_BY_PROJECT = BASE_SELECT + " WHERE t.id_proyecto = ? ORDER BY t.id_tarea_padre, t.id_tarea";
    private static final String UPDATE_TASK = 
            "UPDATE tarea SET id_sprint = ?, id_columna = ?, id_prioridad = ?, titulo = ?, descripcion = ?, fecha_vencimiento = ?, id_tarea_padre = ? " +
            "WHERE id_tarea = ?";
    private static final String DELETE_TASK = "DELETE FROM tarea WHERE id_tarea = ?";
    
    private static final String SELECT_TASKS_BY_SPRINT_WITH_GROUPS = BASE_SELECT_WITH_GROUPS + "ORDER BY t.id_tarea";
    private static final String SELECT_USERS_BY_TASK = 
    "SELECT u.id_usuario, u.nombre, u.correo FROM usuario u " +
    "JOIN usuario_tarea ut ON u.id_usuario = ut.id_usuario " +
    "WHERE ut.id_tarea = ?";

    // consultas de asignacion (usuario_tarea)
    private static final String ASSIGN_USER_TO_TASK = "INSERT INTO usuario_tarea (id_usuario, id_tarea) VALUES (?, ?)";
    private static final String REMOVE_USER_FROM_TASK = "DELETE FROM usuario_tarea WHERE id_usuario = ? AND id_tarea = ?";
    private static final String UPDATE_TASK_COLUMN = "UPDATE tarea SET id_columna = ? WHERE id_tarea = ?";

    // mapea una fila del resultset a un objeto task
    private Task RowToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setIdTarea(rs.getInt("id_tarea"));
        task.setIdProyecto(rs.getInt("id_proyecto"));
        task.setIdColumna(rs.getInt("id_columna"));
        task.setIdPrioridad(rs.getInt("id_prioridad"));
        task.setTitulo(rs.getString("titulo"));
        task.setDescripcion(rs.getString("descripcion"));
        task.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        task.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
        task.setFechaModificacion(rs.getTimestamp("fecha_modificacion"));
        task.setCreadoPor(rs.getInt("creado_por"));
        
        // campos que pueden ser null
        task.setIdSprint((Integer) rs.getObject("id_sprint"));
        task.setIdTareaPadre((Integer) rs.getObject("id_tarea_padre"));

        // campos extra
        task.setNombreProyecto(rs.getString("nombre_proyecto"));
        task.setNombreSprint(rs.getString("nombre_sprint")); // puede ser null
        task.setNombreColumna(rs.getString("nombre_columna"));
        task.setNombrePrioridad(rs.getString("nombre_prioridad"));
        task.setNombreCreador(rs.getString("nombre_creador"));
        task.setTituloTareaPadre(rs.getString("titulo_tarea_padre")); // puede ser null
        task.setGruposAsignados(rs.getString("grupos"));
        return task;
    }

    // inserta una nueva tarea
    public int insertTask(Task task) {
        int generatedId = -1;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TASK, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setInt(1, task.getIdProyecto());
            
            // maneja id_sprint (null)
            if (task.getIdSprint() != null) {
                preparedStatement.setInt(2, task.getIdSprint());
            } else {
                preparedStatement.setNull(2, java.sql.Types.INTEGER);
            }
            
            preparedStatement.setInt(3, task.getIdColumna());
            preparedStatement.setInt(4, task.getIdPrioridad());
            preparedStatement.setString(5, task.getTitulo());
            preparedStatement.setString(6, task.getDescripcion());
            preparedStatement.setDate(7, task.getFechaVencimiento());
            preparedStatement.setInt(8, task.getCreadoPor());
            
            // maneja id_tarea_padre (null)
            if (task.getIdTareaPadre() != null) {
                preparedStatement.setInt(9, task.getIdTareaPadre());
            } else {
                preparedStatement.setNull(9, java.sql.Types.INTEGER);
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

    // obtiene una tarea por su id
    public Task selectTaskById(int taskId) {
        Task task = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TASK_BY_ID)) {
            
            preparedStatement.setInt(1, taskId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    task = RowToTask(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }
    
    // obtiene todas las tareas de un proyecto
    public List<Task> selectTasksByProjectId(int projectId) {
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TASKS_BY_PROJECT)) {
            
            preparedStatement.setInt(1, projectId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    tasks.add(RowToTask(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
    
    // actualiza una tarea
    public boolean updateTask(Task task) {
        boolean rowUpdated = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TASK)) {
            
            // maneja id_sprint (null)
            if (task.getIdSprint() != null) {
                preparedStatement.setInt(1, task.getIdSprint());
            } else {
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
            }
            
            preparedStatement.setInt(2, task.getIdColumna());
            preparedStatement.setInt(3, task.getIdPrioridad());
            preparedStatement.setString(4, task.getTitulo());
            preparedStatement.setString(5, task.getDescripcion());
            preparedStatement.setDate(6, task.getFechaVencimiento());
            
            // maneja id_tarea_padre (null)
            if (task.getIdTareaPadre() != null) {
                preparedStatement.setInt(7, task.getIdTareaPadre());
            } else {
                preparedStatement.setNull(7, java.sql.Types.INTEGER);
            }
            
            preparedStatement.setInt(8, task.getIdTarea());
            
            rowUpdated = preparedStatement.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    // elimina una tarea
    public boolean deleteTask(int taskId) {
        boolean rowDeleted = false;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TASK)) {
            
            preparedStatement.setInt(1, taskId);
            rowDeleted = preparedStatement.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
    

    // asigna un usuario a una tarea
    public boolean assignUserToTask(int userId, int taskId) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN_USER_TO_TASK)) {
            
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, taskId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // remueve un usuario de una tarea
    public boolean removeUserFromTask(int userId, int taskId) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_USER_FROM_TASK)) {
            
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, taskId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Task> selectTasksBySprintId(int sprintId) {
        List<Task> tasks = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TASKS_BY_SPRINT_WITH_GROUPS)) {
            
            preparedStatement.setInt(1, sprintId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Task task = RowToTask(rs);
                    tasks.add(task);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al obtener tareas por Sprint ID: " + sprintId);
            e.printStackTrace();
        }
        return tasks;
    }

    public Task selectTaskWithGroupsById(int taskId) {
        Task task = null;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(BASE_SELECT_WITH_GROUPS_BY_TaskID)) {
            
            preparedStatement.setInt(1, taskId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    task = RowToTask(rs);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al obtener tarea con grupos por ID: " + taskId);
            e.printStackTrace();
        }
        return task;

    }

    public Task updateTaskColumn(int taskId, int newColumnId) throws Exception {

        try (Connection connection = DBUtil.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_TASK_COLUMN)) {

            ps.setInt(1, newColumnId);
            ps.setInt(2, taskId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                Task updatedTask = selectTaskWithGroupsById(taskId);
                return updatedTask;
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar la columna de la tarea en la DB: " + e.getMessage());
            return null;
        }
    }

    public List<User> selectUsersByTaskId(int taskId) throws Exception {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_USERS_BY_TASK)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    // u.setCorreo(rs.getString("correo"));
                    users.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // 2. Obtener las subtareas de una tarea (Recursivo)
    // Nota: Usamos BASE_SELECT para que cada subtarea también tenga sus nombres de
    // prioridad, columna, etc.
    public List<Task> selectSubtasksByParentId(int parentTaskId) throws Exception {
        List<Task> subtasks = new ArrayList<>();
        String query = BASE_SELECT + " WHERE t.id_tarea_padre = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, parentTaskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task sub = RowToTask(rs);
                    // Opcional: Si quieres que las subtareas también muestren sus usuarios en el
                    // diálogo
                    sub.setUsuariosAsignados(selectUsersByTaskId(sub.getIdTarea()));
                    subtasks.add(sub);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subtasks;
    }
}
