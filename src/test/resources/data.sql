DELETE FROM comments;
DELETE FROM ads;
DELETE FROM users;

-- Тестовые пользователи
INSERT INTO users (id, username, password, first_name, last_name, phone, role, enabled, created_at)
VALUES
(1, 'user@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVYc7e', 'Иван', 'Иванов', '+7 (911) 123-45-67', 'USER', true, CURRENT_TIMESTAMP),
(2, 'admin@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVYc7e', 'Админ', 'Админов', '+7 (912) 987-65-43', 'ADMIN', true, CURRENT_TIMESTAMP);

-- Тестовые объявления
INSERT INTO ads (id, title, price, description, author_id, created_at)
VALUES
(1, 'Продам автомобиль', 1000000, 'Отличное состояние', 1, CURRENT_TIMESTAMP),
(2, 'Сниму квартиру', 30000, 'Ищу 2-х комнатную квартиру', 2, CURRENT_TIMESTAMP);

-- Тестовые комментарии
INSERT INTO comments (id, text, author_id, ad_id, created_at)
VALUES
(1, 'Интересное предложение', 2, 1, CURRENT_TIMESTAMP),
(2, 'Да, можно посмотреть', 1, 1, CURRENT_TIMESTAMP);