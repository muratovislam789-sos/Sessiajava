package library.service;

import library.model.Category;
import library.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой для работы с категориями книг.
 */
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public Category create(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Название категории не может быть пустым.");
        return repo.save(new Category(name.trim()));
    }

    public Optional<Category> getById(int id) {
        return repo.findById(id);
    }

    public List<Category> getAll() {
        return repo.findAll();
    }

    public boolean update(int id, String newName) {
        Optional<Category> opt = repo.findById(id);
        if (opt.isEmpty()) {
            System.out.println("Категория с id=" + id + " не найдена.");
            return false;
        }
        Category cat = opt.get();
        cat.setName(newName.trim());
        return repo.update(cat);
    }

    public boolean delete(int id) {
        return repo.deleteById(id);
    }
}
