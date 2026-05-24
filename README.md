# 📚 Библиотечная система — Java Console App

## Архитектура проекта

```
library-system/
├── pom.xml
├── schema.sql                        ← SQL для pgAdmin
└── src/main/java/library/
    ├── Main.java                     ← Точка входа
    ├── db/
    │   ├── DatabaseConnection.java   ← Singleton подключения к PostgreSQL
    │   └── SchemaInitializer.java    ← Создание таблиц при старте
    ├── model/
    │   ├── Author.java               ← Сущность "Автор"
    │   ├── Book.java                 ← Сущность "Книга"
    │   └── Category.java             ← Сущность "Категория"
    ├── repository/
    │   ├── Repository.java           ← Интерфейс (generic CRUD)
    │   ├── AuthorRepository.java     ← CRUD авторов
    │   ├── BookRepository.java       ← CRUD книг (с JOIN)
    │   └── CategoryRepository.java   ← CRUD категорий
    ├── service/
    │   ├── AuthorService.java        ← Бизнес-логика авторов
    │   ├── BookService.java          ← Бизнес-логика книг
    │   └── CategoryService.java      ← Бизнес-логика категорий
    └── ui/
        └── ConsoleMenu.java          ← Консольный интерфейс
```

## Принципы ООП

| Принцип         | Реализация |
|-----------------|------------|
| Инкапсуляция    | Все поля в model private, доступ через getters/setters |
| Абстракция      | Интерфейс `Repository<T, ID>` — единый контракт для всех репозиториев |
| Полиморфизм     | `AuthorRepository`, `BookRepository`, `CategoryRepository` реализуют один интерфейс |
| Разделение ответственности | model / repository / service / ui |

## Требования

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

## Настройка PostgreSQL

### 1. Создать базу данных
```sql
-- В pgAdmin: Tools → Query Tool (подключившись к postgres)
CREATE DATABASE library_db;
```

### 2. Загрузить схему
Открыть файл `schema.sql` в pgAdmin:
- Выбрать БД `library_db`
- Tools → Query Tool → открыть файл → выполнить

### 3. Настроить подключение (если нужно)
Отредактировать `DatabaseConnection.java`:
```java
private static final String URL      = "jdbc:postgresql://localhost:5432/library_db";
private static final String USER     = "postgres";
private static final String PASSWORD = "postgres";  // ← свой пароль
```

## Сборка и запуск

```bash
# Сборка
mvn clean package

# Запуск
java -jar target/library-system-1.0-SNAPSHOT.jar
```

## Функциональность

### Книги
- ✅ Просмотр всех книг (с автором и категорией через JOIN)
- ✅ Поиск по названию (ILIKE)
- ✅ Фильтр по доступности
- ✅ Добавление
- ✅ Редактирование
- ✅ Выдача / возврат книги
- ✅ Удаление

### Авторы
- ✅ Просмотр всех авторов
- ✅ Добавление с валидацией
- ✅ Редактирование
- ✅ Удаление

### Категории
- ✅ Полный CRUD

## Сущности и связи

```
Author ──< Book >── Category
  1          *          1
```

- Один автор может написать много книг
- Одна книга принадлежит одной категории
- При удалении автора/категории книга сохраняется (SET NULL)
"# Sessiajava" 
