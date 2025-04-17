package service;

import model.HealthRecord;

import java.io.*;
import java.util.*;

public class CSVHandler {
    private final String filename = "data/health_data.csv";

    public List<HealthRecord> loadRecords() {
        List<HealthRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(HealthRecord.fromCSV(line));
            }
        } catch (IOException e) {
            System.out.println("No data file found. Starting fresh.");
        }
        return records;
    }

    public void saveRecords(List<HealthRecord> records) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (HealthRecord r : records) {
                bw.write(r.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving records.");
        }
    }

    public void exportCSV(String exportFilename, List<HealthRecord> records) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(exportFilename))) {
            for (HealthRecord r : records) {
                bw.write(r.toCSV());
                bw.newLine();
            }
            System.out.println("Export successful: " + exportFilename);
        } catch (IOException e) {
            System.out.println("Export failed.");
        }
    }
}
