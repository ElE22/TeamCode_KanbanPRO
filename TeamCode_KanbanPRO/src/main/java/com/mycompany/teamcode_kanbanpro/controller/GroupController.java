/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.controller;
import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Group;
import com.mycompany.teamcode_kanbanpro.model.User;
import com.mycompany.teamcode_kanbanpro.view.AsignarUsuarioGrupoView;
import com.mycompany.teamcode_kanbanpro.view.CrearGrupoView;
import com.mycompany.teamcode_kanbanpro.view.GrupoView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author salaz
 */
public class GroupController {

    private GrupoView view;
    private ClientConnector connector;
    private DefaultTableModel modeloGrupos;
    private PermissionManager permission;

    private int grupoSeleccionadoId = -1;
    private String grupoSeleccionadoNombre = ""; // Variable añadida para guardar nombre 

    public GroupController(GrupoView view, ClientConnector connector, PermissionManager permission) {
        this.view = view;
        this.connector = connector;
        this.permission = permission;
        initialize();
    }


    private void initialize() {
        modeloGrupos = view.getModeloGrupos();

        configurarPermisos();

        view.getTablaGrupos().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable tabla = (JTable) e.getSource();
                int filaSeleccionada = tabla.getSelectedRow();

                if (filaSeleccionada != -1) {
                    grupoSeleccionadoId = (Integer) modeloGrupos.getValueAt(filaSeleccionada, 0);
                  //Guardar también el nombre del grupo 
                    grupoSeleccionadoNombre = (String) modeloGrupos.getValueAt(filaSeleccionada, 1);
                }
            }
        });

  
        cargarTodosLosGrupos();
    }

    private void configurarPermisos() {
        if (permission.isScrumOrProduct()) {
            view.getBtnCrearGrupo().addActionListener(e -> crearNuevoGrupo());
            view.getBtnUnirAGrupo().addActionListener(e -> añadirUsuarioGrupo());
        } else {
            view.getPanelBotones().remove(view.getBtnCrearGrupo());
            view.getPanelBotones().remove(view.getBtnUnirAGrupo());
        }

        view.getPanelBotones().revalidate();
        view.getPanelBotones().repaint();
    }

    //Método para cargar grupos desde el servidor 
    public void cargarTodosLosGrupos() {
        try {
            Request req = new Request();
            req.setAction("getAllGroups");

            Response response = connector.sendRequest(req);

            if (response.isSuccess() && response.getData() != null) {
                List<Group> grupos = (List<Group>) response.getData();
                actualizarTablaGrupos(grupos);
            } else {
                JOptionPane.showMessageDialog(view,
                        "Error al cargar grupos: " + response.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Error de conexión al cargar grupos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Método para actualizar la tabla con los datos 
    private void actualizarTablaGrupos(List<Group> grupos) {
        modeloGrupos.setRowCount(0);

        for (Group grupo : grupos) {
            int cantidadMiembros = contarMiembrosGrupo(grupo.getIdGrupo());
            Object[] fila = {
                grupo.getIdGrupo(),
                grupo.getNombre(),
                grupo.getDescripcion() != null ? grupo.getDescripcion() : "",
                cantidadMiembros
            };
            modeloGrupos.addRow(fila);
        }
    }

    //  Método para contar miembros de un grupo
    private int contarMiembrosGrupo(int idGrupo) {
        try {
            Request req = new Request();
            req.setAction("getUsersByGroup");
            Map<String, Object> payload = new HashMap<>();
            payload.put("idGrupo", idGrupo);
            req.setPayload(payload);

            Response response = connector.sendRequest(req);

            if (response.isSuccess() && response.getData() != null) {
                List<User> miembros = (List<User>) response.getData();
                return miembros.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void crearNuevoGrupo() {
        CrearGrupoView formulario = new CrearGrupoView();

        formulario.getBtnCrear().addActionListener(e -> {
            crearGrupoEnServidor(formulario);
        });

        //Configurar listener del botón Cancelar
        formulario.getBtnCancelar().addActionListener(e -> {
            formulario.dispose();
        });

        formulario.setVisible(true);
    }

    // Método completo para enviar grupo al servidor
    private void crearGrupoEnServidor(CrearGrupoView formulario) {
        String nombre = formulario.getTxtNombre().getText().trim();
        String descripcion = formulario.getTxtDescripcion().getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(formulario,
                    "El nombre del grupo es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            formulario.getTxtNombre().requestFocus();
            return;
        }

        if (nombre.length() < 3) {
            JOptionPane.showMessageDialog(formulario,
                    "El nombre debe tener al menos 3 caracteres.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            formulario.getTxtNombre().requestFocus();
            return;
        }

        if (nombre.length() > 100) {
            JOptionPane.showMessageDialog(formulario,
                    "El nombre no puede exceder 100 caracteres.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            formulario.getTxtNombre().requestFocus();
            return;
        }

        try {
            Request req = new Request();
            req.setAction("createGroup");

            Map<String, Object> payload = new HashMap<>();
            payload.put("nombre", nombre);
            payload.put("descripcion", descripcion);
            req.setPayload(payload);

            Response response = connector.sendRequest(req);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(formulario,
                        "Grupo '" + nombre + "' creado exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                formulario.dispose();
                cargarTodosLosGrupos(); // Refrescar tabla
            } else {
                JOptionPane.showMessageDialog(formulario,
                        "Error: " + response.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(formulario,
                    "Error de conexión: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    private void añadirUsuarioGrupo() {
        // Verificar que hay un grupo seleccionado
        int filaSeleccionada = view.getTablaGrupos().getSelectedRow();

        if (filaSeleccionada == -1 || grupoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(view,
                    "Debe seleccionar un grupo antes de asignar usuarios.\n\n"
                    + "Haga clic en un grupo de la tabla.",
                    "Grupo no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

       
        AsignarUsuarioGrupoView formulario = new AsignarUsuarioGrupoView();
        formulario.setGrupo(grupoSeleccionadoId, grupoSeleccionadoNombre);

        //Cargar datos iniciales
        cargarUsuariosDisponibles(formulario);
        cargarMiembrosGrupo(formulario, grupoSeleccionadoId);

        //Configurar botón Agregar 
        formulario.getBtnAgregar().addActionListener(e -> {
            agregarUsuarioAlGrupo(formulario);
        });
        
        //Configurar botón Quitar 
        formulario.getBtnQuitar().addActionListener(e -> {
            quitarUsuarioDelGrupo(formulario);
        });
        
        // Configurar botón Cerrar
        formulario.getBtnCerrar().addActionListener(e -> {
            formulario.dispose();
            cargarTodosLosGrupos(); // Refrescar tabla principal
        });

        formulario.setVisible(true);
    }

    //cargar usuarios en ComboBox 
    private void cargarUsuariosDisponibles(AsignarUsuarioGrupoView formulario) {
        try {
            Request req = new Request();
            req.setAction("getAllUsers");

            Response response = connector.sendRequest(req);

            if (response.isSuccess() && response.getData() != null) {
                List<User> todosUsuarios = (List<User>) response.getData();

                formulario.getCmbUsuarios().removeAllItems();
                formulario.getCmbUsuarios().addItem("-- Seleccionar Usuario --");

                // Obtener miembros actuales para filtrar
                List<Integer> idsMiembros = obtenerIdsMiembrosGrupo(formulario.getIdGrupo());

                for (User usuario : todosUsuarios) {
                    // Solo agregar si NO es miembro del grupo
                    if (!idsMiembros.contains(usuario.getIdUsuario())) {
                        String item = usuario.getIdUsuario() + " - " + usuario.getUsuario() + " (" + usuario.getNombre() + ")";
                        formulario.getCmbUsuarios().addItem(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(formulario,
                    "Error al cargar usuarios: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para obtener IDs de miembros actuales
    private List<Integer> obtenerIdsMiembrosGrupo(int idGrupo) {
        List<Integer> ids = new java.util.ArrayList<>();
        try {
            Request req = new Request();
            req.setAction("getUsersByGroup");
            Map<String, Object> payload = new HashMap<>();
            payload.put("idGrupo", idGrupo);
            req.setPayload(payload);

            Response response = connector.sendRequest(req);

            if (response.isSuccess() && response.getData() != null) {
                List<User> miembros = (List<User>) response.getData();
                for (User u : miembros) {
                    ids.add(u.getIdUsuario());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    //Método para cargar miembros en tabla
    private void cargarMiembrosGrupo(AsignarUsuarioGrupoView formulario, int idGrupo) {
        try {
            Request req = new Request();
            req.setAction("getUsersByGroup");
            Map<String, Object> payload = new HashMap<>();
            payload.put("idGrupo", idGrupo);
            req.setPayload(payload);
            Response response = connector.sendRequest(req);
            if (response.isSuccess() && response.getData() != null) {
                List<User> miembros = (List<User>) response.getData();
                DefaultTableModel modelo = formulario.getModeloMiembros();
                modelo.setRowCount(0);
                for (User usuario : miembros) {
                    Object[] fila = {
                        usuario.getIdUsuario(),
                        usuario.getUsuario(),
                        usuario.getNombre()
                    };
                    modelo.addRow(fila);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(formulario,
                    "Error al cargar miembros: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // agregar usuario al grupo
    private void agregarUsuarioAlGrupo(AsignarUsuarioGrupoView formulario) {
        String seleccion = (String) formulario.getCmbUsuarios().getSelectedItem();

        if (seleccion == null || seleccion.equals("-- Seleccionar Usuario --")) {
            JOptionPane.showMessageDialog(formulario,
                    "Debe seleccionar un usuario del listado.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extraer el ID del usuario del formato "ID - Usuario (Nombre)"
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(seleccion.split(" - ")[0]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(formulario,
                    "Error al procesar la selección.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Request req = new Request();
            req.setAction("joinGroup");

            Map<String, Object> payload = new HashMap<>();
            payload.put("idUsuario", idUsuario);
            payload.put("idGrupo", formulario.getIdGrupo());
            req.setPayload(payload);

            Response response = connector.sendRequest(req);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(formulario,
                        "Usuario agregado al grupo exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Refrescar las listas
                cargarUsuariosDisponibles(formulario);
                cargarMiembrosGrupo(formulario, formulario.getIdGrupo());
            } else {
                JOptionPane.showMessageDialog(formulario,
                        "Error: " + response.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(formulario,
                    "Error de conexión: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // quitar usuario del grupo 
    private void quitarUsuarioDelGrupo(AsignarUsuarioGrupoView formulario) {
        int filaSeleccionada = formulario.getTablaMiembros().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(formulario,
                    "Debe seleccionar un usuario de la tabla de miembros.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = (int) formulario.getModeloMiembros().getValueAt(filaSeleccionada, 0);
        String nombreUsuario = (String) formulario.getModeloMiembros().getValueAt(filaSeleccionada, 2);

        int confirmacion = JOptionPane.showConfirmDialog(formulario,
                "¿Está seguro de quitar a '" + nombreUsuario + "' del grupo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Request req = new Request();
            req.setAction("leaveGroup");

            Map<String, Object> payload = new HashMap<>();
            payload.put("idUsuario", idUsuario);
            payload.put("idGrupo", formulario.getIdGrupo());
            req.setPayload(payload);

            Response response = connector.sendRequest(req);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(formulario,
                        "Usuario removido del grupo exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Refrescar las listas
                cargarUsuariosDisponibles(formulario);
                cargarMiembrosGrupo(formulario, formulario.getIdGrupo());
            } else {
                JOptionPane.showMessageDialog(formulario,
                        "Error: " + response.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(formulario,
                    "Error de conexión: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  
    public int getGrupoSeleccionadoId() {
        return grupoSeleccionadoId;
    }

    public String getGrupoSeleccionadoNombre() {
        return grupoSeleccionadoNombre;
    }
}
