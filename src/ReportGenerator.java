import java.util.*;

public class ReportGenerator {
    public static void generateReport(List<Entry> entries) {
        if (entries.isEmpty()) {
            System.out.println("Нет данных для отчета.");
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

        System.out.println("📊 Отчет:");
        System.out.println("Средний вес: " + totalWeight / entries.size() + " кг");
        System.out.println("Всего упражнений: " + exerciseCount);
        System.out.println("Самое частое упражнение: " + frequentExercise);
    }
}
