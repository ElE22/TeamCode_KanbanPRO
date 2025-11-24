
package com.mycompany.teamcode_kanbanpro;

import com.mycompany.teamcode_kanbanpro.controller.AuthController;
import com.mycompany.teamcode_kanbanpro.view.LoginScreen;
import com.mycompany.teamcode_kanbanpro.view.RegisterUserView;
import javax.swing.SwingUtilities;

/**
 *
 * @author diana
 */
public class TeamCode_KanbanPRO {

   public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginScreen LS = new LoginScreen();
            RegisterUserView RU = new RegisterUserView();
            new AuthController(LS, RU);
            LS.setVisible(true);
        });
    }
}
