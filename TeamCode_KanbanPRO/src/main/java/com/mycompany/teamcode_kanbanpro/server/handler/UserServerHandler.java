package com.mycompany.teamcode_kanbanpro.server.handler;

import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.dao.UserDAO;
import com.mycompany.teamcode_kanbanpro.dao.RoleDAO;
import com.mycompany.teamcode_kanbanpro.model.User;
import com.mycompany.teamcode_kanbanpro.util.BCryptUtil;
import java.util.Map;
/**
 *
 * @author Emanuel
 */
public class UserServerHandler {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    public UserServerHandler(UserDAO userDAO, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
    }

    //Procesa la solicitud de registro de usuario
    public Response handleRegister(Request req) {
        try {
            Map<String, Object> p = req.getPayload();
            
            //obetenr datos del playload
            String nombre = (String) p.get("nombre");
            String usuario = (String) p.get("usuario");
            String email = (String) p.get("email");
            // Nota: Aquí se recibe la clave simple (sin confirmar) desde el cliente
            String passwordPlain = (String) p.get("clave"); 
            String rolNombre = (String) p.get("rolNombre");
            
            // validacion de campos vacios o nulos
            if (nombre == null || nombre.isEmpty() || usuario == null || usuario.isEmpty() || 
                email == null || email.isEmpty() || passwordPlain == null || passwordPlain.isEmpty() || 
                rolNombre == null || rolNombre.isEmpty()) {
                return new Response(false, "Faltan datos obligatorios para el registro.");
            }

            // Verifiacar si ya existen
            if (userDAO.selectUserByUsernameOrEmail(usuario) != null) {
                 return new Response(false, "El nombre de usuario ya está registrado.");
            }
            if (userDAO.selectUserByUsernameOrEmail(email) != null) {
                 return new Response(false, "El correo electrónico ya está registrado.");
            }

            //Obtener ID del rol
            int idRol = roleDAO.findRoleIdByName(rolNombre);
            if (idRol == -1) {
                return new Response(false, "Error de configuración: Rol no válido.");
            }
            
            //Hash clave
            String hashedPassword = BCryptUtil.hashPwd(passwordPlain); // Usar el método correcto

            // objeto & inset en DB con userDAO
            User newUser = new User();
            newUser.setNombre(nombre);
            newUser.setUsuario(usuario);
            newUser.setEmail(email);
            newUser.setPassword(hashedPassword);
            newUser.setIdRol(idRol);
            newUser.setActivo(true);

            boolean success = userDAO.insertUser(newUser);

            if (success) {
                Response response = new Response(true, String.format("Usuario '%s' registrado exitosamente.", newUser.getUsuario()));
                return response;
            } else {
                return new Response(false, "Error desconocido al guardar el usuario en la base de datos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error interno del servidor en UserS_H: " + e.getMessage());
        }
    }
}
