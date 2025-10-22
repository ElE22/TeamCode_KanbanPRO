
package com.mycompany.teamcode_kanbanpro;

import com.mycompany.teamcode_kanbanpro.view.LoginScreen;
import javax.swing.SwingUtilities;

/**
 *
 * @author diana
 */
public class TeamCode_KanbanPRO {

   public static void main(String[] args) {
        // Asegura que la UI se ejecute en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
