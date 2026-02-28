package Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Wallet {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("balance")
    private double balance;
    
    @JsonProperty("lastUpdated")
    private String lastUpdated;
    
    @JsonProperty("active")
    private boolean active;

    public Wallet() {
        this.balance = 0.0;
        this.active = true;
        this.lastUpdated = java.time.LocalDateTime.now().toString();
    }

    public Wallet(String id, String userId, double initialBalance) {
        this.id = id;
        this.userId = userId;
        this.balance = initialBalance;
        this.active = true;
        this.lastUpdated = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { 
        this.balance = balance; 
        this.lastUpdated = java.time.LocalDateTime.now().toString();
    }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // Métodos de negocio
    public boolean addFunds(double amount) {
        if (amount > 0) {
            this.balance += amount;
            this.lastUpdated = java.time.LocalDateTime.now().toString();
            return true;
        }
        return false;
    }

    public boolean deductFunds(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            this.lastUpdated = java.time.LocalDateTime.now().toString();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", balance=" + balance +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", active=" + active +
                '}';
    }
}
