-- ============================================================
--  БИБЛИОТЕЧНАЯ СИСТЕМА — схема базы данных
--  Запустить в pgAdmin: выбрать БД library_db → Tools → Query Tool
-- ============================================================

-- 1. Создание базы данных (выполнить от superuser в БД postgres)
-- CREATE DATABASE library_db;

-- 2. Таблица авторов
CREATE TABLE IF NOT EXISTS authors (
    id         SERIAL      PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    birth_year INT
);

-- 3. Таблица категорий (жанров)
CREATE TABLE IF NOT EXISTS categories (
    id   SERIAL      PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Таблица книг
CREATE TABLE IF NOT EXISTS books (
    id          SERIAL  PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    author_id   INT REFERENCES authors(id)    ON DELETE SET NULL,
    category_id INT REFERENCES categories(id) ON DELETE SET NULL,
    year        INT,
    available   BOOLEAN DEFAULT TRUE
);

-- ============================================================
--  Тестовые данные
-- ============================================================

INSERT INTO authors (first_name, last_name, birth_year) VALUES
    ('Лев',        'Толстой',    1828),
    ('Фёдор',      'Достоевский',1821),
    ('Михаил',     'Булгаков',   1891),
    ('Габриэль',   'Маркес',     1927),
    ('Джордж',     'Оруэлл',     1903)
ON CONFLICT DO NOTHING;

INSERT INTO categories (name) VALUES
    ('Роман'),
    ('Классика'),
    ('Антиутопия'),
    ('Магический реализм'),
    ('Фантастика')
ON CONFLICT DO NOTHING;

INSERT INTO books (title, author_id, category_id, year, available) VALUES
    ('Война и мир',                     1, 2, 1869, TRUE),
    ('Преступление и наказание',         2, 2, 1866, TRUE),
    ('Мастер и Маргарита',               3, 1, 1967, FALSE),
    ('Сто лет одиночества',              4, 4, 1967, TRUE),
    ('1984',                             5, 3, 1949, TRUE),
    ('Анна Каренина',                    1, 1, 1878, TRUE),
    ('Идиот',                            2, 1, 1869, FALSE),
    ('Скотный двор',                     5, 3, 1945, TRUE)
ON CONFLICT DO NOTHING;

-- ============================================================
--  Полезные SELECT-запросы для проверки в pgAdmin
-- ============================================================

-- Все книги с авторами и категориями
SELECT
    b.id,
    b.title,
    a.first_name || ' ' || a.last_name AS author,
    c.name                              AS category,
    b.year,
    CASE WHEN b.available THEN 'Доступна' ELSE 'Выдана' END AS status
FROM books b
LEFT JOIN authors    a ON b.author_id   = a.id
LEFT JOIN categories c ON b.category_id = c.id
ORDER BY b.title;

-- Статистика по категориям
SELECT c.name, COUNT(b.id) AS books_count
FROM categories c
LEFT JOIN books b ON b.category_id = c.id
GROUP BY c.name
ORDER BY books_count DESC;

-- Доступные книги
SELECT title FROM books WHERE available = TRUE;
