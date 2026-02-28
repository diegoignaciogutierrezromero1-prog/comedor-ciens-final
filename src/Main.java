import Controllers.LoginController;
import View.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configurar look and feel nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Iniciar la aplicación con la vista de login
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController(loginView);
            loginView.setVisible(true);
        });
    }
}
  