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

    public void run(Scanner scanner, String currentUser) {
        HealthTrackerService tracker = new HealthTrackerService(currentUser);
    
        while (true) {
            System.out.println("\n=== Health Parameter Tracker ===");
            System.out.println("1. Add Record");
            System.out.println("2. View My Records");
            System.out.println("3. Update My Record");
            System.out.println("4. Delete My Record (admin only)");
            System.out.println("5. Export My Data");
            System.out.println("6. Set Goal & Training Plan");
            System.out.println("7. Mark Daily Training Done");
            System.out.println("8. Show My Statistics");
            System.out.println("9. Logout");
    
            // 💡 Пункт 10 только для админа
            if (currentUser.equals("admin")) {
                System.out.println("10. Generate System Report");
            }
    
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
    
            switch (choice) {
                case "1": tracker.addRecord(scanner); break;
                case "2": tracker.viewRecords(); break;
                case "3": tracker.updateRecord(scanner); break;
                case "4": tracker.deleteRecord(scanner); break;
                case "5": tracker.exportData(); break;
                case "6": tracker.setGoalAndSuggestWorkouts(scanner); break;
                case "7": tracker.markWorkoutDone(scanner); break;
                case "8": tracker.showStatistics(); break;
                case "9":
                    System.out.println("Logging out...");
                    return;
                case "10":
                    if (currentUser.equals("admin")) {
                        tracker.generateReports(); // 💥 вызываем метод генерации отчёта
                    } else {
                        System.out.println("Access denied.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    
}
