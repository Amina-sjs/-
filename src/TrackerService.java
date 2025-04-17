import java.util.*;

public class TrackerService {
    private List<Entry> entries;

    public TrackerService() {
        entries = FileHandler.load();
    }

    public void addEntry(String date, double weight, String bp, String exercise) {
        entries.add(new Entry(date, weight, bp, exercise));
        FileHandler.save(entries);
        System.out.println("✅ Entry added.");
    }

    public void showAll() {
        if (entries.isEmpty()) {
            System.out.println("No entries available.");
            return;
        }
        entries.forEach(System.out::println);
    }

    public void update(String date, Scanner scanner) {
        for (Entry e : entries) {
            if (e.getDate().equals(date)) {
                System.out.print("New weight: ");
                e.setWeight(scanner.nextDouble());
                scanner.nextLine();
                System.out.print("New blood pressure: ");
                e.setBloodPressure(scanner.nextLine());
                System.out.print("New exercise: ");
                e.setExercise(scanner.nextLine());
                FileHandler.save(entries);
                System.out.println("✅ Entry updated.");
                return;
            }
        }
        System.out.println("Entry not found.");
    }

    public void delete(String date) {
        entries.removeIf(e -> e.getDate().equals(date));
        FileHandler.save(entries);
        System.out.println("✅ Entry deleted (if it existed).");
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
