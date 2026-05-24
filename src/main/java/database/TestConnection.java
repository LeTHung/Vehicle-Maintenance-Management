package database;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Kết nối database thành công!");
            System.out.println("Database hiện tại: " + connection.getCatalog());
        } catch (Exception e) {
            System.out.println("Kết nối database thất bại!");
            e.printStackTrace();
        }
    }
}