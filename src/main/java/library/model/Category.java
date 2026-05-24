package library.model;

/**
 * Модель категории (жанра) книги.
 */
public class Category {

    private int    id;
    private String name;

    public Category() {}

    public Category(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    public Category(String name) {
        this(0, name);
    }

    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getName()        { return name; }
    public void   setName(String v){ this.name = v; }

    @Override
    public String toString() {
        return String.format("[%d] %s", id, name);
    }
}
