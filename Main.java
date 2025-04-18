import service.HealthTrackerService;
import service.UserManager;
import util.DBManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserManager userManager = new UserManager();

        while (true) {
            System.out.println("\n=== Welcome to Health Tracker ===");
            System.out.print("Enter your name (or type 'exit' to quit): ");
            String name = scanner.nextLine();

            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (userManager.userExists(name)) {
                System.out.print("Enter your password: ");
                String password = scanner.nextLine();

                if (userManager.authenticate(name, password)) {
                    System.out.println("Login successful. Welcome back, " + name + "!");
                    runUserSession(scanner, name);
                } else {
                    System.out.println("Invalid password. Try again.");
                }

            } else {
                System.out.println("New user. Let's register.");
                System.out.print("Create a password: ");
                String password = scanner.nextLine();
                userManager.register(name, password);
                System.out.println("Registration successful. Welcome, " + name + "!");
                runUserSession(scanner, name);
            }
        }
    }

    private static void runUserSession(Scanner scanner, String currentUser) {
        try (Connection connection = DBManager.getConnection()) {
            HealthTrackerService service = new HealthTrackerService( currentUser);

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

                if (currentUser.equals("admin")) {
                    System.out.println("10. Generate System Report");
                }

                System.out.print("Choose an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> service.addRecord(scanner);
                    case "2" -> service.viewRecords();
                    case "3" -> service.updateRecord(scanner);
                    case "4" -> service.deleteRecord(scanner);
                    case "5" -> service.exportData();
                    case "6" -> service.setGoalAndSuggestWorkouts(scanner);
                    case "7" -> service.markWorkoutDone(scanner);
                    case "8" -> service.showStatistics();
                    case "9" -> {
                        System.out.println("Logged out.");
                        return;
                    }
                    case "10" -> {
                        if (currentUser.equals("admin")) {
                            service.generateReports();
                        } else {
                            System.out.println("Access denied.");
                        }
                    }
                    default -> System.out.println("Invalid option.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
