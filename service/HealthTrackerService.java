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
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter weight (kg): ");
        double weight = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter systolic BP: ");
        int sys = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter diastolic BP: ");
        int dia = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter exercise details: ");
        String exercise = scanner.nextLine();

        if (!Validator.isValidDate(date) || weight <= 0 || sys <= 0 || dia <= 0) {
            System.out.println("Invalid input.");
            return;
        }

        HealthRecord record = new HealthRecord(date, weight, sys, dia, exercise);
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

    public void exportData() {
        csvHandler.exportCSV("data/export.csv", records);
    }
}
