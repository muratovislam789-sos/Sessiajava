package library.repository;

import library.db.DatabaseConnection;
import library.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория для сущности Category.
 */
public class CategoryRepository implements Repository<Category, Integer> {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO categories (name) VALUES (?) RETURNING id";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                category.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("[CategoryRepo] Ошибка сохранения: " + e.getMessage());
        }
        return category;
    }

    @Override
    public Optional<Category> findById(Integer id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CategoryRepo] Ошибка поиска: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CategoryRepo] Ошибка выборки: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setInt   (2, category.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryRepo] Ошибка обновления: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryRepo] Ошибка удаления: " + e.getMessage());
            return false;
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("id"), rs.getString("name"));
    }
}
