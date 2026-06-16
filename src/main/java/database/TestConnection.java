package database;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {
        try {
            System.out.println("DB MODE: " + DatabaseConnection.getConfigMode());
            System.out.println("DB USER: " + DatabaseConnection.getUser());
            try (Connection connection = DatabaseConnection.getConnection()) {
                System.out.println("Ket noi database thanh cong!");
                System.out.println("Database hien tai: " + connection.getCatalog());
            }
        } catch (Throwable exception) {
            System.out.println("Ket noi database that bai!");
            exception.printStackTrace();
        }
    }
}
