package View;

import Model.Food;
import Model.FoodService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class FoodManagementView extends JFrame {
    private final FoodService foodService;
    private final DefaultTableModel tableModel;
    private final JTable foodTable;

    private final JTextField nameField;
    private final JTextField descriptionField;
    private final JComboBox<String> categoryCombo;
    private final JTextField priceField;
    private final JTextField caloriesField;
    private final JCheckBox availableCheck;

    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;
    private final JButton toggleButton;
    private final JButton refreshButton;

    public FoodManagementView() {
        this.foodService = new FoodService();

        setTitle("Gestión de Alimentos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Categoría", "Precio", "Calorías", "Disponibilidad"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        foodTable = new JTable(tableModel);
        foodTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        foodTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedFood();
            }
        });

        nameField = new JTextField(20);
        descriptionField = new JTextField(25);
        categoryCombo = new JComboBox<>(new String[]{
                "plato_principal", "guarnicion", "bebida", "postre"
        });
        priceField = new JTextField(10);
        caloriesField = new JTextField(7);
        availableCheck = new JCheckBox("Disponible", true);

        addButton = new JButton("Añadir");
        editButton = new JButton("Actualizar");
        deleteButton = new JButton("Eliminar");
        toggleButton = new JButton("Cambiar Disponibilidad");
        refreshButton = new JButton("Refrescar");

        addButton.addActionListener(e -> addFood());
        editButton.addActionListener(e -> editFood());
        deleteButton.addActionListener(e -> deleteFood());
        toggleButton.addActionListener(e -> toggleAvailability());
        refreshButton.addActionListener(e -> loadFoods());

        setLayout(new BorderLayout());
        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(foodTable), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadFoods();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Alimento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1;
        panel.add(categoryCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Precio (Bs.):"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Calorías:"), gbc);
        gbc.gridx = 1;
        panel.add(caloriesField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(availableCheck, gbc);

        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(toggleButton);
        panel.add(refreshButton);
        return panel;
    }

    private void loadFoods() {
        tableModel.setRowCount(0);
        List<Food> foods = foodService.getAllFoods();
        for (Food food : foods) {
            tableModel.addRow(new Object[]{
                    food.getId(),
                    food.getName(),
                    food.getCategory(),
                    food.getPrice(),
                    food.getCalories(),
                    food.isAvailable() ? "Disponible" : "No disponible"
            });
        }
        clearForm();
    }

    private void loadSelectedFood() {
        int selected = foodTable.getSelectedRow();
        if (selected < 0) {
            return;
        }
        String id = foodTable.getValueAt(selected, 0).toString();
        Optional<Food> foodOpt = foodService.getFoodById(id);
        if (foodOpt.isEmpty()) {
            return;
        }
        Food food = foodOpt.get();
        nameField.setText(food.getName());
        descriptionField.setText(food.getDescription());
        categoryCombo.setSelectedItem(food.getCategory());
        priceField.setText(String.valueOf(food.getPrice()));
        caloriesField.setText(String.valueOf(food.getCalories()));
        availableCheck.setSelected(food.isAvailable());
    }

    private void addFood() {
        try {
            Food food = foodService.createFood(
                    nameField.getText().trim(),
                    descriptionField.getText().trim(),
                    categoryCombo.getSelectedItem().toString(),
                    parseDouble(priceField.getText().trim(), "precio"),
                    parseInt(caloriesField.getText().trim(), "calorías")
            );
            food.setAvailable(availableCheck.isSelected());
            foodService.updateFood(food);
            loadFoods();
            JOptionPane.showMessageDialog(this, "Alimento creado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editFood() {
        int selected = foodTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un alimento para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = foodTable.getValueAt(selected, 0).toString();
        Optional<Food> foodOpt = foodService.getFoodById(id);
        if (foodOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontró el alimento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Food food = foodOpt.get();
            food.setName(nameField.getText().trim());
            food.setDescription(descriptionField.getText().trim());
            food.setCategory(categoryCombo.getSelectedItem().toString());
            food.setPrice(parseDouble(priceField.getText().trim(), "precio"));
            food.setCalories(parseInt(caloriesField.getText().trim(), "calorías"));
            food.setAvailable(availableCheck.isSelected());
            foodService.updateFood(food);
            loadFoods();
            JOptionPane.showMessageDialog(this, "Alimento actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFood() {
        int selected = foodTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un alimento para eliminar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = foodTable.getValueAt(selected, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este alimento?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (foodService.deleteFood(id)) {
                loadFoods();
                JOptionPane.showMessageDialog(this, "Alimento eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleAvailability() {
        int selected = foodTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un alimento", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = foodTable.getValueAt(selected, 0).toString();
        if (foodService.toggleAvailability(id)) {
            loadFoods();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo cambiar la disponibilidad", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseDouble(String value, String fieldName) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + fieldName + " debe ser numérico");
        }
    }

    private int parseInt(String value, String fieldName) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + fieldName + " debe ser entero");
        }
    }

    private void clearForm() {
        nameField.setText("");
        descriptionField.setText("");
        categoryCombo.setSelectedIndex(0);
        priceField.setText("");
        caloriesField.setText("");
        availableCheck.setSelected(true);
        foodTable.clearSelection();
    }
}
