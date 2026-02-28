package Model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WalletService {
    private static final String FILE_PATH = "src/Database/Wallet/wallets.json";
    private final ObjectMapper objectMapper;
    private List<Wallet> wallets;

    public WalletService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.wallets = loadWallets();
    }

    private List<Wallet> loadWallets() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveWallets(new ArrayList<>());
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Wallet>>() {});
        } catch (IOException e) {
            System.err.println("Error loading wallets: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveWallets(List<Wallet> wallets) {
        try {
            File file = new File(FILE_PATH);
            objectMapper.writeValue(file, wallets);
        } catch (IOException e) {
            System.err.println("Error saving wallets: " + e.getMessage());
        }
    }

    public List<Wallet> getAllWallets() {
        return new ArrayList<>(wallets);
    }

    public Optional<Wallet> getWalletById(String id) {
        return wallets.stream()
                .filter(wallet -> wallet.getId().equals(id))
                .findFirst();
    }

    public Optional<Wallet> getWalletByUserId(String userId) {
        return wallets.stream()
                .filter(wallet -> wallet.getUserId().equals(userId))
                .findFirst();
    }

    public Wallet createWallet(String userId, double initialBalance) {
        String id = UUID.randomUUID().toString();
        Wallet wallet = new Wallet(id, userId, initialBalance);
        wallets.add(wallet);
        saveWallets(wallets);
        return wallet;
    }

    public boolean updateWallet(Wallet wallet) {
        Optional<Wallet> existingWallet = getWalletById(wallet.getId());
        if (existingWallet.isPresent()) {
            wallets.remove(existingWallet.get());
            wallets.add(wallet);
            saveWallets(wallets);
            return true;
        }
        return false;
    }

    public boolean addFunds(String userId, double amount) {
        Optional<Wallet> wallet = getWalletByUserId(userId);
        if (wallet.isPresent() && wallet.get().addFunds(amount)) {
            updateWallet(wallet.get());
            return true;
        }
        return false;
    }

    public boolean deductFunds(String userId, double amount) {
        Optional<Wallet> wallet = getWalletByUserId(userId);
        if (wallet.isPresent() && wallet.get().deductFunds(amount)) {
            updateWallet(wallet.get());
            return true;
        }
        return false;
    }

    public double getBalance(String userId) {
        Optional<Wallet> wallet = getWalletByUserId(userId);
        return wallet.map(Wallet::getBalance).orElse(0.0);
    }
}
