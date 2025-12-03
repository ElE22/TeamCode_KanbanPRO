package com.mycompany.teamcode_kanbanpro.view;

import com.mycompany.teamcode_kanbanpro.model.Column;
import com.mycompany.teamcode_kanbanpro.model.Task;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
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
        // Verificar que la tarea no esté ya en esta columna
        if (columnPanel.containsTask(taskPanel)) {
            return false;
        }

        KanbanColumnPanel sourceColumn = taskPanel.getParentColumn();
        Column newColumnData = this.columnPanel.getColumnData(); // Usar el getter
        Task taskData = taskPanel.getTaskData();

        // Remover de la columna origen
        if (sourceColumn != null) {
            sourceColumn.removeTask(taskPanel);
        }

        // Agregar a la columna destino
        columnPanel.addTask(taskPanel);

        // Notificar al view sobre el movimiento
        if (parentView != null) {
           parentView.getController().handleTaskMoved(taskData, newColumnData);
        }

        return true;
    }
}
