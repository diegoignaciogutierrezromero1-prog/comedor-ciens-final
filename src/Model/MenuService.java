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

public class MenuService {
    private static final String FILE_PATH = "src/Database/Menu/menus.json";
    private final ObjectMapper objectMapper;
    private List<Menu> menus;

    public MenuService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.findAndRegisterModules();
        this.menus = loadMenus();
    }

    private List<Menu> loadMenus() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveMenus(new ArrayList<>());
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Menu>>() {});
        } catch (IOException e) {
            System.err.println("Error loading menus: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveMenus(List<Menu> menus) {
        try {
            File file = new File(FILE_PATH);
            objectMapper.writeValue(file, menus);
        } catch (IOException e) {
            System.err.println("Error saving menus: " + e.getMessage());
        }
    }

    public List<Menu> getAllMenus() {
        return new ArrayList<>(menus);
    }

    public Optional<Menu> getMenuById(String id) {
        return menus.stream()
                .filter(menu -> menu.getId().equals(id))
                .findFirst();
    }

    public Optional<Menu> getMenuByDate(String date) {
        return menus.stream()
                .filter(menu -> menu.getDate().equals(date))
                .findFirst();
    }

    public List<Menu> getMenusByDateRange(String startDate, String endDate) {
        return menus.stream()
                .filter(menu -> menu.getDate().compareTo(startDate) >= 0 && menu.getDate().compareTo(endDate) <= 0)
                .toList();
    }

    public Menu createMenu(String date, String name, String description, 
                          String mainDishId, String sideDishId, String beverageId, String dessertId, 
                          double totalPrice, int maxServings) {
        String id = UUID.randomUUID().toString();
        Menu menu = new Menu(id, date, name, description, mainDishId, sideDishId, beverageId, dessertId, totalPrice, maxServings);
        menus.add(menu);
        saveMenus(menus);
        return menu;
    }

    public boolean updateMenu(Menu menu) {
        Optional<Menu> existingMenu = getMenuById(menu.getId());
        if (existingMenu.isPresent()) {
            menus.remove(existingMenu.get());
            menus.add(menu);
            saveMenus(menus);
            return true;
        }
        return false;
    }

    public boolean deleteMenu(String id) {
        Optional<Menu> menu = getMenuById(id);
        if (menu.isPresent()) {
            menus.remove(menu.get());
            saveMenus(menus);
            return true;
        }
        return false;
    }

    public boolean serveMenu(String menuId) {
        Optional<Menu> menu = getMenuById(menuId);
        if (menu.isPresent() && menu.get().serve()) {
            updateMenu(menu.get());
            return true;
        }
        return false;
    }

    public Optional<Menu> getTodayMenu() {
        return getMenuByDate(java.time.LocalDate.now().toString());
    }
}
