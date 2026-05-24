package library.model;

/**
 * Модель автора книги.
 * Инкапсулирует данные: id, имя, фамилия, год рождения.
 */
public class Author {

    private int    id;
    private String firstName;
    private String lastName;
    private int    birthYear;

    public Author() {}

    public Author(int id, String firstName, String lastName, int birthYear) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.birthYear = birthYear;
    }

    public Author(String firstName, String lastName, int birthYear) {
        this(0, firstName, lastName, birthYear);
    }

    // ── Геттеры и сеттеры ───────────────────────────────────────────────────

    public int    getId()        { return id; }
    public void   setId(int id)  { this.id = id; }

    public String getFirstName()              { return firstName; }
    public void   setFirstName(String v)      { this.firstName = v; }

    public String getLastName()               { return lastName; }
    public void   setLastName(String v)       { this.lastName = v; }

    public int    getBirthYear()              { return birthYear; }
    public void   setBirthYear(int v)         { this.birthYear = v; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s %s (г.р. %d)", id, firstName, lastName, birthYear);
    }
}
