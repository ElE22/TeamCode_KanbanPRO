/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.dao;

import com.mycompany.teamcode_kanbanpro.model.Column;
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

public class ColumnDAO {

    private static final String SELECT_COLUMNS_BY_PROJECT = "SELECT * FROM columna_kanban WHERE id_proyecto = ? ORDER BY orden";
    private static final String SELECT_COLUMN_BY_ID = "SELECT * FROM columna_kanban WHERE id_columna = ?";
    private static final String INSERT_COLUMN = "INSERT INTO columna_kanban (id_proyecto, nombre, orden, color) VALUES (?, ?, ?, ?)";

    private Column rowToColumn(ResultSet rs) throws SQLException {
        Column column = new Column();
        column.setIdColumna(rs.getInt("id_columna"));
        column.setIdProyecto(rs.getInt("id_proyecto"));
        column.setNombre(rs.getString("nombre"));
        column.setOrden(rs.getInt("orden"));
        column.setColor(rs.getString("color"));
        return column;
    }

    public List<Column> getColumnsByProject(int projectId) throws Exception {
        List<Column> columns = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COLUMNS_BY_PROJECT)) {
            
            preparedStatement.setInt(1, projectId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    columns.add(rowToColumn(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener columnas por Proyecto ID: " + projectId);
            e.printStackTrace();
        }
        return columns;
    }

    public Column getColumnById(int columnId) throws Exception {
        Column column = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COLUMN_BY_ID)) {
            
            preparedStatement.setInt(1, columnId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    column = rowToColumn(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener columna por ID: " + columnId);
            e.printStackTrace();
        }
        return column;
    }

    public void insertColumn(Column column) throws Exception {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COLUMN)) {
            preparedStatement.setInt(1, column.getIdProyecto());
            preparedStatement.setString(2, column.getNombre());
            preparedStatement.setInt(3, column.getOrden());
            preparedStatement.setString(4, column.getColor());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar columna: " + column);
            e.printStackTrace();
        }

    }
}
