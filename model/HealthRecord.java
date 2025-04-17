package model;

public class HealthRecord {
    private String name;
    private String date;
    private double weight;
    private int systolic;
    private int diastolic;
    private String exercise;
   


    public HealthRecord(String name,String date, double weight, int systolic, int diastolic, String exercise) {
        this.name = name;
        this.date = date;
        this.weight = weight;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.exercise = exercise;
    }
    public String toCSV() {
        return String.join(",", name, date, String.valueOf(weight),
                String.valueOf(systolic), String.valueOf(diastolic), exercise);
    }
    
    
    public static HealthRecord fromCSV(String line) {
        String[] parts = line.split(",");
        return new HealthRecord(
            parts[0],                      // name
            parts[1],                      // date
            Double.parseDouble(parts[2]), // weight
            Integer.parseInt(parts[3]),   // systolic
            Integer.parseInt(parts[4]),   // diastolic
            parts[5]                       // exercise
        );
    }
    

    @Override
    public String toString() {
        return "Name: " + name + ", Date: " + date + ", Weight: " + weight + "kg, BP: " + systolic + "/" + diastolic + ", Exercise: " + exercise;
    }

    public String getName() {
        return name;
    }
    
    public String getDate() {
        return date;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public int getSystolic() {
        return systolic;
    }
    
    public int getDiastolic() {
        return diastolic;
    }
    
    public String getExercise() {
        return exercise;
    }
    
}
