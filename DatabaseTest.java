import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/health_tracker";
        String user = "postgres";
        String password = "admin123"; // Замени, если у тебя другой

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Успешное подключение к базе данных!");
        } catch (SQLException e) {
            System.out.println("❌ Ошибка подключения: " + e.getMessage());
        }
    }
}
