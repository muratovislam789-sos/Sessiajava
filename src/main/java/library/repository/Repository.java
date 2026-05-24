package library.repository;

import java.util.List;
import java.util.Optional;

/**
 * Универсальный интерфейс репозитория (CRUD).
 * Параметризован типом сущности T и типом идентификатора ID.
 *
 * Демонстрирует абстракцию и полиморфизм через дженерики.
 */
public interface Repository<T, ID> {

    /**
     * Сохранить новую запись. Возвращает сущность с присвоенным id.
     */
    T save(T entity);

    /**
     * Найти запись по идентификатору.
     */
    Optional<T> findById(ID id);

    /**
     * Вернуть все записи.
     */
    List<T> findAll();

    /**
     * Обновить существующую запись.
     */
    boolean update(T entity);

    /**
     * Удалить запись по идентификатору.
     */
    boolean deleteById(ID id);
}
