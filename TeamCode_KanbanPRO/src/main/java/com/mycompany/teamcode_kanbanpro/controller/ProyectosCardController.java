package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.client.ClientConnector;
import com.mycompany.teamcode_kanbanpro.client.Request;
import com.mycompany.teamcode_kanbanpro.client.Response;
import com.mycompany.teamcode_kanbanpro.model.Project;
import com.mycompany.teamcode_kanbanpro.model.Sprint;
import com.mycompany.teamcode_kanbanpro.view.ProyectosView;
import com.mycompany.teamcode_kanbanpro.view.CrearSprintView;
import com.mycompany.teamcode_kanbanpro.controller.KanbanBoardController;

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

    // Variable para almacenar el ID del proyecto actualmente seleccionado
    private int proyectoSeleccionadoId = -1;
    private int sprintSeleccionadoId = -1;

    public ProyectosCardController(ProyectosView view, ClientConnector connector) {
        this.view = view;
        this.connector = connector;
        initialize();
        cargarProyectosIniciales();
    }

    //Inicializa todos los listeners y eventos
    private void initialize() {
        // Obtener referencias a los modelos de las tablas
        modeloProyectos = view.getModeloProyectos();
        modeloSprints = view.getModeloSprints();

        view.getBtnCrearProyecto().addActionListener(e -> crearNuevoProyecto());

        view.getBtnCrearSprint().addActionListener(e -> mostrarFormularioCrearSprint());

        view.getTablaProyectos().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable tabla = (JTable) e.getSource();
                int filaSeleccionada = tabla.getSelectedRow();

                if (filaSeleccionada != -1) {
                    // Obtener el ID del proyecto seleccionado
                    proyectoSeleccionadoId = (Integer) modeloProyectos.getValueAt(filaSeleccionada, 0);

                    // Cargar los sprints de ese proyecto
                    cargarSprintsParaProyecto(proyectoSeleccionadoId);

                    System.out.println("Proyecto seleccionado ID: " + proyectoSeleccionadoId);
                }
            }
        });
        view.getTablaSprints().addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JTable tabla = (JTable) e.getSource();
            int filaSeleccionada = tabla.getSelectedRow();

            if (filaSeleccionada != -1) {
                // Usar el modeloSprints para obtener el ID
                sprintSeleccionadoId = (Integer) modeloSprints.getValueAt(filaSeleccionada, 0); 
                System.out.println("Sprint seleccionado ID: " + sprintSeleccionadoId);
                new KanbanBoardController(connector,sprintSeleccionadoId, proyectoSeleccionadoId  );
            }
        }
    });
       view.getTablaSprints().getSelectionModel().addListSelectionListener(e -> {
        // Asegurarse de que el evento no está siendo ajustado (previene disparos múltiples)
        if (!e.getValueIsAdjusting()) { 
            int filaSeleccionada = view.getTablaSprints().getSelectedRow();
            if (filaSeleccionada != -1) {
                // Usar modeloSprints y solo actualizar la variable de ID
                sprintSeleccionadoId = (Integer) modeloSprints.getValueAt(filaSeleccionada, 0);
                System.out.println("Sprint seleccionado ID (por teclado/modelo): " + sprintSeleccionadoId);
                // ELIMINADO: Ya NO se llama a cargarSprintsParaProyecto(proyectoSeleccionadoId);
            }
        }
    });
    }

    //Muestra el formulario para crear un nuevo sprint
    private void mostrarFormularioCrearSprint() {
        //Verificar que hay un proyecto seleccionado
        int filaSeleccionada = view.getTablaProyectos().getSelectedRow();

        if (filaSeleccionada == -1 || proyectoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(view,
                    "Debe seleccionar un proyecto antes de crear un sprint.\n\n"
                    + "Haga clic en un proyecto de la tabla de la izquierda.",
                    "Proyecto no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Obtener el nombre del proyecto para mostrar en el formulario (opcional)
        String nombreProyecto = (String) modeloProyectos.getValueAt(filaSeleccionada, 1);

        //Crear la vista del formulario
        CrearSprintView formulario = new CrearSprintView();
        formulario.setTitle("Crear Sprint para: " + nombreProyecto);

        new SprintController(
                formulario,
                connector,
                proyectoSeleccionadoId,
                () -> {
                    // Este código se ejecuta después de crear el sprint
                    System.out.println("Sprint creado, refrescando tabla...");
                    cargarSprintsParaProyecto(proyectoSeleccionadoId);
                }
        );

        formulario.setVisible(true);
    }

    //Carga los sprints de un proyecto específico desde el servidor
    private void cargarSprintsParaProyecto(int projectID) {
        try {

            Request req = new Request();
            req.setAction("getSprintsByProject");

            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", projectID);
            req.setPayload(payload);

            // Enviar al servidor
            Response resp = connector.sendRequest(req);

            if (resp.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Sprint> listSprints = (List<Sprint>) resp.getData();
                actualizarTablaSprints(listSprints);

                System.out.println("Sprints cargados: " + (listSprints != null ? listSprints.size() : 0));
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

    //Muestra el formulario para crear un nuevo proyecto
    private void crearNuevoProyecto() {

        String userRole = connector.getUserRole();
        if (userRole == null || !userRole.equalsIgnoreCase("Scrum Master")) {
            JOptionPane.showMessageDialog(view,
                    "Solo los usuarios con rol de Scrum Master pueden crear proyectos.",
                    "Permiso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear la vista del formulario
        com.mycompany.teamcode_kanbanpro.view.CrearProyectoView formulario
                = new com.mycompany.teamcode_kanbanpro.view.CrearProyectoView();

        // Crear el controlador con callback para refrescar
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

    //Carga los proyectos del usuario actual desde el servidor 
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

                // Limpiar la tabla de sprints al cargar proyectos
                modeloSprints.setRowCount(0);
                proyectoSeleccionadoId = -1;

                System.out.println("Proyectos cargados: " + (listProjects != null ? listProjects.size() : 0));
            } else {
                JOptionPane.showMessageDialog(view,
                        "Error al cargar proyectos: " + resp.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Error de comunicación con el servidor:\n" + e.getMessage(),
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Actualiza la tabla de proyectos con los datos recibidos
    private void actualizarTablaProyectos(List<Project> proyectos) {
        // Limpiar la tabla
        modeloProyectos.setRowCount(0);

        if (proyectos == null || proyectos.isEmpty()) {
            // Mostrar mensaje si no hay proyectos
            System.out.println("No se encontraron proyectos para este usuario");
            return;
        }

        // Agregar cada proyecto a la tabla
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

        // Agregar cada sprint a la tabla
        // CORREGIDO: Ahora coincide con las 5 columnas de la vista
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
}
