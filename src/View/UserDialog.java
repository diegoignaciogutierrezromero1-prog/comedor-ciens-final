package View;

import Model.User;

import javax.swing.*;
import java.awt.*;

public class UserDialog extends JDialog {
    private JTextField usernameField;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JCheckBox activeCheckBox;
    private JButton okButton;
    private JButton cancelButton;
    
    private boolean confirmed = false;
    private User user;

    public UserDialog(Dialog parent, String title, User user) {
        super(parent, title, true);
        this.user = user;
        
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();
        setupEventHandlers();
        
        if (user != null) {
            loadUserData();
        }
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        String[] roles = {"admin", "student", "staff"};
        roleComboBox = new JComboBox<>(roles);
        
        activeCheckBox = new JCheckBox("Usuario Activo");
        activeCheckBox.setSelected(true);
        
        okButton = new JButton("Aceptar");
        cancelButton = new JButton("Cancelar");
        
        // Si es edición, la contraseña no es obligatoria
        if (user != null) {
            passwordField.setToolTipText("Dejar en blanco para mantener la contraseña actual");
            confirmPasswordField.setToolTipText("Dejar en blanco para mantener la contraseña actual");
        }
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(emailField, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);

        // Confirmar Contraseña
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Confirmar:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(confirmPasswordField, gbc);

        // Rol
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(roleComboBox, gbc);

        // Activo
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(activeCheckBox, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        okButton.addActionListener(e -> {
            if (validateForm()) {
                saveUserData();
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void loadUserData() {
        if (user != null) {
            usernameField.setText(user.getUsername());
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            roleComboBox.setSelectedItem(user.getRole());
            activeCheckBox.setSelected(user.isActive());
            
            // En edición, los campos de contraseña están vacíos
            passwordField.setText("");
            confirmPasswordField.setText("");
            
            usernameField.setEnabled(false); // No permitir cambiar username en edición
        }
    }

    private boolean validateForm() {
        String username = usernameField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El usuario es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return false;
        }

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El email es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "El email no es válido", "Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        // Validar contraseña solo para nuevos usuarios o si se quiere cambiar
        if (user == null || !password.isEmpty()) {
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña es obligatoria para nuevos usuarios", "Error", JOptionPane.ERROR_MESSAGE);
                passwordField.requestFocus();
                return false;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                confirmPasswordField.requestFocus();
                return false;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
                passwordField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void saveUserData() {
        if (user == null) {
            // Nuevo usuario
            user = new User();
        }

        user.setUsername(usernameField.getText().trim());
        user.setName(nameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setRole((String) roleComboBox.getSelectedItem());
        user.setActive(activeCheckBox.isSelected());

        // Solo actualizar contraseña si se proporcionó una nueva
        String password = new String(passwordField.getPassword());
        if (!password.isEmpty()) {
            user.setPassword(password);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public User getUser() {
        return user;
    }
}
