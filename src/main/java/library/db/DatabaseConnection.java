package library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton для управления подключением к PostgreSQL.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/library_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Подключение к PostgreSQL успешно установлено.");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver не найден: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Соединение закрыто.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}
