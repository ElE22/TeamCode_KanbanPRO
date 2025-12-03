/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.teamcode_kanbanpro.view;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 *
 * @author Emanuel
 */

class TaskTransferHandler extends TransferHandler {

    // DataFlavor personalizado para las tareas
    private static DataFlavor TASK_FLAVOR = null;
    
    static {
        try {
            TASK_FLAVOR = new DataFlavor(
                DataFlavor.javaJVMLocalObjectMimeType +
                ";class=" + KanbanTaskPanel.class.getName()
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DataFlavor getTaskFlavor() {
        return TASK_FLAVOR;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (!(c instanceof KanbanTaskPanel)) {
            return null;
        }

        KanbanTaskPanel taskPanel = (KanbanTaskPanel) c;
        
        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{TASK_FLAVOR};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return TASK_FLAVOR.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) 
                    throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) {
                    return taskPanel;
                }
                throw new UnsupportedFlavorException(flavor);
            }
        };
    }

    // Metodo publico para acceder a createTransferable desde otras clases
    public Transferable createTransferablePublic(JComponent c) {
        return createTransferable(c);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        //  se llama despues de completar el drag
        // Aqui podrias agregar logica adicional si es necesario
    }
}

// TransferHandler para las columnas, maneja la recepcion de tareas cuando se sueltan en una columna
class ColumnTransferHandler extends TransferHandler {

    private final KanbanColumnPanel columnPanel;
    private final KanbanBoardView parentView;

    public ColumnTransferHandler(KanbanColumnPanel columnPanel, KanbanBoardView parentView) {
        this.columnPanel = columnPanel;
        this.parentView = parentView;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        // Verificar si el tipo de datos es compatible
        return support.isDataFlavorSupported(TaskTransferHandler.getTaskFlavor());
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (DataFlavor flavor : transferFlavors) {
            if (TaskTransferHandler.getTaskFlavor().equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        try {
            Transferable transferable = support.getTransferable();
            KanbanTaskPanel taskPanel = (KanbanTaskPanel) transferable.getTransferData(
                TaskTransferHandler.getTaskFlavor()
            );

            return moveTask(taskPanel);

        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        if (!canImport(comp, t.getTransferDataFlavors())) {
            return false;
        }

        try {
            KanbanTaskPanel taskPanel = (KanbanTaskPanel) t.getTransferData(
                TaskTransferHandler.getTaskFlavor()
            );

            return moveTask(taskPanel);

        } catch (UnsupportedFlavorException | IOException | ClassCastException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Mueve la tarea de una columna a otra
    private boolean moveTask(KanbanTaskPanel taskPanel) {
        // Verificar que la tarea no esté ya en esta columna
        if (columnPanel.containsTask(taskPanel)) {
            return false;
        }

        // Obtener la columna de origen
        KanbanColumnPanel sourceColumn = taskPanel.getParentColumn();

        // Remover de la columna origen
        if (sourceColumn != null) {
            sourceColumn.removeTask(taskPanel);
        }

        // Agregar a la columna destino
        columnPanel.addTask(taskPanel);

        // Notificar al view sobre el movimiento
        if (parentView != null) {
            parentView.onTaskMoved(taskPanel.getTitle(), columnPanel.getColumnName());
        }

        return true;
    }
}