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

public class FoodService {
    private static final String FILE_PATH = "src/Database/Food/foods.json";
    private final ObjectMapper objectMapper;
    private List<Food> foods;

    public FoodService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.foods = loadFoods();
    }

    private List<Food> loadFoods() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveFoods(new ArrayList<>());
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Food>>() {});
        } catch (IOException e) {
            System.err.println("Error loading foods: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveFoods(List<Food> foods) {
        try {
            File file = new File(FILE_PATH);
            objectMapper.writeValue(file, foods);
        } catch (IOException e) {
            System.err.println("Error saving foods: " + e.getMessage());
        }
    }

    public List<Food> getAllFoods() {
        return new ArrayList<>(foods);
    }

    public List<Food> getFoodsByCategory(String category) {
        return foods.stream()
                .filter(food -> food.getCategory().equals(category))
                .filter(Food::isAvailable)
                .toList();
    }

    public Optional<Food> getFoodById(String id) {
        return foods.stream()
                .filter(food -> food.getId().equals(id))
                .findFirst();
    }

    public Food createFood(String name, String description, String category, double price, int calories) {
        String id = UUID.randomUUID().toString();
        Food food = new Food(id, name, description, category, price, calories);
        foods.add(food);
        saveFoods(foods);
        return food;
    }

    public boolean updateFood(Food food) {
        Optional<Food> existingFood = getFoodById(food.getId());
        if (existingFood.isPresent()) {
            foods.remove(existingFood.get());
            foods.add(food);
            saveFoods(foods);
            return true;
        }
        return false;
    }

    public boolean deleteFood(String id) {
        Optional<Food> food = getFoodById(id);
        if (food.isPresent()) {
            foods.remove(food.get());
            saveFoods(foods);
            return true;
        }
        return false;
    }

    public boolean toggleAvailability(String id) {
        Optional<Food> food = getFoodById(id);
        if (food.isPresent()) {
            food.get().setAvailable(!food.get().isAvailable());
            updateFood(food.get());
            return true;
        }
        return false;
    }
}
