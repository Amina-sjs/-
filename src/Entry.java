import java.io.Serializable;

public class Entry implements Serializable {
    private String date;
    private double weight;
    private String bloodPressure;
    private String exercise;


    public Entry(String date, double weight, String bloodPressure, String exercise) {
     this.date =date;
     this.weight = weight;
     this.bloodPressure = bloodPressure;
     this.exercise = exercise;
    }  
    
    public String getDate() { return date; }
    public double getWeight() {return weight; }
    public String getbloodPressure() { return bloodPressure; }
    public String getExercise() { return exercise; }

    public void setWeight(double weight) {this.weight = weight;}
    public void setBloodPressure(String bp) {this.bloodPressure = bp; }
    public void setExercise(String ex) { this.exercise = ex;}

    @Override
    public String toString() {
        return "Date: " + date + ", Вес: " + weight + " кг, Давление: " + bloodPressure + ", Упражнение: " + exercise;
    }

}
