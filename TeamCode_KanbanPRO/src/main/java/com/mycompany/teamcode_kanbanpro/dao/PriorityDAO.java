/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Priority;
import com.mycompany.teamcode_kanbanpro.util.DBUtil; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author escobe11
 */
public class PriorityDAO {
    private static final String SELECT_ALL_PRIORITIES =  "SELECT id_prioridad, nombre, peso, color FROM prioridad ORDER BY peso ASC";
    private static final String SELECT_PRIORITY_BY_ID = "SELECT id_prioridad, nombre, peso, color FROM prioridad WHERE id_prioridad = ?";
    private static final String SELECT_PRIORITY_BY_NAME = "SELECT id_prioridad, nombre, peso, color FROM prioridad WHERE nombre = ?";
    private static final String SELECT_PRIORITY_BY_WEIGHT = "SELECT id_prioridad, nombre, peso, color FROM prioridad WHERE peso = ?";
    private static final String INSERT_PRIORITY = "INSERT INTO prioridad (nombre, peso, color) VALUES (?, ?, ?)";
    private static final String UPDATE_PRIORITY = "UPDATE prioridad SET nombre = ?, peso = ?, color = ? WHERE id_prioridad = ?";
    private static final String DELETE_PRIORITY = "DELETE FROM prioridad WHERE id_prioridad = ?";
    private static final String COUNT_PRIORITY_BY_NAME = "SELECT COUNT(*) FROM prioridad WHERE nombre = ?";
    private static final String COUNT_PRIORITY_BY_WEIGHT = "SELECT COUNT(*) FROM prioridad WHERE peso = ?";
    private static final String COUNT_ALL_PRIORITIES = "SELECT COUNT(*) FROM prioridad";
   
    private Priority RowToPriority(ResultSet rs) throws SQLException {
        Priority priority = new Priority();
        priority.setIdPrioridad(rs.getInt("id_prioridad"));
        priority.setNombre(rs.getString("nombre"));
        priority.setPeso(rs.getInt("peso"));
        priority.setColor(rs.getString("color"));
        return priority;
    }

    public List<Priority> selectAllPriorities() { 
        List<Priority> priorities = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_PRIORITIES);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                priorities.add(RowToPriority(rs));
            }
        } catch (Exception e) {
            System.err.println("Error en selectAllPriorities: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en selectAllPriorities: " + e.getMessage(), e);
        }
        
        return priorities; 
    }

    public Priority selectPriorityById(int idPrioridad) {
        Priority priority = null;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_PRIORITY_BY_ID)) {
            
            stmt.setInt(1, idPrioridad);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    priority = RowToPriority(rs);
                }
            } catch (Exception e) {
                System.err.println("Error en selectPriorityById (ResultSet): " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error en selectPriorityById (ResultSet): " + e.getMessage(), e);
            }
        } catch (Exception e) {
            System.err.println("Error en selectPriorityById: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en selectPriorityById: " + e.getMessage(), e);
        }
        
        return priority;
    }
    
    public Priority selectPriorityByName(String nombre) {
        Priority priority = null;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_PRIORITY_BY_NAME)) {
            
            stmt.setString(1, nombre);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    priority = RowToPriority(rs);
                }
            } catch (Exception e) {
                System.err.println("Error en selectPriorityByName (ResultSet): " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error en selectPriorityByName (ResultSet): " + e.getMessage(), e);
            }
        } catch (Exception e) {
            System.err.println("Error en selectPriorityByName: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en selectPriorityByName: " + e.getMessage(), e);
        }
        
        return priority;
    }
    
    public Priority selectPriorityByWeight(int peso) { 
        Priority priority = null;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_PRIORITY_BY_WEIGHT)) {
            
            stmt.setInt(1, peso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    priority = RowToPriority(rs);
                }
            } catch (Exception e) {
                System.err.println("Error en selectPriorityByWeight (ResultSet): " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error en selectPriorityByWeight (ResultSet): " + e.getMessage(), e);
            }
        } catch (Exception e) {
            System.err.println("Error en selectPriorityByWeight: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en selectPriorityByWeight: " + e.getMessage(), e);
        }
        
        return priority;
    }

    public int insertPriority(Priority priority) {
        int generatedId = -1;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_PRIORITY, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, priority.getNombre());
            stmt.setInt(2, priority.getPeso());
            stmt.setString(3, priority.getColor());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        priority.setIdPrioridad(generatedId);
                    }
                } catch (Exception e) {
                    System.err.println("Error en insertPriority (generatedKeys): " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Error en insertPriority (generatedKeys): " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en insertPriority: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en insertPriority: " + e.getMessage(), e);
        }
        
        return generatedId; 
    }
    
    public boolean updatePriority(Priority priority) {
        boolean rowUpdated = false;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE_PRIORITY)) {
            
            stmt.setString(1, priority.getNombre());
            stmt.setInt(2, priority.getPeso());
            stmt.setString(3, priority.getColor());
            stmt.setInt(4, priority.getIdPrioridad());
            
            rowUpdated = stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error en updatePriority: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en updatePriority: " + e.getMessage(), e);
        }
        return rowUpdated; 
    }
    
    public boolean deletePriority(int idPrioridad) { 
        boolean rowDeleted = false;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_PRIORITY)) {
            
            stmt.setInt(1, idPrioridad);
            
            rowDeleted = stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error en deletePriority: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en deletePriority: " + e.getMessage(), e);
        }
        return rowDeleted; 
    }

    public boolean existsByName(String nombre) {
        boolean exists = false;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_PRIORITY_BY_NAME)) {
            
            stmt.setString(1, nombre);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            } catch (Exception e) {
                System.err.println("Error en existsByName (ResultSet): " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error en existsByName (ResultSet): " + e.getMessage(), e);
            }
        } catch (Exception e) {
            System.err.println("Error en existsByName: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en existsByName: " + e.getMessage(), e);
        }
        
        return exists;
    }
    
    public boolean existsByWeight(int peso) {
        boolean exists = false;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_PRIORITY_BY_WEIGHT)) {
            
            stmt.setInt(1, peso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            } catch (Exception e) {
                System.err.println("Error en existsByWeight (ResultSet): " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error en existsByWeight (ResultSet): " + e.getMessage(), e);
            }
        } catch (Exception e) {
            System.err.println("Error en existsByWeight: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en existsByWeight: " + e.getMessage(), e);
        }
        
        return exists;
    }
    
    public int countPriorities() {
        int count = 0;
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_ALL_PRIORITIES);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error en countPriorities: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en countPriorities: " + e.getMessage(), e);
        }
        
        return count; 
    }
}