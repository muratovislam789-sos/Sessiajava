package library.model;

/**
 * Модель книги.
 * Связана с Author и Category (отношения ManyToOne).
 */
public class Book {

    private int      id;
    private String   title;
    private Author   author;
    private Category category;
    private int      year;
    private boolean  available;

    public Book() {}

    public Book(int id, String title, Author author, Category category, int year, boolean available) {
        this.id        = id;
        this.title     = title;
        this.author    = author;
        this.category  = category;
        this.year      = year;
        this.available = available;
    }

    public Book(String title, Author author, Category category, int year) {
        this(0, title, author, category, year, true);
    }

    // ── Геттеры и сеттеры ───────────────────────────────────────────────────

    public int      getId()                  { return id; }
    public void     setId(int id)            { this.id = id; }

    public String   getTitle()               { return title; }
    public void     setTitle(String v)       { this.title = v; }

    public Author   getAuthor()              { return author; }
    public void     setAuthor(Author v)      { this.author = v; }

    public Category getCategory()            { return category; }
    public void     setCategory(Category v)  { this.category = v; }

    public int      getYear()                { return year; }
    public void     setYear(int v)           { this.year = v; }

    public boolean  isAvailable()            { return available; }
    public void     setAvailable(boolean v)  { this.available = v; }

    @Override
    public String toString() {
        String authorName   = author   != null ? author.getFullName()   : "—";
        String categoryName = category != null ? category.getName()     : "—";
        String status       = available ? "✓ есть" : "✗ выдана";
        return String.format("[%d] \"%s\" | %s | %s | %d г. | %s",
                id, title, authorName, categoryName, year, status);
    }
}
