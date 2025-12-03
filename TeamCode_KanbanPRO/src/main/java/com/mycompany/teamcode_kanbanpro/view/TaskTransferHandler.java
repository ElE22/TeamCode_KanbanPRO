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
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
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

