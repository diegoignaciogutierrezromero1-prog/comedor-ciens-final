package View;

import Model.User;
import Model.Wallet;
import Model.WalletService;
import Utils.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WalletView extends JDialog {
    private User currentUser;
    private WalletService walletService;
    private Wallet wallet;
    
    private JLabel balanceLabel;
    private JLabel userIdLabel;
    private JLabel userNameLabel;
    private JTextField amountField;
    private JButton addFundsButton;
    private JButton transactionHistoryButton;
    private JButton closeButton;

    public WalletView(JFrame parent) {
        super(parent, "Mi Monedero", true);
        this.currentUser = UserSession.getInstance().getCurrentUser();
        this.walletService = new WalletService();
        
        // Cargar el monedero del usuario
        this.wallet = walletService.getWalletByUserId(currentUser.getId()).orElse(null);
        
        if (wallet == null) {
            // Crear monedero si no existe
            wallet = walletService.createWallet(currentUser.getId(), 0.0);
        }
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();
        setupEventHandlers();
        updateBalance();
    }

    private void initComponents() {
        balanceLabel = new JLabel("Bs. 0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balanceLabel.setForeground(new Color(0, 128, 0));
        
        userIdLabel = new JLabel("ID: " + currentUser.getId());
        userNameLabel = new JLabel("Usuario: " + currentUser.getName());
        
        amountField = new JTextField(10);
        amountField.setHorizontalAlignment(JTextField.RIGHT);
        
        addFundsButton = new JButton("Recargar Monedero");
        transactionHistoryButton = new JButton("Ver Historial");
        closeButton = new JButton("Cerrar");
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel superior con información del usuario
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userInfoPanel.add(userIdLabel);
        userInfoPanel.add(userNameLabel);
        mainPanel.add(userInfoPanel, BorderLayout.NORTH);
        
        // Panel central con el balance
        JPanel balancePanel = new JPanel(new BorderLayout());
        balancePanel.setBorder(BorderFactory.createTitledBorder("Saldo Actual"));
        balancePanel.add(balanceLabel, BorderLayout.CENTER);
        mainPanel.add(balancePanel, BorderLayout.CENTER);
        
        // Panel inferior con acciones
        JPanel actionPanel = new JPanel(new BorderLayout());
        
        // Panel de recarga
        JPanel rechargePanel = new JPanel(new FlowLayout());
        rechargePanel.add(new JLabel("Monto a recargar:"));
        rechargePanel.add(amountField);
        rechargePanel.add(addFundsButton);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(transactionHistoryButton);
        buttonPanel.add(closeButton);
        
        actionPanel.add(rechargePanel, BorderLayout.NORTH);
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void setupEventHandlers() {
        addFundsButton.addActionListener(e -> addFunds());
        transactionHistoryButton.addActionListener(e -> showTransactionHistory());
        closeButton.addActionListener(e -> dispose());
        
        // Permitir solo números en el campo de monto
        amountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.' || c == ',')) {
                    e.consume();
                }
            }
        });
    }

    private void updateBalance() {
        if (wallet != null) {
            balanceLabel.setText(String.format("Bs. %.2f", wallet.getBalance()));
        }
    }

    private void addFunds() {
        String amountText = amountField.getText().trim();
        
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese un monto válido", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Reemplazar coma por punto para decimales
            amountText = amountText.replace(',', '.');
            double amount = Double.parseDouble(amountText);
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "El monto debe ser mayor a cero", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int result = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de recargar Bs. " + String.format("%.2f", amount) + "?",
                "Confirmar Recarga",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                if (walletService.addFunds(currentUser.getId(), amount)) {
                    // Recargar el wallet para obtener los datos actualizados
                    wallet = walletService.getWalletByUserId(currentUser.getId()).orElse(null);
                    updateBalance();
                    amountField.setText("");
                    
                    JOptionPane.showMessageDialog(this, 
                        "Recarga exitosa de Bs. " + String.format("%.2f", amount), 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al procesar la recarga", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Monto inválido. Use formato numérico (ej: 50.00)", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTransactionHistory() {
        // TODO: Implementar historial de transacciones
        JOptionPane.showMessageDialog(this, 
            "Historial de transacciones en desarrollo", 
            "Historial", JOptionPane.INFORMATION_MESSAGE);
    }
}
