package library.service;

import library.model.Author;
import library.model.Book;
import library.model.Category;
import library.repository.AuthorRepository;
import library.repository.BookRepository;
import library.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой для работы с книгами.
 * Связывает Book с Author и Category через их репозитории.
 */
public class BookService {

    private final BookRepository     bookRepo;
    private final AuthorRepository   authorRepo;
    private final CategoryRepository categoryRepo;

    public BookService(BookRepository bookRepo,
                       AuthorRepository authorRepo,
                       CategoryRepository categoryRepo) {
        this.bookRepo     = bookRepo;
        this.authorRepo   = authorRepo;
        this.categoryRepo = categoryRepo;
    }

    public Book create(String title, int authorId, int categoryId, int year) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Название книги не может быть пустым.");

        Author   author   = authorRepo.findById(authorId).orElse(null);
        Category category = categoryRepo.findById(categoryId).orElse(null);

        Book book = new Book(title.trim(), author, category, year);
        return bookRepo.save(book);
    }

    public Optional<Book> getById(int id) {
        return bookRepo.findById(id);
    }

    public List<Book> getAll() {
        return bookRepo.findAll();
    }

    public List<Book> searchByTitle(String query) {
        return bookRepo.findByTitle(query);
    }

    public List<Book> getAvailable() {
        return bookRepo.findAvailable();
    }

    public boolean update(int id, String title, int authorId, int categoryId, int year, boolean available) {
        Optional<Book> opt = bookRepo.findById(id);
        if (opt.isEmpty()) {
            System.out.println("Книга с id=" + id + " не найдена.");
            return false;
        }
        Book book = opt.get();
        book.setTitle(title.trim());
        book.setAuthor(authorRepo.findById(authorId).orElse(null));
        book.setCategory(categoryRepo.findById(categoryId).orElse(null));
        book.setYear(year);
        book.setAvailable(available);
        return bookRepo.update(book);
    }

    /** Отметить книгу как выданную / возвращённую. */
    public boolean setAvailability(int id, boolean available) {
        Optional<Book> opt = bookRepo.findById(id);
        if (opt.isEmpty()) return false;
        Book book = opt.get();
        book.setAvailable(available);
        return bookRepo.update(book);
    }

    public boolean delete(int id) {
        return bookRepo.deleteById(id);
    }
}
