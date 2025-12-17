package com.mycompany.teamcode_kanbanpro.view;

import com.mycompany.teamcode_kanbanpro.model.Column;
import com.mycompany.teamcode_kanbanpro.model.Task;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author Emanuel
 */

class ColumnTransferHandler extends TransferHandler {

    private final KanbanColumnPanel columnPanel;
    private final KanbanBoardView parentView;

    public ColumnTransferHandler(KanbanColumnPanel columnPanel, KanbanBoardView parentView) {
        this.columnPanel = columnPanel;
        this.parentView = parentView;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
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
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        try {
            Transferable transferable = support.getTransferable();
            KanbanTaskPanel taskPanel = (KanbanTaskPanel) transferable.getTransferData(TaskTransferHandler.getTaskFlavor());
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
        KanbanColumnPanel sourceColumn = (KanbanColumnPanel) SwingUtilities.getAncestorOfClass(
                KanbanColumnPanel.class, taskPanel
        );

        // Verificar que la tarea no esté ya en esta columna destino
        if (columnPanel.containsTask(taskPanel)) {
            return false;
        }

        Column newColumnData = this.columnPanel.getColumnData();
        Task taskData = taskPanel.getTaskData();

        if (sourceColumn != null) {
            sourceColumn.removeTask(taskPanel);
            sourceColumn.revalidate();
            sourceColumn.repaint();
        }

        columnPanel.addTask(taskPanel);
        
        if (parentView != null) {
            // Esto obliga a Swing a recalcular todas las posiciones y limpiar restos
            parentView.getContentPane().revalidate();
            parentView.getContentPane().repaint();

            // Notificar al controller (tu lógica de reflexión actual)
            Object controller = parentView.getController();
            if (controller != null) {
                try {
                    Method m = controller.getClass().getMethod("handleTaskMoved", Task.class, Column.class);
                    m.invoke(controller, taskData, newColumnData);
                } catch (NoSuchMethodException e) {
                    // Ignorar
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
