package View;

import Model.User;
import Model.UserService;
import Utils.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementView extends JDialog {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton closeButton;
    private UserService userService;

    public UserManagementView(JFrame parent) {
        super(parent, "Gestión de Usuarios", true);
        this.userService = new UserService();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();
        loadUsers();
        setupEventHandlers();
    }

    private void initComponents() {
        // Tabla de usuarios
        String[] columns = {"ID", "Usuario", "Nombre", "Rol", "Email", "Activo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);

        // Botones
        addButton = new JButton("Agregar Usuario");
        editButton = new JButton("Editar Usuario");
        deleteButton = new JButton("Eliminar Usuario");
        refreshButton = new JButton("Actualizar");
        closeButton = new JButton("Cerrar");

        // Configurar estado inicial de botones
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void layoutComponents() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel de la tabla con scroll
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel de información
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Total de usuarios: "));
        JLabel countLabel = new JLabel("0");
        infoPanel.add(countLabel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        add(mainPanel);

        // Actualizar contador
        updateUserInfo();
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> {
            loadUsers();
            updateUserInfo();
        });
        closeButton.addActionListener(e -> dispose());

        // Selección de tabla
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = userTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole(),
                user.getEmail(),
                user.isActive() ? "Sí" : "No"
            };
            tableModel.addRow(row);
        }
    }

    private void updateUserInfo() {
        int userCount = userService.getAllUsers().size();
        // Actualizar el label de contador (necesitaríamos referencia a él)
    }

    private void showAddUserDialog() {
        UserDialog dialog = new UserDialog(this, "Agregar Usuario", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            User newUser = dialog.getUser();
            if (userService.createUser(
                newUser.getUsername(),
                newUser.getPassword(),
                newUser.getName(),
                newUser.getRole(),
                newUser.getEmail()
            ) != null) {
                loadUsers();
                updateUserInfo();
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear usuario", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = userService.getUserById(userId).orElse(null);
        
        if (user != null) {
            UserDialog dialog = new UserDialog(this, "Editar Usuario", user);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                User updatedUser = dialog.getUser();
                if (userService.updateUser(updatedUser)) {
                    loadUsers();
                    updateUserInfo();
                    JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar usuario", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el usuario '" + username + "'?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            if (userService.deleteUser(userId)) {
                loadUsers();
                updateUserInfo();
                JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
