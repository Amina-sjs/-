import java.util.*;

public class TrackerService {
    private List<Entry> entries;

    public TrackerService() {
        entries = FileHandler.load();
    }

    public void addEntry(String date, double weight, String bp, String exercise) {
        entries.add(new Entry(date, weight, bp, exercise));
        FileHandler.save(entries);
        System.out.println("✅ Запись добавлена.");
    }

    public void showAll() {
        if (entries.isEmpty()) {
            System.out.println("Нет записей.");
            return;
        }
        entries.forEach(System.out::println);
    }

    public void update(String date, Scanner scanner) {
        for (Entry e : entries) {
            if (e.getDate().equals(date)) {
                System.out.print("Новый вес: ");
                e.setWeight(scanner.nextDouble());
                scanner.nextLine();
                System.out.print("Новое давление: ");
                e.setBloodPressure(scanner.nextLine());
                System.out.print("Новое упражнение: ");
                e.setExercise(scanner.nextLine());
                FileHandler.save(entries);
                System.out.println("✅ Запись обновлена.");
                return;
            }
        }
        System.out.println("Запись не найдена.");
    }

    public void delete(String date) {
        entries.removeIf(e -> e.getDate().equals(date));
        FileHandler.save(entries);
        System.out.println("✅ Запись удалена (если существовала).");
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
