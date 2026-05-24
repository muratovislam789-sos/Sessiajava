package library;

import library.db.DatabaseConnection;
import library.db.SchemaInitializer;
import library.repository.AuthorRepository;
import library.repository.BookRepository;
import library.repository.CategoryRepository;
import library.service.AuthorService;
import library.service.BookService;
import library.service.CategoryService;
import library.ui.ConsoleMenu;

import java.sql.SQLException;

/**
 * Точка входа в приложение.
 */
public class Main {

    public static void main(String[] args) {

        // 1. Инициализация БД
        SchemaInitializer.initialize();

        // 2. Репозитории
        AuthorRepository   authorRepo   = new AuthorRepository();
        CategoryRepository categoryRepo = new CategoryRepository();
        BookRepository     bookRepo     = new BookRepository();

        // 3. Сервисы
        AuthorService   authorService   = new AuthorService(authorRepo);
        CategoryService categoryService = new CategoryService(categoryRepo);
        BookService     bookService     = new BookService(bookRepo, authorRepo, categoryRepo);

        // 4. Консольный интерфейс
        ConsoleMenu menu = new ConsoleMenu(bookService, authorService, categoryService);
        menu.start();

        // 5. Закрытие соединения
        try {
            DatabaseConnection.getInstance().close();
        } catch (SQLException e) {
            System.err.println("[DB] Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}
