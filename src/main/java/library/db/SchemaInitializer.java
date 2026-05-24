package library.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Создаёт таблицы при первом запуске приложения.
 */
public class SchemaInitializer {

    public static void initialize() {
        String createAuthors = """
                CREATE TABLE IF NOT EXISTS authors (
                    id         SERIAL PRIMARY KEY,
                    first_name VARCHAR(100) NOT NULL,
                    last_name  VARCHAR(100) NOT NULL,
                    birth_year INT
                );
                """;

        String createCategories = """
                CREATE TABLE IF NOT EXISTS categories (
                    id   SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL UNIQUE
                );
                """;

        String createBooks = """
                CREATE TABLE IF NOT EXISTS books (
                    id          SERIAL PRIMARY KEY,
                    title       VARCHAR(255) NOT NULL,
                    author_id   INT REFERENCES authors(id) ON DELETE SET NULL,
                    category_id INT REFERENCES categories(id) ON DELETE SET NULL,
                    year        INT,
                    available   BOOLEAN DEFAULT TRUE
                );
                """;

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createAuthors);
                stmt.execute(createCategories);
                stmt.execute(createBooks);
                System.out.println("[DB] Схема базы данных инициализирована.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Ошибка инициализации схемы: " + e.getMessage());
        }
    }
}
