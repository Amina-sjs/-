package model;

public class HealthRecord {
    private String date;
    private double weight;
    private int systolic;
    private int diastolic;
    private String exercise;

    public HealthRecord(String date, double weight, int systolic, int diastolic, String exercise) {
        this.date = date;
        this.weight = weight;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.exercise = exercise;
    }

    public String toCSV() {
        return date + "," + weight + "," + systolic + "," + diastolic + "," + exercise;
    }

    public static HealthRecord fromCSV(String line) {
        String[] parts = line.split(",");
        return new HealthRecord(parts[0], Double.parseDouble(parts[1]),
                Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4]);
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Weight: " + weight + "kg, BP: " + systolic + "/" + diastolic + ", Exercise: " + exercise;
    }

    // + геттеры/сеттеры при необходимости
}
