package service;

import java.sql.*;
import java.util.Scanner;

public class UserService {
    private final Connection connection;

    public UserService(Connection connection) {
        this.connection = connection;
        createUsersTableIfNotExists();
    }

    private void createUsersTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "username VARCHAR(50) PRIMARY KEY, " +
                     "password VARCHAR(50))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating users table: " + e.getMessage());
        }
    }

    public boolean register(String name, String password) {
        if (userExists(name)) {
            System.out.println("User already exists.");
            return false;
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public boolean login(String name, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return password.equals(rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
        return false;
    }

    public boolean userExists(String name) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking user: " + e.getMessage());
            return false;
        }
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

            if ("admin".equals(currentUser)) {
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
                    if ("admin".equals(currentUser)) {
                        tracker.generateReports();
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
