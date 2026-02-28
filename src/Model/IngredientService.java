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

public class IngredientService {
    private static final String FILE_PATH = "src/Database/Ingredient/ingredients.json";
    private final ObjectMapper objectMapper;
    private List<Ingredient> ingredients;

    public IngredientService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.ingredients = loadIngredients();
    }

    private List<Ingredient> loadIngredients() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveIngredients(new ArrayList<>());
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Ingredient>>() {});
        } catch (IOException e) {
            System.err.println("Error loading ingredients: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveIngredients(List<Ingredient> ingredients) {
        try {
            File file = new File(FILE_PATH);
            objectMapper.writeValue(file, ingredients);
        } catch (IOException e) {
            System.err.println("Error saving ingredients: " + e.getMessage());
        }
    }

    public List<Ingredient> getAllIngredients() {
        return new ArrayList<>(ingredients);
    }

    public Optional<Ingredient> getIngredientById(String id) {
        return ingredients.stream()
                .filter(ingredient -> ingredient.getId().equals(id))
                .findFirst();
    }

    public List<Ingredient> getIngredientsNeedingRestock() {
        return ingredients.stream()
                .filter(Ingredient::needsRestock)
                .filter(Ingredient::isActive)
                .toList();
    }

    public Ingredient createIngredient(String name, String description, String unit, double stock, double minStock, double costPerUnit) {
        String id = UUID.randomUUID().toString();
        Ingredient ingredient = new Ingredient(id, name, description, unit, stock, minStock, costPerUnit);
        ingredients.add(ingredient);
        saveIngredients(ingredients);
        return ingredient;
    }

    public boolean updateIngredient(Ingredient ingredient) {
        Optional<Ingredient> existingIngredient = getIngredientById(ingredient.getId());
        if (existingIngredient.isPresent()) {
            ingredients.remove(existingIngredient.get());
            ingredients.add(ingredient);
            saveIngredients(ingredients);
            return true;
        }
        return false;
    }

    public boolean deleteIngredient(String id) {
        Optional<Ingredient> ingredient = getIngredientById(id);
        if (ingredient.isPresent()) {
            ingredients.remove(ingredient.get());
            saveIngredients(ingredients);
            return true;
        }
        return false;
    }

    public boolean consumeIngredient(String id, double amount) {
        Optional<Ingredient> ingredient = getIngredientById(id);
        if (ingredient.isPresent() && ingredient.get().consume(amount)) {
            updateIngredient(ingredient.get());
            return true;
        }
        return false;
    }

    public boolean restockIngredient(String id, double amount) {
        Optional<Ingredient> ingredient = getIngredientById(id);
        if (ingredient.isPresent()) {
            ingredient.get().restock(amount);
            updateIngredient(ingredient.get());
            return true;
        }
        return false;
    }
}
