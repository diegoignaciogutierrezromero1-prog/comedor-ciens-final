package View;

import Model.Menu;
import Model.MenuService;
import Model.Food;
import Model.FoodService;
import Utils.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class MenuManagementView extends JFrame {
    private MenuService menuService;
    private FoodService foodService;
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField dateField;
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField priceField;
    private JTextField servingsField;
    private JComboBox<String> mainDishCombo;
    private JComboBox<String> sideDishCombo;
    private JComboBox<String> beverageCombo;
    private JComboBox<String> dessertCombo;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public MenuManagementView() {
        this.menuService = new MenuService();
        this.foodService = new FoodService();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadMenus();
        loadFoods();
    }

    private void initializeComponents() {
        setTitle("Gestión de Menús");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Tabla de menús
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Fecha", "Nombre", "Descripción", "Precio", "Servicios", "Disponible"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(tableModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.getTableHeader().setReorderingAllowed(false);

        // Campos del formulario
        dateField = new JTextField(15);
        nameField = new JTextField(20);
        descriptionField = new JTextField(30);
        priceField = new JTextField(10);
        servingsField = new JTextField(10);

        // Combobox para alimentos
        mainDishCombo = new JComboBox<>();
        sideDishCombo = new JComboBox<>();
        beverageCombo = new JComboBox<>();
        dessertCombo = new JComboBox<>();

        // Botones
        addButton = new JButton("Añadir Menú");
        editButton = new JButton("Editar Menú");
        deleteButton = new JButton("Eliminar Menú");
        refreshButton = new JButton("Actualizar");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Panel superior con formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Menú"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1: Fecha y Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 2;
        formPanel.add(nameField, gbc);

        // Fila 2: Descripción
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 4;
        formPanel.add(descriptionField, gbc);

        // Fila 3: Alimentos
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Plato Principal:"), gbc);
        gbc.gridx = 1;
        formPanel.add(mainDishCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Acompañamiento:"), gbc);
        gbc.gridx = 3;
        formPanel.add(sideDishCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Bebida:"), gbc);
        gbc.gridx = 1;
        formPanel.add(beverageCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Postre:"), gbc);
        gbc.gridx = 3;
        formPanel.add(dessertCombo, gbc);

        // Fila 4: Precio y Servicios
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Max Servicios:"), gbc);
        gbc.gridx = 3;
        formPanel.add(servingsField, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Panel central con tabla
        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Menús"));

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> addMenu());
        editButton.addActionListener(e -> editMenu());
        deleteButton.addActionListener(e -> deleteMenu());
        refreshButton.addActionListener(e -> {
            loadMenus();
            loadFoods();
        });

        // Selección en tabla
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedMenu();
            }
        });
    }

    private void loadMenus() {
        tableModel.setRowCount(0);
        List<Menu> menus = menuService.getAllMenus();
        
        for (Menu menu : menus) {
            Object[] row = {
                menu.getId(),
                menu.getDate(),
                menu.getName(),
                menu.getDescription(),
                "Bs. " + menu.getTotalPrice(),
                menu.getCurrentServings() + "/" + menu.getMaxServings(),
                menu.isAvailable() ? "Sí" : "No"
            };
            tableModel.addRow(row);
        }
    }

    private void loadFoods() {
        mainDishCombo.removeAllItems();
        sideDishCombo.removeAllItems();
        beverageCombo.removeAllItems();
        dessertCombo.removeAllItems();

        mainDishCombo.addItem("");
        sideDishCombo.addItem("");
        beverageCombo.addItem("");
        dessertCombo.addItem("");

        List<Food> foods = foodService.getAllFoods();
        for (Food food : foods) {
            switch (food.getCategory().toLowerCase()) {
                case "plato_principal":
                case "principal":
                    mainDishCombo.addItem(food.getId() + " - " + food.getName());
                    break;
                case "guarnicion":
                case "acompañamiento":
                case "secundario":
                    sideDishCombo.addItem(food.getId() + " - " + food.getName());
                    break;
                case "bebida":
                    beverageCombo.addItem(food.getId() + " - " + food.getName());
                    break;
                case "postre":
                    dessertCombo.addItem(food.getId() + " - " + food.getName());
                    break;
            }
        }
    }

    private void loadSelectedMenu() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow >= 0) {
            String menuId = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<Menu> menuOpt = menuService.getMenuById(menuId);
            
            if (menuOpt.isPresent()) {
                Menu menu = menuOpt.get();
                dateField.setText(menu.getDate());
                nameField.setText(menu.getName());
                descriptionField.setText(menu.getDescription());
                priceField.setText(String.valueOf(menu.getTotalPrice()));
                servingsField.setText(String.valueOf(menu.getMaxServings()));

                // Cargar alimentos seleccionados
                selectFoodInCombo(mainDishCombo, menu.getMainDishId());
                selectFoodInCombo(sideDishCombo, menu.getSideDishId());
                selectFoodInCombo(beverageCombo, menu.getBeverageId());
                selectFoodInCombo(dessertCombo, menu.getDessertId());
            }
        }
    }

    private void selectFoodInCombo(JComboBox<String> combo, String foodId) {
        if (foodId != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).startsWith(foodId + " - ")) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
            combo.setSelectedIndex(0);
        } else {
            combo.setSelectedIndex(0);
        }
    }

    private String getSelectedFoodId(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null && !selected.isEmpty()) {
            return selected.split(" - ")[0];
        }
        return null;
    }

    private void addMenu() {
        try {
            String date = dateField.getText().trim();
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int maxServings = Integer.parseInt(servingsField.getText().trim());

            if (date.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La fecha y el nombre son obligatorios", 
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String mainDishId = getSelectedFoodId(mainDishCombo);
            String sideDishId = getSelectedFoodId(sideDishCombo);
            String beverageId = getSelectedFoodId(beverageCombo);
            String dessertId = getSelectedFoodId(dessertCombo);

            Menu menu = menuService.createMenu(date, name, description, 
                mainDishId, sideDishId, beverageId, dessertId, price, maxServings);

            JOptionPane.showMessageDialog(this, "Menú creado exitosamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            loadMenus();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio y los servicios deben ser números válidos", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al crear el menú: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editMenu() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un menú para editar", 
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String menuId = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<Menu> menuOpt = menuService.getMenuById(menuId);
            
            if (menuOpt.isPresent()) {
                Menu menu = menuOpt.get();
                
                String date = dateField.getText().trim();
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int maxServings = Integer.parseInt(servingsField.getText().trim());

                if (date.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La fecha y el nombre son obligatorios", 
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                menu.setDate(date);
                menu.setName(name);
                menu.setDescription(description);
                menu.setTotalPrice(price);
                menu.setMaxServings(maxServings);

                menu.setMainDishId(getSelectedFoodId(mainDishCombo));
                menu.setSideDishId(getSelectedFoodId(sideDishCombo));
                menu.setBeverageId(getSelectedFoodId(beverageCombo));
                menu.setDessertId(getSelectedFoodId(dessertCombo));

                if (menuService.updateMenu(menu)) {
                    JOptionPane.showMessageDialog(this, "Menú actualizado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadMenus();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el menú", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio y los servicios deben ser números válidos", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al editar el menú: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMenu() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un menú para eliminar", 
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea eliminar este menú?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String menuId = (String) tableModel.getValueAt(selectedRow, 0);
            if (menuService.deleteMenu(menuId)) {
                JOptionPane.showMessageDialog(this, "Menú eliminado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMenus();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el menú", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        dateField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        priceField.setText("");
        servingsField.setText("");
        mainDishCombo.setSelectedIndex(0);
        sideDishCombo.setSelectedIndex(0);
        beverageCombo.setSelectedIndex(0);
        dessertCombo.setSelectedIndex(0);
        menuTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuManagementView().setVisible(true);
        });
    }
}
