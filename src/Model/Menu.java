package Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.ArrayList;

public class Menu {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("date")
    private String date;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("mainDish")
    private String mainDishId;
    
    @JsonProperty("sideDish")
    private String sideDishId;
    
    @JsonProperty("beverage")
    private String beverageId;
    
    @JsonProperty("dessert")
    private String dessertId;
    
    @JsonProperty("totalPrice")
    private double totalPrice;
    
    @JsonProperty("available")
    private boolean available;
    
    @JsonProperty("maxServings")
    private int maxServings;
    
    @JsonProperty("currentServings")
    private int currentServings;

    public Menu() {
        this.available = true;
        this.maxServings = 100;
        this.currentServings = 0;
        this.totalPrice = 0.0;
    }

    public Menu(String id, String date, String name, String description, 
                String mainDishId, String sideDishId, String beverageId, String dessertId, 
                double totalPrice, int maxServings) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.description = description;
        this.mainDishId = mainDishId;
        this.sideDishId = sideDishId;
        this.beverageId = beverageId;
        this.dessertId = dessertId;
        this.totalPrice = totalPrice;
        this.maxServings = maxServings;
        this.currentServings = 0;
        this.available = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMainDishId() { return mainDishId; }
    public void setMainDishId(String mainDishId) { this.mainDishId = mainDishId; }

    public String getSideDishId() { return sideDishId; }
    public void setSideDishId(String sideDishId) { this.sideDishId = sideDishId; }

    public String getBeverageId() { return beverageId; }
    public void setBeverageId(String beverageId) { this.beverageId = beverageId; }

    public String getDessertId() { return dessertId; }
    public void setDessertId(String dessertId) { this.dessertId = dessertId; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getMaxServings() { return maxServings; }
    public void setMaxServings(int maxServings) { this.maxServings = maxServings; }

    public int getCurrentServings() { return currentServings; }
    public void setCurrentServings(int currentServings) { this.currentServings = currentServings; }

    // Métodos de negocio
    public boolean canServe() {
        return available && currentServings < maxServings;
    }

    public boolean serve() {
        if (canServe()) {
            currentServings++;
            return true;
        }
        return false;
    }

    public int getRemainingServings() {
        return maxServings - currentServings;
    }

    public List<String> getFoodIds() {
        List<String> foodIds = new ArrayList<>();
        if (mainDishId != null) foodIds.add(mainDishId);
        if (sideDishId != null) foodIds.add(sideDishId);
        if (beverageId != null) foodIds.add(beverageId);
        if (dessertId != null) foodIds.add(dessertId);
        return foodIds;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", name='" + name + '\'' +
                ", totalPrice=" + totalPrice +
                ", available=" + available +
                ", servings=" + currentServings + "/" + maxServings +
                '}';
    }
}
