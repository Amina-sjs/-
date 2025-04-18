package service;

import model.HealthRecord;
import util.DBManager;
import util.Validator;

import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.sql.Date;


public class HealthTrackerService {
    private final String currentUser;
    private String goal;
    private String selectedWorkout;

    public HealthTrackerService(String currentUser) {
        this.currentUser = currentUser;
        loadGoal();
    }

    public void run(Scanner scanner) {
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

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addRecord(scanner);
                case "2" -> viewRecords();
                case "3" -> updateRecord(scanner);
                case "4" -> deleteRecord(scanner);
                case "5" -> exportData();
                case "6" -> setGoalAndSuggestWorkouts(scanner);
                case "7" -> markWorkoutDone(scanner);
                case "8" -> showStatistics();
                case "9" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public void addRecord(Scanner scanner) {
        try (Connection connection = DBManager.getConnection()) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = scanner.nextLine();
            System.out.print("Enter weight (kg): ");
            double weight = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter blood pressure (e.g., 120/80): ");
            String bloodPressure = scanner.nextLine();
            String[] bpParts = bloodPressure.split("/");
            if (bpParts.length != 2) {
                System.out.println("Invalid blood pressure format.");
                return;
            }
             
            System.out.print("Enter exercise details: ");
            String exercise = scanner.nextLine();

            if (!Validator.isValidDate(date) || weight <= 0) {
                System.out.println("Invalid input.");
                return;
            }

            String sql = "INSERT INTO health_records (user_id, date, weight, blood_pressure, exercise, goal) VALUES ((SELECT id FROM users WHERE name = ?), ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, currentUser);
                stmt.setString(2, currentUser);
                stmt.setDate(3, Date.valueOf(date));
                stmt.setDouble(4, weight);
                stmt.setString(5, bloodPressure);
                stmt.setString(6, exercise);
                stmt.setString(7, goal); 
                stmt.executeUpdate();
                System.out.println("Record added!");
                logActivity(currentUser, "add_record");
            }
        } catch (Exception e) {
            System.out.println("Failed to add record: " + e.getMessage());
        }
    }

    public void viewRecords() {
        String sql = currentUser.equals("admin") ?
            "SELECT u.id, h.date, h.weight, h.blood_pressure, h.exercise, h.goal FROM health_records h JOIN users u ON h.username " +
            "FROM health_records h " +
            "JOIN users u ON h.user_id = u.id " +
            "WHERE u.name = ?";
            
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!currentUser.equals("admin")) {
                stmt.setString(1, currentUser);
            }
            ResultSet rs = stmt.executeQuery();
            boolean found = false;
            while (rs.next()) {
                String userInfo = currentUser.equals("admin") ? ("User: " + rs.getString("name") + " | ") : "";
                System.out.printf("ID: " + rs.getInt("id") +
                               ", Date: " + rs.getDate("date") +
                               ", Weight: " + rs.getDouble("weight") +
                               ", Blood Pressure: " + rs.getString("blood_pressure") +
                               ", Exercise: " + rs.getString("exercise"));

            }
            if (!found) System.out.println("No records found.");
        } catch (SQLException e) {
            System.out.println("Failed to retrieve records: " + e.getMessage());
        }
    }

    public void updateRecord(Scanner scanner) {
        try (Connection conn = DBManager.getConnection()) {
            String selectSQL = "SELECT h.*, u.name FROM health_records h JOIN users u ON h.user_id = u.id" +
                    (currentUser.equals("admin") ? "" : " WHERE u.name = ?");
            List<HealthRecord> records = new ArrayList<>();

            try (PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
                if (!currentUser.equals("admin")) {
                    stmt.setString(1, currentUser);
                }
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    records.add(new HealthRecord(
                            rs.getString("name"),
                            rs.getDate("date").toString(),
                            rs.getDouble("weight"),
                            rs.getInt("systolic"),
                            rs.getInt("diastolic"),
                            rs.getString("exercise")
                    ));
                }
            }

            if (records.isEmpty()) {
                System.out.println("No records found.");
                return;
            }

            for (int i = 0; i < records.size(); i++) {
                System.out.println((i + 1) + ". " + records.get(i));
            }

            System.out.print("Choose record to update: ");
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            HealthRecord toUpdate = records.get(choice);

            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM health_records WHERE user_id = (SELECT id FROM users WHERE name = ?) AND date = ?")) {
                deleteStmt.setString(1, toUpdate.getName());
                deleteStmt.setDate(2, Date.valueOf(toUpdate.getDate()));
                deleteStmt.executeUpdate();
            }

            addRecord(scanner);
            logActivity(currentUser, "update_record");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void deleteRecord(Scanner scanner) {
        if (!currentUser.equals("admin")) {
            System.out.println("Only admin can delete records.");
            return;
        }

        viewRecords();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter date of record to delete (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM health_records WHERE user_id = (SELECT id FROM users WHERE name = ?) AND date = ?")) {
            stmt.setString(1, username);
            stmt.setDate(2, Date.valueOf(date));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Record deleted.");
                logActivity(currentUser, "delete_record");
            } else {
                System.out.println("No record found.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    public void exportData() {
        String sql = "SELECT date, weight, systolic, diastolic, exercise FROM health_records WHERE user_id = (SELECT id FROM users WHERE name = ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            try (PrintWriter writer = new PrintWriter("export_" + currentUser + ".csv")) {
                writer.println("Date,Weight,BP,Exercise");
                while (rs.next()) {
                    String line = rs.getDate("date") + "," +
                            rs.getDouble("weight") + "," +
                            rs.getString("blood_pressure") + "," +
                            rs.getString("exercise");
                    writer.println(line);
                }
                System.out.println("Data exported.");
                logActivity(currentUser, "export_data");
            }
        } catch (Exception e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    public void showStatistics() {
        String sql = "SELECT AVG(weight) as avg_weight, AVG(systolic) as avg_sys, AVG(diastolic) as avg_dia FROM health_records WHERE user_id = (SELECT id FROM users WHERE name = ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.printf("Average Weight: %.2f kg\n", rs.getDouble("avg_weight"));
                System.out.printf("Average BP: %d/%d\n", rs.getInt("avg_sys"), rs.getInt("avg_dia"));
            }
        } catch (Exception e) {
            System.out.println("Failed to show stats: " + e.getMessage());
        }

        System.out.println("Your goal: " + (goal != null ? goal : "Not set"));
        System.out.println("Your workout: " + (selectedWorkout != null ? selectedWorkout : "Not set"));
    }

    public void setGoalAndSuggestWorkouts(Scanner scanner) {
        try {
            System.out.print("Enter your goal (e.g., lose weight): ");
            String inputGoal = scanner.nextLine().trim();
            if (inputGoal.isEmpty()) {
                System.out.println("Goal cannot be empty.");
                return;
            }

            System.out.print("Choose workout plan (e.g., running, yoga): ");
            String workout = scanner.nextLine().trim();
            if (workout.isEmpty()) {
                System.out.println("Workout plan cannot be empty.");
                return;
            }

            this.goal = inputGoal;
            this.selectedWorkout = workout;
            saveGoal();
            System.out.println("Goal and workout plan saved successfully.");
            logActivity(currentUser, "set_goal");
        } catch (Exception e) {
            System.out.println("Failed to set goal and workout: " + e.getMessage());
        }
    }

    public void markWorkoutDone(Scanner scanner) {
        if (selectedWorkout == null || selectedWorkout.isEmpty()) {
            System.out.println("Set goal and workout first.");
            return;
        }

        String today = LocalDate.now().toString();

        if (isWorkoutAlreadyLogged(today)) {
            System.out.println("Already marked as done today.");
            return;
        }

        System.out.print("Did you complete " + selectedWorkout + " today? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            saveWorkoutLog(today);
            System.out.println("Great! Keep it up.");
            logActivity(currentUser, "workout_done");
        } else {
            System.out.println("Try again tomorrow!");
        }
    }

    private boolean isWorkoutAlreadyLogged(String date) {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM workout_log WHERE username = ? AND log_date = ?")) {
            stmt.setString(1, currentUser);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Failed to check workout log: " + e.getMessage());
            return false;
        }
    }

    private void saveWorkoutLog(String date) {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO workout_log (username, log_date, workout) VALUES (?, ?, ?)")) {
            stmt.setString(1, currentUser);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, selectedWorkout);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Failed to save workout log: " + e.getMessage());
        }
    }

    private void saveGoal() {
        try (Connection conn = DBManager.getConnection()) {
            
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM goals WHERE username = ?")) {
                del.setString(1, currentUser);
                del.executeUpdate();
            }
    
            
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO goals (username, goal, workout) VALUES (?, ?, ?)")) {
                stmt.setString(1, currentUser);
                stmt.setString(2, goal);
                stmt.setString(3, selectedWorkout);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Failed to save goal: " + e.getMessage());
        }
    }
    public void generateReports() {
        System.out.println("System-wide report (admin only). Not implemented yet.");
    }
    

    private void loadGoal() {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT goal, workout FROM goals WHERE username = ?")) {
            stmt.setString(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                goal = rs.getString("goal");
                selectedWorkout = rs.getString("workout");
            }
        } catch (Exception e) {
            System.out.println("Failed to load goal: " + e.getMessage());
        }
    }

    private void logActivity(String user, String action) {
        String date = LocalDate.now().toString();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO activity_log (log_date, username, action) VALUES (?, ?, ?)")) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setString(2, user);
            stmt.setString(3, action);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Failed to log activity: " + e.getMessage());
        }
    }
}
