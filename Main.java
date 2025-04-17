import service.HealthTrackerService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HealthTrackerService service = new HealthTrackerService();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Health Parameter Tracker ===");
            System.out.println("1. Add Record");
            System.out.println("2. View Records");
            System.out.println("3. Update Record");
            System.out.println("4. Delete Record");
            System.out.println("5. Export Data");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            System.out.println("7. Show Statistics");


            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> service.addRecord(scanner);
                case 2 -> service.viewRecords();
                case 3 -> service.updateRecord(scanner);
                case 4 -> service.deleteRecord(scanner);
                case 5 -> service.exportData();
                case 6 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 6);
    }
}
