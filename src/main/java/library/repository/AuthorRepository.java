package library.repository;

import library.db.DatabaseConnection;
import library.model.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория для сущности Author.
 * Все SQL-операции выполняются через PreparedStatement.
 */
public class AuthorRepository implements Repository<Author, Integer> {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Override
    public Author save(Author author) {
        String sql = "INSERT INTO authors (first_name, last_name, birth_year) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.setInt   (3, author.getBirthYear());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                author.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("[AuthorRepo] Ошибка сохранения: " + e.getMessage());
        }
        return author;
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    @Override
    public Optional<Author> findById(Integer id) {
        String sql = "SELECT * FROM authors WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AuthorRepo] Ошибка поиска: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Author> findAll() {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT * FROM authors ORDER BY last_name, first_name";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AuthorRepo] Ошибка выборки: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Override
    public boolean update(Author author) {
        String sql = "UPDATE authors SET first_name = ?, last_name = ?, birth_year = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.setInt   (3, author.getBirthYear());
            ps.setInt   (4, author.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AuthorRepo] Ошибка обновления: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM authors WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AuthorRepo] Ошибка удаления: " + e.getMessage());
            return false;
        }
    }

    // ── Маппинг строки ResultSet → объект ───────────────────────────────────

    private Author mapRow(ResultSet rs) throws SQLException {
        return new Author(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getInt("birth_year")
        );
    }
}
