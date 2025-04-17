import java.util.Locale;
import java.util.Scanner;

public class Menu {
    private TrackerService service = new TrackerService();
    private Scanner scanner = new Scanner(System.in).useLocale(Locale.ENGLISH);

    public void start() {
        
        while (true) {
            System.out.println("""
                \n📋 Menu:
                1. Add entry
                2. Show all entries
                3. Update entry
                4. Delete entry
                5. Generate report
                6. Exit
                Choose an option:""");

            

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> addEntry();
                case "2" -> service.showAll();
                case "3" -> {
                    System.out.print("Enter the date of the entry: ");
                    service.update(scanner.nextLine(), scanner);
                }
                case "4" -> {
                    System.out.print("Enter the date to delete: ");
                    service.delete(scanner.nextLine());
                }
                case "5" -> ReportGenerator.generateReport(service.getEntries());
                case "6" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private void addEntry() {
        System.out.print("Date (e.g., 2025-04-17): ");
        String date = scanner.nextLine();
        System.out.print("Weight (kg): ");
        double weight = scanner.nextDouble(); scanner.nextLine();
        System.out.print("Blood pressure (e.g., 120/80): ");
        String bp = scanner.nextLine();
        System.out.print("Exercise: ");
        String exercise = scanner.nextLine();
        service.addEntry(date, weight, bp, exercise);
    }
}