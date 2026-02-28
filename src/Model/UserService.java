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

public class UserService {
    private static final String FILE_PATH = "src/Database/User/users.json";
    private final ObjectMapper objectMapper;
    private List<User> users;

    public UserService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.users = loadUsers();
    }

    private List<User> loadUsers() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveUsers(new ArrayList<>());
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<User> users) {
        try {
            File file = new File(FILE_PATH);
            objectMapper.writeValue(file, users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public Optional<User> getUserById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public User createUser(String username, String password, String name, String role, String email) {
        String id = UUID.randomUUID().toString();
        User user = new User(id, username, password, name, role, email);
        users.add(user);
        saveUsers(users);
        return user;
    }

    public boolean updateUser(User user) {
        Optional<User> existingUser = getUserById(user.getId());
        if (existingUser.isPresent()) {
            users.remove(existingUser.get());
            users.add(user);
            saveUsers(users);
            return true;
        }
        return false;
    }

    public boolean deleteUser(String id) {
        Optional<User> user = getUserById(id);
        if (user.isPresent()) {
            users.remove(user.get());
            saveUsers(users);
            return true;
        }
        return false;
    }

    public boolean authenticateUser(String username, String password) {
        Optional<User> user = getUserByUsername(username);
        return user.isPresent() && 
               user.get().isActive() && 
               user.get().getPassword().equals(password);
    }
}
