package library.service;

import library.model.Author;
import library.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой для работы с авторами.
 * Содержит бизнес-логику и валидацию.
 */
public class AuthorService {

    private final AuthorRepository repo;

    public AuthorService(AuthorRepository repo) {
        this.repo = repo;
    }

    public Author create(String firstName, String lastName, int birthYear) {
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("Имя автора не может быть пустым.");
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Фамилия автора не может быть пустой.");
        if (birthYear < 0 || birthYear > 2025)
            throw new IllegalArgumentException("Некорректный год рождения: " + birthYear);

        Author author = new Author(firstName.trim(), lastName.trim(), birthYear);
        return repo.save(author);
    }

    public Optional<Author> getById(int id) {
        return repo.findById(id);
    }

    public List<Author> getAll() {
        return repo.findAll();
    }

    public boolean update(int id, String firstName, String lastName, int birthYear) {
        Optional<Author> opt = repo.findById(id);
        if (opt.isEmpty()) {
            System.out.println("Автор с id=" + id + " не найден.");
            return false;
        }
        Author author = opt.get();
        author.setFirstName(firstName.trim());
        author.setLastName(lastName.trim());
        author.setBirthYear(birthYear);
        return repo.update(author);
    }

    public boolean delete(int id) {
        return repo.deleteById(id);
    }
}
