package service;

import java.io.*;
import java.util.*;

import util.ActivityLogger;

public class UserManager {
    private static final String USER_FILE = "data/users.csv";
    private final Map<String, String> users = new HashMap<>();

    public UserManager() {
        loadUsers();
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]); // name -> password
                }
            }
        } catch (IOException e) {
            System.out.println("No users found. Starting fresh.");
        }
    }

    public boolean userExists(String name) {
        return users.containsKey(name);
    }

    public boolean authenticate(String name, String password) {
        return users.containsKey(name) && users.get(name).equals(password);
    }
public boolean register(String name, String password) {
    if (users.containsKey(name)) {
        System.out.println("User already exists.");
        return false;
    }
    users.put(name, password);
    saveUsers();

   
    ActivityLogger.log(name, "register", "new user registered");

    return true;
}
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save users.");
        }
    }
}
