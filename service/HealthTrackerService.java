package service;

import model.HealthRecord;
import util.Validator;

import java.util.*;

public class HealthTrackerService {
    private final CSVHandler csvHandler = new CSVHandler();
    private final List<HealthRecord> records;

    public HealthTrackerService() {
        this.records = csvHandler.loadRecords();
    }

    public void addRecord(Scanner scanner) {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
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
    
        HealthRecord record = new HealthRecord(name, date, weight, sys, dia, exercise);
        records.add(record);
        csvHandler.saveRecords(records);
        System.out.println("Record added!");
    }
    
    

    public void viewRecords() {
        if (records.isEmpty()) {
            System.out.println("No records found.");
        } else {
            records.forEach(System.out::println);
        }
    }

    public void updateRecord(Scanner scanner) {
        viewRecords();
        System.out.print("Enter record number to update: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= records.size()) {
            System.out.println("Invalid record.");
            return;
        }
        addRecord(scanner); // Переиспользуем ввод
        records.remove(index); // Удалим старый
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
        if (records.isEmpty()) {
            System.out.println("No records to analyze.");
            return;
        }
    
        double totalWeight = 0;
        int totalSys = 0;
        int totalDia = 0;
    
    
        double avgWeight = totalWeight / records.size();
        int avgSys = totalSys / records.size();
        int avgDia = totalDia / records.size();
    
        System.out.printf("Average Weight: %.2f kg\n", avgWeight);
        System.out.printf("Average Blood Pressure: %d/%d\n", avgSys, avgDia);
    }
    
    

    public void exportData() {
        csvHandler.exportCSV("data/export.csv", records);
    }
}
