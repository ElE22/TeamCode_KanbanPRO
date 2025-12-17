package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Project;
import com.mycompany.teamcode_kanbanpro.model.Sprint;
import com.mycompany.teamcode_kanbanpro.model.Group;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;
import com.mycompany.teamcode_kanbanpro.view.CrearSprintView;
import com.mycompany.teamcode_kanbanpro.view.CrearProyectoView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author Emanuel
 */
public class ProyectosCardController {

    private ProyectosView view;
    private ClientConnector connector;
    private DefaultTableModel modeloProyectos;
    private DefaultTableModel modeloSprints;
    private PermissionManager permission;
    private KanbanBoardController kanbanControllerAbierto = null;

    private int proyectoSeleccionadoId = -1;
    private int sprintSeleccionadoId = -1;
    private String proyectoSeleccionadoNombre = null;
    private String sprintSeleccionadoNombre = null;
    
    // Variable para verificar si usuario tiene grupos 
    private boolean usuarioTieneGrupos = false;

    public ProyectosCardController(ProyectosView view, ClientConnector connector, PermissionManager permission) {
        this.view = view;
        this.connector = connector;
        this.permission = permission;
        initialize();
        
        // Verificar grupos antes de cargar proyectos
        verificarGruposYCargarProyectos();
    }

    private void initialize() {
        modeloProyectos = view.getModeloProyectos();
        modeloSprints = view.getModeloSprints();

        configurarPermisos();

        // Listener para selección de proyecto
        view.getTablaProyectos().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sprintSeleccionadoId = -1; 
                sprintSeleccionadoNombre = null;
                JTable tabla = (JTable) e.getSource();
                int filaSeleccionada = tabla.getSelectedRow();

                if (filaSeleccionada != -1) {
                    proyectoSeleccionadoId = (Integer) modeloProyectos.getValueAt(filaSeleccionada, 0);
                    proyectoSeleccionadoNombre = (String) modeloProyectos.getValueAt(filaSeleccionada, 1);
                    cargarSprintsParaProyecto(proyectoSeleccionadoId);
                    
                }
            }
        });
        
        // Listener para selección de sprint (abre Kanban)
        view.getTablaSprints().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable tabla = (JTable) e.getSource();
                int filaSeleccionada = tabla.getSelectedRow();

                if (filaSeleccionada != -1) {
                    sprintSeleccionadoId = (Integer) modeloSprints.getValueAt(filaSeleccionada, 0);
                    sprintSeleccionadoNombre = (String) modeloSprints.getValueAt(filaSeleccionada, 1);

                    if (kanbanControllerAbierto != null && kanbanControllerAbierto.isVisible()) {
                        kanbanControllerAbierto.toFront();
                        return;
                    }

                    kanbanControllerAbierto = new KanbanBoardController(
                            connector,
                            sprintSeleccionadoId,
                            proyectoSeleccionadoId, proyectoSeleccionadoNombre,
                            sprintSeleccionadoNombre, permission);

                    kanbanControllerAbierto.getView().addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            kanbanControllerAbierto = null; 
                        }
                    });
                }
            }
        });
        
        view.getTablaSprints().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = view.getTablaSprints().getSelectedRow();
                if (filaSeleccionada != -1) {
                    sprintSeleccionadoId = (Integer) modeloSprints.getValueAt(filaSeleccionada, 0);
                }
            }
        });
    }

    private void configurarPermisos() {
        if (permission.isScrumOrProduct()) {
            view.getBtnCrearProyecto().addActionListener(e -> crearNuevoProyecto());
            view.getBtnCrearSprint().addActionListener(e -> mostrarFormularioCrearSprint());
        } else {
            view.getPanelProyectos().remove(view.getBtnCrearProyecto());
            view.getPanelSprints().remove(view.getBtnCrearSprint());
        }
        
        view.getPanelProyectos().revalidate();
        view.getPanelProyectos().repaint();
        view.getPanelSprints().revalidate();
        view.getPanelSprints().repaint();
    }

    // nuevo: Verificar si el usuario tiene grupos asignados 
    private void verificarGruposYCargarProyectos() {
        try {
            Request req = new Request();
            req.setAction("getGroupsByUser");
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("idUsuario", connector.getUserID());
            req.setPayload(payload);
            
            Response resp = connector.sendRequest(req);
            
            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Group> grupos = (List<Group>) resp.getData();
                
                if (grupos == null || grupos.isEmpty()) {
                    usuarioTieneGrupos = false;
                   mostrarMensajeSinGrupos();
                } else {
                    usuarioTieneGrupos = true;
                    cargarProyectosIniciales();
                }
            } else {
                System.err.println("[ProyectosCardController] Error al verificar grupos: " + resp.getMessage());
                cargarProyectosIniciales(); // Intentar cargar de todas formas
            }
        } catch (Exception e) {
            e.printStackTrace();
            cargarProyectosIniciales();
        }
    }
    
    // === NUEVA INTEGRACIÓN: Mostrar mensaje cuando usuario no tiene grupos ===
    private void mostrarMensajeSinGrupos() {
        modeloProyectos.setRowCount(0);
        modeloSprints.setRowCount(0);
        
        // Deshabilitar botones de creación
        view.getBtnCrearProyecto().setEnabled(false);
        view.getBtnCrearSprint().setEnabled(false);
        
        JOptionPane.showMessageDialog(view,
                "No perteneces a ningún grupo de trabajo.\n\n" +
                "Para ver y crear proyectos, debes ser miembro de al menos un grupo.\n" +
                "Contacta al Scrum Master o administrador para ser asignado.",
                "Sin grupos asignados",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarFormularioCrearSprint() {
        // Verificar grupos antes de crear sprint
        if (!usuarioTieneGrupos) {
            JOptionPane.showMessageDialog(view,
                    "No puedes crear sprints porque no perteneces a ningún grupo.",
                    "Sin permisos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaSeleccionada = view.getTablaProyectos().getSelectedRow();

        if (filaSeleccionada == -1 || proyectoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(view,
                    "Debe seleccionar un proyecto antes de crear un sprint.\n\n"
                    + "Haga clic en un proyecto de la tabla de la izquierda.",
                    "Proyecto no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreProyecto = (String) modeloProyectos.getValueAt(filaSeleccionada, 1);

        CrearSprintView formulario = new CrearSprintView();
        formulario.setTitle("Crear Sprint para: " + nombreProyecto);

        new SprintController(
                formulario,
                connector,
                proyectoSeleccionadoId,
                () -> {
                    System.out.println("Sprint creado, refrescando tabla...");
                    cargarSprintsParaProyecto(proyectoSeleccionadoId);
                }
        );

        formulario.setVisible(true);
    }

    private void cargarSprintsParaProyecto(int projectID) {
        try {
            Request req = new Request();
            req.setAction("getSprintsByProject");

            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", projectID);
            req.setPayload(payload);

            Response resp = connector.sendRequest(req);

            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Sprint> listSprints = (List<Sprint>) resp.getData();
                actualizarTablaSprints(listSprints);
            } else {
                JOptionPane.showMessageDialog(view,
                        "Error al cargar sprints: " + resp.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                modeloSprints.setRowCount(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Error de comunicación con el servidor:\n" + e.getMessage(),
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearNuevoProyecto() {
        //Verificar grupos antes de crear proyecto 
        if (!usuarioTieneGrupos) {
            JOptionPane.showMessageDialog(view,
                    "No puedes crear proyectos porque no perteneces a ningún grupo.\n\n" +
                    "Contacta al administrador para ser asignado a un grupo.",
                    "Sin permisos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        CrearProyectoView formulario = new CrearProyectoView();

        new ProyectoController(
                formulario,
                connector,
                () -> {
                    System.out.println("Proyecto creado, refrescando tabla...");
                    cargarProyectosIniciales();
                }
        );

        formulario.setVisible(true);
    }

    public void cargarProyectosIniciales() {
        try {
            Request req = new Request();
            req.setAction("getProjectsByUser");

            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", connector.getUserID());
            req.setPayload(payload);

            Response resp = connector.sendRequest(req);

            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Project> listProjects = (List<Project>) resp.getData();
                actualizarTablaProyectos(listProjects);

                modeloSprints.setRowCount(0);
                proyectoSeleccionadoId = -1;

                System.out.println("Proyectos cargados: " + (listProjects != null ? listProjects.size() : 0));
                
                // Habilitar botones si hay proyectos 
                if (listProjects != null && !listProjects.isEmpty()) {
                    view.getBtnCrearSprint().setEnabled(permission.isScrumOrProduct());
                }
            } else {
                // Manejar caso sin grupos 
                if (resp.getMessage().contains("no pertenece a ningún grupo")) {
                    usuarioTieneGrupos = false;
                 mostrarMensajeSinGrupos();
                } else {
                    JOptionPane.showMessageDialog(view,
                            "Error al cargar proyectos: " + resp.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Error de comunicación con el servidor:\n" + e.getMessage(),
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTablaProyectos(List<Project> proyectos) {
        modeloProyectos.setRowCount(0);

        if (proyectos == null || proyectos.isEmpty()) {
            System.out.println("No se encontraron proyectos para este usuario");
            return;
        }

        for (Project p : proyectos) {
            Object[] fila = new Object[5];
            fila[0] = p.getIdProyecto();
            fila[1] = p.getNombre();
            fila[2] = p.getDescripcion() != null ? p.getDescripcion() : "";
            fila[3] = p.getGruposPertenencia() != null ? p.getGruposPertenencia() : "";
            fila[4] = p.getFechaCreacion() != null ? p.getFechaCreacion().toString() : "";

            modeloProyectos.addRow(fila);
        }
    }

    private void actualizarTablaSprints(List<Sprint> sprints) {
        modeloSprints.setRowCount(0);

        if (sprints == null || sprints.isEmpty()) {
            System.out.println("No se encontraron sprints para este proyecto");
            return;
        }

        for (Sprint s : sprints) {
            Object[] fila = new Object[5];
            fila[0] = s.getIdSprint();
            fila[1] = s.getNombre() != null ? s.getNombre() : "";
            fila[2] = s.getNombreEstado() != null ? s.getNombreEstado() : "Sin estado";
            fila[3] = s.getFechaInicio() != null ? s.getFechaInicio().toString() : "";
            fila[4] = s.getFechaFin() != null ? s.getFechaFin().toString() : "";

            modeloSprints.addRow(fila);
        }
        
        view.getTablaSprints().clearSelection();
        sprintSeleccionadoId = -1;
    }

    public int getProyectoSeleccionadoId() {
        return proyectoSeleccionadoId;
    }
    
    //Getter para verificar estado de grupos 
    public boolean isUsuarioTieneGrupos() {
        return usuarioTieneGrupos;
    }
}

