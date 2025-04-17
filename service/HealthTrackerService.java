package service;

import model.HealthRecord;
import util.Validator;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class HealthTrackerService {
    private final CSVHandler csvHandler = new CSVHandler();
    private final List<HealthRecord> records;
    private final String currentUser;
    private String goal;
    private String selectedWorkout;
    private final Map<String, Boolean> dailyWorkoutLog = new HashMap<>();
    private final String GOAL_FILE = "data/goals.csv";

    public HealthTrackerService(String currentUser) {
        this.currentUser = currentUser;
        this.records = csvHandler.loadRecords();
        loadGoal(); 
    }

    public void run(Scanner scanner) {
        loadGoal(); 
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
                case "1": addRecord(scanner); break;
                case "2": viewRecords(); break;
                case "3": updateRecord(scanner); break;
                case "4": deleteRecord(scanner); break;
                case "5": exportData(); break;
                case "6": setGoalAndSuggestWorkouts(scanner); break;
                case "7": markWorkoutDone(scanner); break;
                case "8": showStatistics(); break;
                case "9":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void addRecord(Scanner scanner) {
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
        int sys, dia;
        try {
            sys = Integer.parseInt(bpParts[0]);
            dia = Integer.parseInt(bpParts[1]);
        } catch (NumberFormatException e) {
            System.out.println("Blood pressure must be numbers like 120/80.");
            return;
        }
        System.out.print("Enter exercise details: ");
        String exercise = scanner.nextLine();
        if (!Validator.isValidDate(date) || weight <= 0) {
            System.out.println("Invalid input.");
            return;
        }
        HealthRecord record = new HealthRecord(currentUser, date, weight, sys, dia, exercise);
        records.add(record);
        csvHandler.saveRecords(records);
        System.out.println("Record added!");
    }

    public void viewRecords() {
        boolean isAdmin = currentUser.equals("admin");
        List<HealthRecord> visibleRecords = new ArrayList<>();
        for (HealthRecord r : records) {
            if (isAdmin || r.getName().equals(currentUser)) {
                visibleRecords.add(r);
            }
        }
        if (visibleRecords.isEmpty()) {
            System.out.println("No records found.");
        } else {
            visibleRecords.forEach(System.out::println);
        }
    }

    public void updateRecord(Scanner scanner) {
        boolean isAdmin = currentUser.equals("admin");
        List<HealthRecord> visibleRecords = new ArrayList<>();
        for (HealthRecord r : records) {
            if (isAdmin || r.getName().equals(currentUser)) {
                visibleRecords.add(r);
            }
        }
        if (visibleRecords.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        for (int i = 0; i < visibleRecords.size(); i++) {
            System.out.println((i + 1) + ". " + visibleRecords.get(i));
        }
        System.out.print("Enter record number to update: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= visibleRecords.size()) {
            System.out.println("Invalid record.");
            return;
        }
        HealthRecord toUpdate = visibleRecords.get(index);
        records.remove(toUpdate);
        addRecord(scanner);
        csvHandler.saveRecords(records);
        System.out.println("Record updated.");
    }

    public void deleteRecord(Scanner scanner) {
        System.out.print("Enter admin password to delete a record: ");
        String password = scanner.nextLine();
        if (!password.equals("admin123")) {
            System.out.println("Access denied. Invalid password.");
            return;
        }
        viewRecords();
        System.out.print("Enter record number to delete: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= records.size()) {
            System.out.println("Invalid record.");
            return;
        }
        records.remove(index);
        csvHandler.saveRecords(records);
        System.out.println("Record deleted.");
    }

    public void showStatistics() {
        List<HealthRecord> userRecords = new ArrayList<>();
        for (HealthRecord r : records) {
            if (r.getName().equals(currentUser)) {
                userRecords.add(r);
            }
        }

        if (userRecords.isEmpty()) {
            System.out.println("No records to analyze.");
            return;
        }

        double totalWeight = 0;
        int totalSys = 0;
        int totalDia = 0;

        for (HealthRecord r : userRecords) {
            totalWeight += r.getWeight();
            totalSys += r.getSystolic();
            totalDia += r.getDiastolic();
        }

        double avgWeight = totalWeight / userRecords.size();
        int avgSys = totalSys / userRecords.size();
        int avgDia = totalDia / userRecords.size();

        System.out.printf("Average Weight: %.2f kg\n", avgWeight);
        System.out.printf("Average Blood Pressure: %d/%d\n", avgSys, avgDia);
        System.out.println("Your goal: " + (goal != null ? goal : "Not set"));
        System.out.println("Your selected workout: " + (selectedWorkout != null ? selectedWorkout : "Not chosen"));
    }

    public void exportData() {
        List<HealthRecord> userRecords = new ArrayList<>();
        for (HealthRecord r : records) {
            if (r.getName().equals(currentUser)) {
                userRecords.add(r);
            }
        }
        csvHandler.exportCSV("data/export_" + currentUser + ".csv", userRecords);
    }

    public void setGoalAndSuggestWorkouts(Scanner scanner) {
        System.out.print("Enter your goal (e.g., lose weight, gain muscle): ");
        goal = scanner.nextLine();
        System.out.println("Suggested workouts:");
        if (goal.equalsIgnoreCase("lose weight")) {
            System.out.println("- Cardio\n- Running\n- Jump Rope");
        } else if (goal.equalsIgnoreCase("gain muscle")) {
            System.out.println("- Weight Lifting\n- Resistance Bands\n- Push-Ups");
        } else {
            System.out.println("- Walking\n- Stretching");
        }
        System.out.print("Choose one workout from the list: ");
        selectedWorkout = scanner.nextLine();
        System.out.println("You chose: " + selectedWorkout + ". Good luck!");
        saveGoal();
    }

    public void markWorkoutDone(Scanner scanner) {
        if (selectedWorkout == null || selectedWorkout.isEmpty()) {
            System.out.println("You haven't set a goal or chosen a workout yet.");
            return;
        }

        String today = LocalDate.now().toString();
        String logKey = currentUser + "_" + today;

        if (dailyWorkoutLog.getOrDefault(logKey, false)) {
            System.out.println("You've already marked today's workout as done ");
            return;
        }

        System.out.print("Did you complete your workout today (" + selectedWorkout + ")? (yes/no): ");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("yes")) {
            dailyWorkoutLog.put(logKey, true);
            System.out.println("Great job! Workout marked as done for today.");
        } else {
            System.out.println("No worries, try again tomorrow!");
        }
    }

    private void saveGoal() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GOAL_FILE, true))) {
            writer.println(currentUser + "," + goal + "," + selectedWorkout);
        } catch (IOException e) {
            System.out.println("Failed to save goal.");
        }
    }

    private void loadGoal() {
        try (BufferedReader reader = new BufferedReader(new FileReader(GOAL_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(currentUser)) {
                    goal = parts[1];
                    selectedWorkout = parts[2];
                }
            }
        } catch (IOException ignored) {
        }
    }
}
