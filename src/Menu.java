import java.util.Scanner;

public class Menu {
    private TrackerService service = new TrackerService();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("""
                \n📋 Меню:
                1. Добавить запись
                2. Показать все записи
                3. Обновить запись
                4. Удалить запись
                5. Сгенерировать отчет
                6. Выход
                Выберите опцию:
            """);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> addEntry();
                case "2" -> service.showAll();
                case "3" -> {
                    System.out.print("Введите дату записи: ");
                    service.update(scanner.nextLine(), scanner);
                }
                case "4" -> {
                    System.out.print("Введите дату для удаления: ");
                    service.delete(scanner.nextLine());
                }
                case "5" -> ReportGenerator.generateReport(service.getEntries());
                case "6" -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный ввод.");
            }
        }
    }

    private void addEntry() {
        System.out.print("Дата (например, 2025-04-17): ");
        String date = scanner.nextLine();
        System.out.print("Вес (в кг): ");
        double weight = scanner.nextDouble(); scanner.nextLine();
        System.out.print("Давление (например, 120/80): ");
        String bp = scanner.nextLine();
        System.out.print("Упражнение: ");
        String exercise = scanner.nextLine();
        service.addEntry(date, weight, bp, exercise);
    }
}
