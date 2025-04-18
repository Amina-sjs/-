package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/health_tracker";
    private static final String USER = "postgres"; // или твой логин
    private static final String PASSWORD = "admin123"; // замени на свой пароль

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
