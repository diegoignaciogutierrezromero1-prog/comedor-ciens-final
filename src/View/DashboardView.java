package View;

import Utils.UserSession;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardView extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel;
    private JButton walletButton;
    private JButton menuButton;
    private JButton logoutButton;
    private JPanel centerPanel;
    private JPanel studentPanel;
    private JPanel adminPanel;
    private JPanel staffPanel;

    public DashboardView() {
        currentUser = UserSession.getInstance().getCurrentUser();
        
        setTitle("Comedor UCV - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        setupRoleBasedUI();
    }

    private void initComponents() {
        welcomeLabel = new JLabel("¡Bienvenido, " + currentUser.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        walletButton = new JButton("Mi Monedero");
        menuButton = new JButton("Ver Menú");
        logoutButton = new JButton("Cerrar Sesión");
        
        logoutButton.addActionListener(e -> {
            UserSession.getInstance().logout();
            dispose();
            View.LoginView loginView = new View.LoginView();
            new Controllers.LoginController(loginView);
            loginView.setVisible(true);
        });
        
        // Crear paneles para cada rol
        studentPanel = createStudentPanel();
        adminPanel = createAdminPanel();
        staffPanel = createStaffPanel();
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(welcomeLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel - Role specific content
        centerPanel = new JPanel(new CardLayout());
        centerPanel.add(studentPanel, "student");
        centerPanel.add(adminPanel, "admin");
        centerPanel.add(staffPanel, "staff");
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(logoutButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        walletButton.addActionListener(e -> showWalletInfo());
        menuButton.addActionListener(e -> showMenuInfo());
        
        panel.add(walletButton);
        panel.add(menuButton);
        
        JButton historyButton = new JButton("Historial de Compras");
        historyButton.addActionListener(e -> showPurchaseHistory());
        panel.add(historyButton);
        
        JButton profileButton = new JButton("Mi Perfil");
        profileButton.addActionListener(e -> showProfile());
        panel.add(profileButton);
        
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton usersButton = new JButton("Gestionar Usuarios");
        usersButton.addActionListener(e -> manageUsers());
        panel.add(usersButton);
        
        JButton menuManageButton = new JButton("Gestionar Menús");
        menuManageButton.addActionListener(e -> manageMenus());
        panel.add(menuManageButton);
        
        JButton foodButton = new JButton("Gestionar Alimentos");
        foodButton.addActionListener(e -> manageFoods());
        panel.add(foodButton);
        
        JButton ingredientButton = new JButton("Gestionar Ingredientes");
        ingredientButton.addActionListener(e -> manageIngredients());
        panel.add(ingredientButton);
        
        JButton reportsButton = new JButton("Reportes");
        reportsButton.addActionListener(e -> showReports());
        panel.add(reportsButton);
        
        JButton settingsButton = new JButton("Configuración");
        settingsButton.addActionListener(e -> showSettings());
        panel.add(settingsButton);
        
        return panel;
    }

    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton serveButton = new JButton("Servir Comida");
        serveButton.addActionListener(e -> serveFood());
        panel.add(serveButton);
        
        JButton todayMenuButton = new JButton("Menú del Día");
        todayMenuButton.addActionListener(e -> showTodayMenu());
        panel.add(todayMenuButton);
        
        JButton inventoryButton = new JButton("Ver Inventario");
        inventoryButton.addActionListener(e -> viewInventory());
        panel.add(inventoryButton);
        
        JButton salesButton = new JButton("Ventas del Día");
        salesButton.addActionListener(e -> showDailySales());
        panel.add(salesButton);
        
        return panel;
    }

    private void setupRoleBasedUI() {
        CardLayout cardLayout = (CardLayout) centerPanel.getLayout();
        
        if (UserSession.getInstance().isAdmin()) {
            cardLayout.show(centerPanel, "admin");
        } else if (UserSession.getInstance().isStudent()) {
            cardLayout.show(centerPanel, "student");
        } else if (UserSession.getInstance().isStaff()) {
            cardLayout.show(centerPanel, "staff");
        }
    }

    // Placeholder methods for functionality
    private void showWalletInfo() {
        WalletView walletView = new WalletView(this);
        walletView.setVisible(true);
    }

    private void showMenuInfo() {
        MenuView menuView = new MenuView();
        menuView.setVisible(true);
    }

    private void showPurchaseHistory() {
        JOptionPane.showMessageDialog(this, "Historial de compras en desarrollo", 
            "Historial", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showProfile() {
        JOptionPane.showMessageDialog(this, "Perfil de usuario en desarrollo", 
            "Perfil", JOptionPane.INFORMATION_MESSAGE);
    }

    private void manageUsers() {
        UserManagementView userManagementView = new UserManagementView(this);
        userManagementView.setVisible(true);
    }

    private void manageMenus() {
        MenuManagementView menuManagementView = new MenuManagementView();
        menuManagementView.setVisible(true);
    }

    private void manageFoods() {
        FoodManagementView foodManagementView = new FoodManagementView();
        foodManagementView.setVisible(true);
    }

    private void manageIngredients() {
        IngredientManagementView ingredientManagementView = new IngredientManagementView();
        ingredientManagementView.setVisible(true);
    }

    private void showReports() {
        JOptionPane.showMessageDialog(this, "Reportes en desarrollo", 
            "Reportes", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(this, "Configuración en desarrollo", 
            "Configuración", JOptionPane.INFORMATION_MESSAGE);
    }

    private void serveFood() {
        JOptionPane.showMessageDialog(this, "Servir comida en desarrollo", 
            "Servir", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTodayMenu() {
        MenuView menuView = new MenuView();
        menuView.setVisible(true);
    }

    private void viewInventory() {
        JOptionPane.showMessageDialog(this, "Inventario en desarrollo", 
            "Inventario", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDailySales() {
        JOptionPane.showMessageDialog(this, "Ventas del día en desarrollo", 
            "Ventas", JOptionPane.INFORMATION_MESSAGE);
    }
}
