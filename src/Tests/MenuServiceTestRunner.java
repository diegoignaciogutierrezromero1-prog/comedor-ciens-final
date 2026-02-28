package Tests;

import Model.Menu;
import Model.MenuService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MenuServiceTestRunner {
    private static final String MENU_FILE_PATH = "src/Database/Menu/menus.json";

    public static void main(String[] args) {
        System.out.println("=== MenuService Test Runner ===");

        String originalContent = backupCurrentMenus();
        List<TestResult> results = new ArrayList<>();

        List<TestCase> tests = Arrays.asList(
                new TestCase("createMenuShouldPersistData", MenuServiceTestRunner::testCreateMenuShouldPersistData),
                new TestCase("getMenusByDateRangeShouldReturnMatchingMenus", MenuServiceTestRunner::testGetMenusByDateRangeShouldReturnMatchingMenus),
                new TestCase("deleteMenuShouldRemoveFromStorage", MenuServiceTestRunner::testDeleteMenuShouldRemoveFromStorage)
        );

        for (TestCase test : tests) {
            results.add(runTest(test));
        }

        restoreMenus(originalContent);

        long passed = results.stream().filter(TestResult::passed).count();
        long failed = results.size() - passed;

        System.out.println("=== Summary ===");
        results.forEach(result -> System.out.println(result.name() + ": " + (result.passed() ? "PASSED" : "FAILED -> " + result.message())));
        System.out.println("Total: " + results.size() + ", Passed: " + passed + ", Failed: " + failed);

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static TestResult runTest(TestCase testCase) {
        try {
            resetMenuFile();
            testCase.test().run();
            return new TestResult(testCase.name(), true, "");
        } catch (AssertionError | Exception e) {
            return new TestResult(testCase.name(), false, e.getMessage());
        }
    }

    private static void testCreateMenuShouldPersistData() {
        MenuService service = new MenuService();
        Menu menu = service.createMenu(
                LocalDate.now().toString(),
                "Menú Ejecutivo",
                "Menú completo",
                "food-001",
                "food-002",
                "food-003",
                "food-004",
                35.0,
                50
        );

        assertCondition(service.getAllMenus().size() == 1, "Debe existir un menú almacenado en memoria");

        MenuService reloaded = new MenuService();
        assertCondition(reloaded.getMenuById(menu.getId()).isPresent(), "El menú creado debe persistirse en el archivo");
    }

    private static void testGetMenusByDateRangeShouldReturnMatchingMenus() {
        MenuService service = new MenuService();
        service.createMenu("2026-02-20", "Menú A", "desc", null, null, null, null, 20.0, 10);
        service.createMenu("2026-02-21", "Menú B", "desc", null, null, null, null, 22.0, 12);
        service.createMenu("2026-02-25", "Menú C", "desc", null, null, null, null, 25.0, 15);

        List<Menu> filtered = service.getMenusByDateRange("2026-02-20", "2026-02-22");
        assertCondition(filtered.size() == 2, "Sólo dos menús deberían estar dentro del rango solicitado");
    }

    private static void testDeleteMenuShouldRemoveFromStorage() {
        MenuService service = new MenuService();
        Menu menu = service.createMenu("2026-02-20", "Menú A", "desc", null, null, null, null, 20.0, 10);

        assertCondition(service.deleteMenu(menu.getId()), "El menú debe eliminarse exitosamente");
        assertCondition(service.getAllMenus().isEmpty(), "No debe haber menús en memoria tras eliminar");

        MenuService reloaded = new MenuService();
        assertCondition(reloaded.getAllMenus().isEmpty(), "El archivo debe reflejar la eliminación del menú");
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void resetMenuFile() throws IOException {
        Path path = Paths.get(MENU_FILE_PATH);
        Files.createDirectories(path.getParent());
        Files.writeString(path, "[]", StandardCharsets.UTF_8);
    }

    private static String backupCurrentMenus() {
        Path path = Paths.get(MENU_FILE_PATH);
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("No se pudo respaldar menus.json: " + e.getMessage());
            return null;
        }
    }

    private static void restoreMenus(String originalContent) {
        Path path = Paths.get(MENU_FILE_PATH);
        try {
            if (originalContent == null) {
                Files.deleteIfExists(path);
            } else {
                Files.writeString(path, originalContent, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            System.err.println("No se pudo restaurar menus.json: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface TestExecutable {
        void run() throws Exception;
    }

    private record TestCase(String name, TestExecutable test) {}

    private record TestResult(String name, boolean passed, String message) {}
}
