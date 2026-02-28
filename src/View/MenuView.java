package View;

import Model.Menu;
import Model.MenuService;
import Model.Food;
import Model.FoodService;
import Utils.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class MenuView extends JFrame {
    private MenuService menuService;
    private FoodService foodService;
    private JPanel menuPanel;
    private JScrollPane scrollPane;
    private JButton refreshButton;
    private JButton backButton;

    public MenuView() {
        this.menuService = new MenuService();
        this.foodService = new FoodService();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadTodayMenu();
    }

    private void initializeComponents() {
        setTitle("Menú del Día");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        
        scrollPane = new JScrollPane(menuPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        refreshButton = new JButton("Actualizar");
        backButton = new JButton("Volver al Dashboard");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Panel superior con título
        JPanel headerPanel = new JPanel(new FlowLayout());
        JLabel titleLabel = new JLabel("Menú del Día");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);

        // Panel inferior con botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadTodayMenu());
        backButton.addActionListener(e -> {
            dispose();
            // Volver al dashboard
            View.DashboardView dashboard = new View.DashboardView();
            dashboard.setVisible(true);
        });
    }

    private void loadTodayMenu() {
        menuPanel.removeAll();
        menuPanel.revalidate();
        menuPanel.repaint();

        // Obtener el menú de hoy
        Optional<Menu> todayMenuOpt = menuService.getTodayMenu();
        
        if (todayMenuOpt.isPresent()) {
            Menu todayMenu = todayMenuOpt.get();
            displayMenu(todayMenu);
        } else {
            displayNoMenuMessage();
        }

        // También mostrar menús próximos
        displayUpcomingMenus();
    }

    private void displayMenu(Menu menu) {
        JPanel menuCard = createMenuCard(menu);
        menuPanel.add(menuCard);
        menuPanel.add(Box.createVerticalStrut(20));
    }

    private JPanel createMenuCard(Menu menu) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.GRAY, 1)
        ));
        card.setMaximumSize(new Dimension(750, 300));

        // Panel de información principal
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        infoPanel.add(new JLabel("Nombre:"));
        infoPanel.add(new JLabel(menu.getName()));
        
        infoPanel.add(new JLabel("Descripción:"));
        infoPanel.add(new JLabel(menu.getDescription()));
        
        infoPanel.add(new JLabel("Precio:"));
        JLabel priceLabel = new JLabel("Bs. " + menu.getTotalPrice());
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 128, 0));
        infoPanel.add(priceLabel);
        
        infoPanel.add(new JLabel("Disponibilidad:"));
        JLabel availabilityLabel = new JLabel(
            menu.getRemainingServings() + " porciones disponibles"
        );
        availabilityLabel.setForeground(menu.canServe() ? new Color(0, 128, 0) : Color.RED);
        infoPanel.add(availabilityLabel);

        // Panel de alimentos
        JPanel foodPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        foodPanel.setBorder(BorderFactory.createTitledBorder("Componentes del Menú"));

        addFoodToPanel(foodPanel, "Plato Principal", menu.getMainDishId());
        addFoodToPanel(foodPanel, "Acompañamiento", menu.getSideDishId());
        addFoodToPanel(foodPanel, "Bebida", menu.getBeverageId());
        addFoodToPanel(foodPanel, "Postre", menu.getDessertId());

        // Panel de estado
        JPanel statusPanel = new JPanel(new FlowLayout());
        if (menu.isAvailable() && menu.canServe()) {
            JLabel statusLabel = new JLabel("✓ Disponible para pedido");
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
            statusPanel.add(statusLabel);
        } else {
            JLabel statusLabel = new JLabel("✗ No disponible");
            statusLabel.setForeground(Color.RED);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
            statusPanel.add(statusLabel);
        }

        card.add(infoPanel, BorderLayout.NORTH);
        card.add(foodPanel, BorderLayout.CENTER);
        card.add(statusPanel, BorderLayout.SOUTH);

        return card;
    }

    private void addFoodToPanel(JPanel panel, String label, String foodId) {
        if (foodId != null) {
            Optional<Food> foodOpt = foodService.getFoodById(foodId);
            if (foodOpt.isPresent()) {
                Food food = foodOpt.get();
                JPanel foodInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
                foodInfo.add(new JLabel(label + ":"));
                JLabel foodLabel = new JLabel(food.getName());
                foodLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                foodInfo.add(foodLabel);
                
                if (food.getPrice() > 0) {
                    JLabel priceLabel = new JLabel("(Bs. " + food.getPrice() + ")");
                    priceLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                    priceLabel.setForeground(Color.GRAY);
                    foodInfo.add(priceLabel);
                }
                
                panel.add(foodInfo);
            }
        }
    }

    private void displayNoMenuMessage() {
        JPanel messagePanel = new JPanel(new FlowLayout());
        JLabel messageLabel = new JLabel("No hay menú disponible para hoy");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        messageLabel.setForeground(Color.GRAY);
        messagePanel.add(messageLabel);
        menuPanel.add(messagePanel);
        menuPanel.add(Box.createVerticalStrut(20));
    }

    private void displayUpcomingMenus() {
        List<Menu> allMenus = menuService.getAllMenus();
        String today = java.time.LocalDate.now().toString();
        
        // Filtrar menús futuros
        List<Menu> upcomingMenus = allMenus.stream()
            .filter(menu -> menu.getDate().compareTo(today) > 0)
            .sorted((m1, m2) -> m1.getDate().compareTo(m2.getDate()))
            .limit(3)
            .toList();

        if (!upcomingMenus.isEmpty()) {
            JPanel upcomingPanel = new JPanel(new BorderLayout());
            upcomingPanel.setBorder(BorderFactory.createTitledBorder("Próximos Menús"));
            
            JPanel upcomingList = new JPanel();
            upcomingList.setLayout(new BoxLayout(upcomingList, BoxLayout.Y_AXIS));

            for (Menu menu : upcomingMenus) {
                JPanel menuItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
                menuItem.add(new JLabel("• " + menu.getDate() + ": " + menu.getName()));
                menuItem.add(new JLabel(" (Bs. " + menu.getTotalPrice() + ")"));
                upcomingList.add(menuItem);
            }

            upcomingPanel.add(upcomingList, BorderLayout.CENTER);
            menuPanel.add(upcomingPanel);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuView().setVisible(true);
        });
    }
}
