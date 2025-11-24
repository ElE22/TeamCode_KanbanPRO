package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.RoleDAO;
import java.util.List;

/**
 *
 * @author Emanuel
 */
public class RoleServerHandler {
    private final RoleDAO roleDAO;

    public RoleServerHandler(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    public Response handleGetRoles() {
        try {
            
            List<String> roleNames = roleDAO.selectAllRoleNames();

            if (roleNames == null || roleNames.isEmpty()) {
                // Si no hay roles en la DB
                Response r = new Response(true, "No hay roles definidos en el sistema.");
                r.setData(roleNames); // Devuelve lista vacía
                return r;
            }

            // Ddevuelve la lista de nombres de roles
            Response r = new Response(true, "Roles obtenidos exitosamente.");
            r.setData(roleNames); 
            return r;

        } catch (Exception ex) {
            ex.printStackTrace();
            return new Response(false, "Error interno del servidor al obtener roles: " + ex.getMessage());
        }
    }
}

