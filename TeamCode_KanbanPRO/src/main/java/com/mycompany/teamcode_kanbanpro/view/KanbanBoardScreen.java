
package com.mycompany.teamcode_kanbanpro.view;

/**
 *
 * @author diana
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class KanbanBoardScreen extends JFrame {

    public KanbanBoardScreen() {
        setTitle("Pizarra Kanban - Proyecto Actual");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizado por defecto

        //Barra de Herramientas Superior
        JToolBar topBar = new JToolBar();
        topBar.setFloatable(false);
        // Selector de Proyecto
        JComboBox<String> projectSelector = new JComboBox<>(new String[]{"Proyecto X (Seleccionado)", "Proyecto Y", "Proyecto Z"});
        topBar.add(new JLabel("Proyecto: "));
        topBar.add(projectSelector);
        topBar.add(Box.createHorizontalGlue()); // Empuja el resto del cont. a la derecha
        topBar.add(new JButton("Crear Tarea"));

        // Contenedor Principal de la Pizarra (Horizontal)
        // Usamos BoxLayout para que las columnas se dispongan de izquierda a derecha.
        // Maes tengan cuidado
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.X_AXIS));
        boardPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Margen alrededor de la pizarra

        //Scroll Pane (Permite el desplazamiento horizontal)
        JScrollPane scrollPane = new JScrollPane(boardPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Solo las tareas dentro de la columna se desplazan

        // Ejemplo de Columnas Kanban
        // En un prototipo real, estos se cargarían dinámicamente.
        addColumnToBoard(boardPanel, "Backlog", new Color(255, 193, 7)); // Amarillo
        addColumnToBoard(boardPanel, "In Progress", new Color(0, 150, 136)); // verda azulado maso menos
        addColumnToBoard(boardPanel, "Review", new Color(33, 150, 243)); // Azul
        addColumnToBoard(boardPanel, "Done", new Color(76, 175, 80)); // Verde

        // Organizar la ventana
        add(topBar, BorderLayout.NORTH);//arriba
        add(scrollPane, BorderLayout.CENTER);//ponemos en el centro, para el resto de la ventana
    }

    // Metodo para crear una Columna Kanban
    private void addColumnToBoard(JPanel boardPanel, String title, Color headerColor) {
        JPanel column = new JPanel();
        column.setLayout(new BorderLayout());
        column.setPreferredSize(new Dimension(300, 0)); // Ancho fijo para las columnas
        column.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));// si no les gusta el color del borde aqui lo podriamos modificar, pero no sabria si podemos ponerla mas ancho la linea

        // Titulo de la columna
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(headerColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
        column.add(titleLabel, BorderLayout.NORTH);

        // Contenedor de tareas para la Columna
        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tasksPanel.setBackground(new Color(245, 245, 245)); // Gris claro para el fondo de la columna

        // Ejemplo de tareas
        // Aqui se agregarian las tarjetas Kanban.
        if (title.equals("Backlog")) {
            addTaskCard(tasksPanel, "Implementar Login", "ALTA", "25/10/2025");
            addTaskCard(tasksPanel, "Diseño de la Pizarra", "MEDIA", "23/10/2025");
        } else if (title.equals("In Progress")) {
             addTaskCard(tasksPanel, "Revisar Diagrama E-R", "BAJA", "30/10/2025");
        }


        // Scroll vertical para las tareas dentro de la columna
        JScrollPane taskScrollPane = new JScrollPane(tasksPanel);
        taskScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        taskScrollPane.setBorder(null);

        column.add(taskScrollPane, BorderLayout.CENTER);

        // Agrega la columna y un espaciador al panel principal
        boardPanel.add(column);
        boardPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Espacio entre columnas
    }

    // Metodo para crear una tarjeta de tarea
    private void addTaskCard(JPanel container, String title, String priority, String dueDate) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getPriorityColor(priority), 3), // Borde de prioridad
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel infoLabel = new JLabel("P: " + priority + " | Venc: " + dueDate);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(infoLabel, BorderLayout.SOUTH);

        // Agregar la tarjeta al contenedor de tareas y añadir un espaciador
        container.add(card);
        container.add(Box.createRigidArea(new Dimension(0, 8))); // Espacio entre tarjetas
    }

    // Metodo de utilidad para el color de prioridad
    private Color getPriorityColor(String priority) {
        return switch (priority) {
            case "ALTA" -> new Color(255, 64, 129); // Rojo/Rosa (Prioridad alta)
            case "MEDIA" -> new Color(255, 179, 0); // Naranja (Prioridad media)
            case "BAJA" -> new Color(30, 136, 229); // Azul (Prioridad baja)
            default -> Color.LIGHT_GRAY;
        };
    }
}
