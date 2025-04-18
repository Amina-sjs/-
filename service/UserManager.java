package service;

import util.ActivityLogger;
import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    public boolean userExists(String name) {
        String sql = "SELECT 1 FROM users WHERE name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Ошибка при проверке существования пользователя: " + e.getMessage());
            return false;
        }
    }

    public boolean authenticate(String name, String password) {
        String sql = "SELECT * FROM users WHERE name = ? AND password = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Ошибка при аутентификации: " + e.getMessage());
            return false;
        }
    }

    public boolean register(String name, String password) {
        if (userExists(name)) {
            System.out.println("Пользователь уже существует.");
            return false;
        }

        String sql = "INSERT INTO users (name, password, is_admin) VALUES (?, ?, false)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.executeUpdate();

            ActivityLogger.log(name, "register", "new user registered");
            return true;

        } catch (SQLException e) {
            System.out.println("Ошибка при регистрации: " + e.getMessage());
            return false;
        }
    }

    public boolean isAdmin(String name) {
        String sql = "SELECT is_admin FROM users WHERE name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_admin");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при проверке администратора: " + e.getMessage());
        }

        return false;
    }
}
