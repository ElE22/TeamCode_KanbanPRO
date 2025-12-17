package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.model.Task;
import com.mycompany.teamcode_kanbanpro.model.Comment;
import com.mycompany.teamcode_kanbanpro.model.User;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MockDataProvider {

    public static Task getFullMockTask() {
        // 1. Crear Usuarios base para las pruebas
        User u1 = new User(); u1.setIdUsuario(1); u1.setNombre("Emanuel Escobedo");
        User u2 = new User(); u2.setIdUsuario(2); u2.setNombre("Ana García");
        User u3 = new User(); u3.setIdUsuario(3); u3.setNombre("Roberto Gómez");

        // 2. Crear Tarea Principal
        Task mainTask = new Task();
        mainTask.setIdTarea(100);
        mainTask.setIdProyecto(1);
        mainTask.setTitulo("Implementar Módulo de Reportes");
        mainTask.setDescripcion("Desarrollar la lógica y vista para exportar reportes en PDF y Excel.");
        mainTask.setIdPrioridad(3); // Alta
        mainTask.setNombrePrioridad("Alta");
        mainTask.setFechaVencimiento(new Date(System.currentTimeMillis() + 86400000 * 5)); // 5 días después
        
        // Asignar usuarios a la principal
        List<User> asignadosPrincipal = new ArrayList<>();
        asignadosPrincipal.add(u1);
        asignadosPrincipal.add(u2);
        mainTask.setUsuariosAsignados(asignadosPrincipal);

        // 3. Crear Subtareas con sus propios usuarios asignados
        List<Task> subtareas = new ArrayList<>();
        
        // Subtarea 1
        Task st1 = new Task();
        st1.setIdTarea(101);
        st1.setIdTareaPadre(100);
        st1.setTitulo("Diseñar plantilla PDF");
        st1.setNombreColumna("DONE"); 
        List<User> usersSt1 = new ArrayList<>();
        usersSt1.add(u2); // Solo Ana
        st1.setUsuariosAsignados(usersSt1);
        
        // Subtarea 2
        Task st2 = new Task();
        st2.setIdTarea(102);
        st2.setIdTareaPadre(100);
        st2.setTitulo("Configurar librería Apache POI");
        st2.setNombreColumna("IN PROGRESS");
        List<User> usersSt2 = new ArrayList<>();
        usersSt2.add(u1); // Emanuel
        usersSt2.add(u3); // Roberto
        st2.setUsuariosAsignados(usersSt2);
        
        subtareas.add(st1);
        subtareas.add(st2);
        mainTask.setSubtareas(subtareas);

        return mainTask;
    }

    public static List<Comment> getMockComments(int idTarea) {
        List<Comment> mockList = new ArrayList<>();
        
        Comment c1 = new Comment();
        c1.setIdComentario(1);
        c1.setNombreUsuario("Emanuel Escobedo");
        c1.setContenido("He comenzado con el análisis de los requisitos. Todo parece en orden.");
        c1.setFecha(new Timestamp(System.currentTimeMillis() - 3600000 * 2)); // Hace 2 horas
        
        Comment c2 = new Comment();
        c2.setIdComentario(2);
        c2.setNombreUsuario("Ana García");
        c2.setContenido("¿Alguien tiene el acceso al servidor de base de datos? No puedo conectar.");
        c2.setFecha(new Timestamp(System.currentTimeMillis() - 3600000)); // Hace 1 hora
        
        Comment c3 = new Comment();
        c3.setIdComentario(3);
        c3.setNombreUsuario("Sistema");
        c3.setContenido("La fecha de vencimiento ha sido actualizada por el administrador.");
        c3.setFecha(new Timestamp(System.currentTimeMillis()));

        mockList.add(c1);
        mockList.add(c2);
        mockList.add(c3);
        
        return mockList;
    }
}