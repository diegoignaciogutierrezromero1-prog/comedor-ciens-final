package View;

import Model.Ingredient;
import Model.IngredientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class IngredientManagementView extends JFrame {
    private final IngredientService ingredientService;
    private final DefaultTableModel tableModel;
    private final JTable ingredientTable;

    private final JTextField nameField;
    private final JTextField descriptionField;
    private final JComboBox<String> unitCombo;
    private final JTextField stockField;
    private final JTextField minStockField;
    private final JTextField costField;
    private final JCheckBox activeCheck;

    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;
    private final JButton consumeButton;
    private final JButton restockButton;
    private final JButton refreshButton;

    public IngredientManagementView() {
        this.ingredientService = new IngredientService();

        setTitle("Gestión de Ingredientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Unidad", "Stock", "Stock Mínimo", "Costo Unidad", "Activo"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ingredientTable = new JTable(tableModel);
        ingredientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ingredientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedIngredient();
            }
        });

        nameField = new JTextField(20);
        descriptionField = new JTextField(25);
        unitCombo = new JComboBox<>(new String[]{"kg", "g", "l", "ml", "unidad"});
        stockField = new JTextField(8);
        minStockField = new JTextField(8);
        costField = new JTextField(8);
        activeCheck = new JCheckBox("Activo", true);

        addButton = new JButton("Añadir");
        editButton = new JButton("Actualizar");
        deleteButton = new JButton("Eliminar");
        consumeButton = new JButton("Consumir");
        restockButton = new JButton("Reponer");
        refreshButton = new JButton("Refrescar");

        addButton.addActionListener(e -> addIngredient());
        editButton.addActionListener(e -> editIngredient());
        deleteButton.addActionListener(e -> deleteIngredient());
        consumeButton.addActionListener(e -> consumeIngredient());
        restockButton.addActionListener(e -> restockIngredient());
        refreshButton.addActionListener(e -> loadIngredients());

        setLayout(new BorderLayout());
        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(ingredientTable), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadIngredients();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Ingrediente"));
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
        panel.add(new JLabel("Unidad:"), gbc);
        gbc.gridx = 1;
        panel.add(unitCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Stock Actual:"), gbc);
        gbc.gridx = 1;
        panel.add(stockField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Stock Mínimo:"), gbc);
        gbc.gridx = 1;
        panel.add(minStockField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Costo por Unidad (Bs.):"), gbc);
        gbc.gridx = 1;
        panel.add(costField, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        panel.add(activeCheck, gbc);

        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(consumeButton);
        panel.add(restockButton);
        panel.add(refreshButton);
        return panel;
    }

    private void loadIngredients() {
        tableModel.setRowCount(0);
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        for (Ingredient ingredient : ingredients) {
            tableModel.addRow(new Object[]{
                    ingredient.getId(),
                    ingredient.getName(),
                    ingredient.getUnit(),
                    ingredient.getStock(),
                    ingredient.getMinStock(),
                    ingredient.getCostPerUnit(),
                    ingredient.isActive() ? "Sí" : "No"
            });
        }
        clearForm();
    }

    private void loadSelectedIngredient() {
        int selected = ingredientTable.getSelectedRow();
        if (selected < 0) {
            return;
        }
        String id = ingredientTable.getValueAt(selected, 0).toString();
        Optional<Ingredient> ingredientOpt = ingredientService.getIngredientById(id);
        if (ingredientOpt.isEmpty()) {
            return;
        }
        Ingredient ingredient = ingredientOpt.get();
        nameField.setText(ingredient.getName());
        descriptionField.setText(ingredient.getDescription());
        unitCombo.setSelectedItem(ingredient.getUnit());
        stockField.setText(String.valueOf(ingredient.getStock()));
        minStockField.setText(String.valueOf(ingredient.getMinStock()));
        costField.setText(String.valueOf(ingredient.getCostPerUnit()));
        activeCheck.setSelected(ingredient.isActive());
    }

    private void addIngredient() {
        try {
            Ingredient ingredient = ingredientService.createIngredient(
                    nameField.getText().trim(),
                    descriptionField.getText().trim(),
                    unitCombo.getSelectedItem().toString(),
                    parseDouble(stockField.getText().trim(), "stock"),
                    parseDouble(minStockField.getText().trim(), "stock mínimo"),
                    parseDouble(costField.getText().trim(), "costo")
            );
            ingredient.setActive(activeCheck.isSelected());
            ingredientService.updateIngredient(ingredient);
            loadIngredients();
            JOptionPane.showMessageDialog(this, "Ingrediente creado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editIngredient() {
        int selected = ingredientTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ingrediente", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = ingredientTable.getValueAt(selected, 0).toString();
        Optional<Ingredient> ingredientOpt = ingredientService.getIngredientById(id);
        if (ingredientOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontró el ingrediente", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Ingredient ingredient = ingredientOpt.get();
            ingredient.setName(nameField.getText().trim());
            ingredient.setDescription(descriptionField.getText().trim());
            ingredient.setUnit(unitCombo.getSelectedItem().toString());
            ingredient.setStock(parseDouble(stockField.getText().trim(), "stock"));
            ingredient.setMinStock(parseDouble(minStockField.getText().trim(), "stock mínimo"));
            ingredient.setCostPerUnit(parseDouble(costField.getText().trim(), "costo"));
            ingredient.setActive(activeCheck.isSelected());
            ingredientService.updateIngredient(ingredient);
            loadIngredients();
            JOptionPane.showMessageDialog(this, "Ingrediente actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteIngredient() {
        int selected = ingredientTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ingrediente", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = ingredientTable.getValueAt(selected, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este ingrediente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (ingredientService.deleteIngredient(id)) {
                loadIngredients();
                JOptionPane.showMessageDialog(this, "Ingrediente eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void consumeIngredient() {
        int selected = ingredientTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ingrediente", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String amountStr = JOptionPane.showInputDialog(this, "Cantidad a consumir:");
        if (amountStr == null || amountStr.isBlank()) {
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr.replace(',', '.'));
            String id = ingredientTable.getValueAt(selected, 0).toString();
            if (ingredientService.consumeIngredient(id, amount)) {
                loadIngredients();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo consumir la cantidad indicada", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restockIngredient() {
        int selected = ingredientTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ingrediente", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String amountStr = JOptionPane.showInputDialog(this, "Cantidad a reponer:");
        if (amountStr == null || amountStr.isBlank()) {
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr.replace(',', '.'));
            String id = ingredientTable.getValueAt(selected, 0).toString();
            if (ingredientService.restockIngredient(id, amount)) {
                loadIngredients();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo reponer la cantidad indicada", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseDouble(String value, String field) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("El campo " + field + " es obligatorio");
        }
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo " + field + " debe ser numérico");
        }
    }

    private void clearForm() {
        nameField.setText("");
        descriptionField.setText("");
        unitCombo.setSelectedIndex(0);
        stockField.setText("");
        minStockField.setText("");
        costField.setText("");
        activeCheck.setSelected(true);
        ingredientTable.clearSelection();
    }
}
