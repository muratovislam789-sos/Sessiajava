package library.repository;

import library.db.DatabaseConnection;
import library.model.Author;
import library.model.Book;
import library.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория для сущности Book.
 * Использует JOIN для получения связанных Author и Category.
 */
public class BookRepository implements Repository<Book, Integer> {

    // SQL с JOIN для полных данных
    private static final String SELECT_FULL = """
            SELECT b.id, b.title, b.year, b.available,
                   a.id AS a_id, a.first_name, a.last_name, a.birth_year,
                   c.id AS c_id, c.name AS c_name
            FROM books b
            LEFT JOIN authors    a ON b.author_id   = a.id
            LEFT JOIN categories c ON b.category_id = c.id
            """;

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Override
    public Book save(Book book) {
        String sql = """
                INSERT INTO books (title, author_id, category_id, year, available)
                VALUES (?, ?, ?, ?, ?) RETURNING id
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString (1, book.getTitle());
            setNullableInt(ps, 2, book.getAuthor()   != null ? book.getAuthor().getId()   : null);
            setNullableInt(ps, 3, book.getCategory() != null ? book.getCategory().getId() : null);
            ps.setInt    (4, book.getYear());
            ps.setBoolean(5, book.isAvailable());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) book.setId(rs.getInt("id"));
        } catch (SQLException e) {
            System.err.println("[BookRepo] Ошибка сохранения: " + e.getMessage());
        }
        return book;
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    @Override
    public Optional<Book> findById(Integer id) {
        String sql = SELECT_FULL + " WHERE b.id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookRepo] Ошибка поиска: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_FULL + " ORDER BY b.title";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookRepo] Ошибка выборки: " + e.getMessage());
        }
        return list;
    }

    /** Поиск книг по части названия (ILIKE — регистронезависимо). */
    public List<Book> findByTitle(String titlePart) {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE b.title ILIKE ? ORDER BY b.title";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, "%" + titlePart + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookRepo] Ошибка поиска по названию: " + e.getMessage());
        }
        return list;
    }

    /** Все доступные (не выданные) книги. */
    public List<Book> findAvailable() {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE b.available = TRUE ORDER BY b.title";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookRepo] Ошибка поиска: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Override
    public boolean update(Book book) {
        String sql = """
                UPDATE books
                SET title = ?, author_id = ?, category_id = ?, year = ?, available = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString (1, book.getTitle());
            setNullableInt(ps, 2, book.getAuthor()   != null ? book.getAuthor().getId()   : null);
            setNullableInt(ps, 3, book.getCategory() != null ? book.getCategory().getId() : null);
            ps.setInt    (4, book.getYear());
            ps.setBoolean(5, book.isAvailable());
            ps.setInt    (6, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookRepo] Ошибка обновления: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookRepo] Ошибка удаления: " + e.getMessage());
            return false;
        }
    }

    // ── Вспомогательные методы ───────────────────────────────────────────────

    private Book mapRow(ResultSet rs) throws SQLException {
        Author author = null;
        int aId = rs.getInt("a_id");
        if (!rs.wasNull()) {
            author = new Author(aId,
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("birth_year"));
        }

        Category category = null;
        int cId = rs.getInt("c_id");
        if (!rs.wasNull()) {
            category = new Category(cId, rs.getString("c_name"));
        }

        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                author,
                category,
                rs.getInt("year"),
                rs.getBoolean("available")
        );
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) ps.setNull(index, Types.INTEGER);
        else               ps.setInt(index, value);
    }
}
