package service;

import java.io.*;
import java.util.*;

public class UserService {
    private static final String USERS_FILE = "data/users.csv";
    private final Map<String, String> users = new HashMap<>();

    public UserService() {
        loadUsers();
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public boolean register(String name, String password) {
        if (users.containsKey(name)) {
            System.out.println("User already exists.");
            return false;
        }
        users.put(name, password);
        saveUsers();
        return true;
    }

    public boolean login(String name, String password) {
        return password.equals(users.get(name));
    }

    public boolean userExists(String name) {
        return users.containsKey(name);
    }
}
