package library.ui;

import library.model.Author;
import library.model.Book;
import library.model.Category;
import library.service.AuthorService;
import library.service.BookService;
import library.service.CategoryService;

import java.util.List;
import java.util.Scanner;

/**
 * Консольный интерфейс библиотечной системы.
 * Отвечает только за ввод/вывод.
 */
public class ConsoleMenu {

    private final Scanner         scanner;
    private final BookService     bookService;
    private final AuthorService   authorService;
    private final CategoryService categoryService;

    public ConsoleMenu(BookService bookService,
                       AuthorService authorService,
                       CategoryService categoryService) {
        this.scanner         = new Scanner(System.in);
        this.bookService     = bookService;
        this.authorService   = authorService;
        this.categoryService = categoryService;
    }

    // ── Главное меню ─────────────────────────────────────────────────────────

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> booksMenu();
                case "2" -> authorsMenu();
                case "3" -> categoriesMenu();
                case "0" -> running = false;
                default  -> System.out.println("  ⚠ Неверный выбор. Попробуйте снова.");
            }
        }
        System.out.println("\n  До свидания!\n");
    }

    // ── Меню: Книги ───────────────────────────────────────────────────────────

    private void booksMenu() {
        boolean back = false;
        while (!back) {
            line();
            System.out.println("  📚 КНИГИ");
            line();
            System.out.println("  1. Показать все книги");
            System.out.println("  2. Поиск по названию");
            System.out.println("  3. Доступные книги");
            System.out.println("  4. Добавить книгу");
            System.out.println("  5. Редактировать книгу");
            System.out.println("  6. Выдать / вернуть книгу");
            System.out.println("  7. Удалить книгу");
            System.out.println("  0. Назад");
            line();
            print("  Выбор: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> listBooks(bookService.getAll());
                case "2" -> searchBooks();
                case "3" -> listBooks(bookService.getAvailable());
                case "4" -> addBook();
                case "5" -> editBook();
                case "6" -> toggleBook();
                case "7" -> deleteBook();
                case "0" -> back = true;
                default  -> System.out.println("  ⚠ Неверный выбор.");
            }
        }
    }

    private void listBooks(List<Book> books) {
        line();
        if (books.isEmpty()) {
            System.out.println("  Книги не найдены.");
        } else {
            books.forEach(b -> System.out.println("  " + b));
        }
        line();
    }

    private void searchBooks() {
        print("  Введите часть названия: ");
        String q = scanner.nextLine().trim();
        listBooks(bookService.searchByTitle(q));
    }

    private void addBook() {
        line();
        System.out.println("  ➕ ДОБАВЛЕНИЕ КНИГИ");

        print("  Название: ");
        String title = scanner.nextLine().trim();

        showAuthors();
        print("  ID автора: ");
        int authorId = readInt();

        showCategories();
        print("  ID категории: ");
        int categoryId = readInt();

        print("  Год издания: ");
        int year = readInt();

        try {
            Book b = bookService.create(title, authorId, categoryId, year);
            System.out.println("  ✅ Книга добавлена: " + b);
        } catch (Exception e) {
            System.out.println("  ❌ Ошибка: " + e.getMessage());
        }
    }

    private void editBook() {
        listBooks(bookService.getAll());
        print("  ID книги для редактирования: ");
        int id = readInt();

        bookService.getById(id).ifPresentOrElse(book -> {
            System.out.println("  Текущие данные: " + book);

            print("  Новое название [" + book.getTitle() + "]: ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) title = book.getTitle();

            showAuthors();
            print("  Новый ID автора [" + (book.getAuthor() != null ? book.getAuthor().getId() : 0) + "]: ");
            String aStr = scanner.nextLine().trim();
            int authorId = aStr.isEmpty() ? (book.getAuthor() != null ? book.getAuthor().getId() : 0)
                                           : Integer.parseInt(aStr);

            showCategories();
            print("  Новый ID категории [" + (book.getCategory() != null ? book.getCategory().getId() : 0) + "]: ");
            String cStr = scanner.nextLine().trim();
            int categoryId = cStr.isEmpty() ? (book.getCategory() != null ? book.getCategory().getId() : 0)
                                             : Integer.parseInt(cStr);

            print("  Новый год [" + book.getYear() + "]: ");
            String yStr = scanner.nextLine().trim();
            int year = yStr.isEmpty() ? book.getYear() : Integer.parseInt(yStr);

            boolean ok = bookService.update(id, title, authorId, categoryId, year, book.isAvailable());
            System.out.println(ok ? "  ✅ Обновлено." : "  ❌ Ошибка обновления.");

        }, () -> System.out.println("  ❌ Книга не найдена."));
    }

    private void toggleBook() {
        listBooks(bookService.getAll());
        print("  ID книги: ");
        int id = readInt();
        bookService.getById(id).ifPresentOrElse(book -> {
            boolean newStatus = !book.isAvailable();
            bookService.setAvailability(id, newStatus);
            System.out.println(newStatus ? "  ✅ Книга отмечена как ВОЗВРАЩЕНА."
                                         : "  ✅ Книга отмечена как ВЫДАНА.");
        }, () -> System.out.println("  ❌ Книга не найдена."));
    }

private void deleteBook() {
    listBooks(bookService.getAll());
    print("  ID книги для удаления: ");
    int id = readInt();
    print("  Подтвердите удаление (1-да, 0-нет): ");
    if (scanner.nextLine().trim().equals("1")) {
        boolean ok = bookService.delete(id);
        System.out.println(ok ? "  ? Удалено." : "  ? Ошибка удаления.");
    } else {
        System.out.println("  Отменено.");
    }
}

    // ── Меню: Авторы ──────────────────────────────────────────────────────────

    private void authorsMenu() {
        boolean back = false;
        while (!back) {
            line();
            System.out.println("  ✍️  АВТОРЫ");
            line();
            System.out.println("  1. Показать всех авторов");
            System.out.println("  2. Добавить автора");
            System.out.println("  3. Редактировать автора");
            System.out.println("  4. Удалить автора");
            System.out.println("  0. Назад");
            line();
            print("  Выбор: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> showAuthors();
                case "2" -> addAuthor();
                case "3" -> editAuthor();
                case "4" -> deleteAuthor();
                case "0" -> back = true;
                default  -> System.out.println("  ⚠ Неверный выбор.");
            }
        }
    }

    private void showAuthors() {
        line();
        List<Author> authors = authorService.getAll();
        if (authors.isEmpty()) System.out.println("  Авторы не найдены.");
        else authors.forEach(a -> System.out.println("  " + a));
        line();
    }

    private void addAuthor() {
        line();
        print("  Имя: ");        String fn = scanner.nextLine().trim();
        print("  Фамилия: ");    String ln = scanner.nextLine().trim();
        print("  Год рождения: "); int year = readInt();
        try {
            Author a = authorService.create(fn, ln, year);
            System.out.println("  ✅ Автор добавлен: " + a);
        } catch (Exception e) {
            System.out.println("  ❌ " + e.getMessage());
        }
    }

    private void editAuthor() {
        showAuthors();
        print("  ID автора: "); int id = readInt();
        authorService.getById(id).ifPresentOrElse(a -> {
            print("  Новое имя [" + a.getFirstName() + "]: ");
            String fn = scanner.nextLine().trim();
            if (fn.isEmpty()) fn = a.getFirstName();

            print("  Новая фамилия [" + a.getLastName() + "]: ");
            String ln = scanner.nextLine().trim();
            if (ln.isEmpty()) ln = a.getLastName();

            print("  Новый год [" + a.getBirthYear() + "]: ");
            String ys = scanner.nextLine().trim();
            int yr = ys.isEmpty() ? a.getBirthYear() : Integer.parseInt(ys);

            System.out.println(authorService.update(id, fn, ln, yr) ? "  ✅ Обновлено." : "  ❌ Ошибка.");
        }, () -> System.out.println("  ❌ Автор не найден."));
    }

    private void deleteAuthor() {
        showAuthors();
        print("  ID автора для удаления: "); int id = readInt();
        System.out.println(authorService.delete(id) ? "  ✅ Удалено." : "  ❌ Ошибка.");
    }

    // ── Меню: Категории ───────────────────────────────────────────────────────

    private void categoriesMenu() {
        boolean back = false;
        while (!back) {
            line();
            System.out.println("  🏷️  КАТЕГОРИИ");
            line();
            System.out.println("  1. Показать все категории");
            System.out.println("  2. Добавить категорию");
            System.out.println("  3. Редактировать категорию");
            System.out.println("  4. Удалить категорию");
            System.out.println("  0. Назад");
            line();
            print("  Выбор: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> showCategories();
                case "2" -> addCategory();
                case "3" -> editCategory();
                case "4" -> deleteCategory();
                case "0" -> back = true;
                default  -> System.out.println("  ⚠ Неверный выбор.");
            }
        }
    }

    private void showCategories() {
        line();
        List<Category> cats = categoryService.getAll();
        if (cats.isEmpty()) System.out.println("  Категории не найдены.");
        else cats.forEach(c -> System.out.println("  " + c));
        line();
    }

    private void addCategory() {
        print("  Название категории: ");
        String name = scanner.nextLine().trim();
        try {
            Category c = categoryService.create(name);
            System.out.println("  ✅ Категория добавлена: " + c);
        } catch (Exception e) {
            System.out.println("  ❌ " + e.getMessage());
        }
    }

    private void editCategory() {
        showCategories();
        print("  ID категории: "); int id = readInt();
        print("  Новое название: "); String name = scanner.nextLine().trim();
        System.out.println(categoryService.update(id, name) ? "  ✅ Обновлено." : "  ❌ Ошибка.");
    }

    private void deleteCategory() {
        showCategories();
        print("  ID категории для удаления: "); int id = readInt();
        System.out.println(categoryService.delete(id) ? "  ✅ Удалено." : "  ❌ Ошибка.");
    }

    // ── Вспомогательные методы ────────────────────────────────────────────────

    private void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║       БИБЛИОТЕЧНАЯ СИСТЕМА  v1.0     ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println();
    }

    private void printMainMenu() {
        line();
        System.out.println("  ГЛАВНОЕ МЕНЮ");
        line();
        System.out.println("  1. Книги");
        System.out.println("  2. Авторы");
        System.out.println("  3. Категории");
        System.out.println("  0. Выход");
        line();
        print("  Выбор: ");
    }

    private void line() {
        System.out.println("  ──────────────────────────────────────");
    }

    private void print(String msg) {
        System.out.print(msg);
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ⚠ Ожидается число. Принято 0.");
            return 0;
        }
    }
}
