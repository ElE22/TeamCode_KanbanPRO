/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.ColumnDAO;
import com.mycompany.teamcode_kanbanpro.model.Column;
import java.util.List;

/**
 *
 * @author Emanuel
 */
public class ColumnServerHandler {

    private ColumnDAO columnDAO;

    public ColumnServerHandler(ColumnDAO columnDAO) {
        this.columnDAO = columnDAO;
    }

    public Response handleGetColumns(int sprintId) {
        try {
            List<Column> columns = columnDAO.getColumnsByProject(sprintId);
            Response r = new Response(true, "Columnas cargadas exitosamente");
            r.setData(columns);
            return r;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno al cargar columnas: " + ex.getMessage());
        }
    }

}
