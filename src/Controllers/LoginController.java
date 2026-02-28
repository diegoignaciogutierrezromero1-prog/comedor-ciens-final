package Controllers;

import Model.UserService;
import Utils.UserSession;
import View.DashboardView;
import View.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController implements ActionListener {
    private LoginView loginView;
    private UserService userService;

    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        this.userService = new UserService();
        
        loginView.addLoginListener(this);
        loginView.addEnterKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        handleLogin();
    }

    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            loginView.setMessage("Por favor ingrese usuario y contraseña");
            return;
        }

        if (userService.authenticateUser(username, password)) {
            UserSession.getInstance().setCurrentUser(
                userService.getUserByUsername(username).orElse(null)
            );
            
            loginView.dispose();
            
            DashboardView dashboard = new DashboardView();
            dashboard.setVisible(true);
        } else {
            loginView.setMessage("Usuario o contraseña incorrectos");
            loginView.clearFields();
        }
    }
}
