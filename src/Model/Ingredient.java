package Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ingredient {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("unit")
    private String unit; // "kg", "g", "l", "ml", "unidades"
    
    @JsonProperty("stock")
    private double stock;
    
    @JsonProperty("minStock")
    private double minStock;
    
    @JsonProperty("costPerUnit")
    private double costPerUnit;
    
    @JsonProperty("active")
    private boolean active;

    public Ingredient() {
        this.active = true;
        this.stock = 0.0;
        this.minStock = 0.0;
    }

    public Ingredient(String id, String name, String description, String unit, double stock, double minStock, double costPerUnit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.stock = stock;
        this.minStock = minStock;
        this.costPerUnit = costPerUnit;
        this.active = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }

    public double getMinStock() { return minStock; }
    public void setMinStock(double minStock) { this.minStock = minStock; }

    public double getCostPerUnit() { return costPerUnit; }
    public void setCostPerUnit(double costPerUnit) { this.costPerUnit = costPerUnit; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // Métodos de negocio
    public boolean needsRestock() {
        return stock <= minStock;
    }

    public boolean consume(double amount) {
        if (amount > 0 && stock >= amount) {
            stock -= amount;
            return true;
        }
        return false;
    }

    public void restock(double amount) {
        if (amount > 0) {
            stock += amount;
        }
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", stock=" + stock +
                ", minStock=" + minStock +
                ", active=" + active +
                '}';
    }
}
