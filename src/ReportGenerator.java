import java.util.*;

public class ReportGenerator {
    public static void generateReport(List<Entry> entries) {
        if (entries.isEmpty()) {
            System.out.println("No data available for the report.");
            return;
        }

        double totalWeight = 0;
        int exerciseCount = 0;
        Map<String, Integer> exerciseStats = new HashMap<>();

        for (Entry e : entries) {
            totalWeight += e.getWeight();
            if (!e.getExercise().isEmpty()) {
                exerciseCount++;
                exerciseStats.put(e.getExercise(), exerciseStats.getOrDefault(e.getExercise(), 0) + 1);
            }
        }

        String frequentExercise = Collections.max(exerciseStats.entrySet(), Map.Entry.comparingByValue()).getKey();

        System.out.println("📊 Report:");
        System.out.println("Average weight: " + totalWeight / entries.size() + " kg");
        System.out.println("Total exercises: " + exerciseCount);
        System.out.println("Most frequent exercise: " + frequentExercise);
    }
}
