DELETE FROM friends;
DELETE FROM likes;
DELETE FROM film_genre;
DELETE FROM film_directors;

DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

DELETE FROM films;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;

DELETE FROM directors;
ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1;

DELETE FROM genres;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;

DELETE FROM mpa_ratings;
ALTER TABLE mpa_ratings ALTER COLUMN mpa_id RESTART WITH 1;

DELETE FROM event_type;
ALTER TABLE event_type ALTER COLUMN type_id RESTART WITH 1;

DELETE FROM event_operation;
ALTER TABLE event_operation ALTER COLUMN operation_id RESTART WITH 1;

INSERT INTO mpa_ratings (mpa_id, name, description) VALUES
(1, 'G', 'Без возрастных ограничений'),
(2, 'PG', 'Детям рекомендуется смотреть с родителями'),
(3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
(4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого'),
(5, 'NC-17', 'Лицам до 18 лет просмотр запрещен');

INSERT INTO genres (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

INSERT INTO directors (name) VALUES
('Квентин Тарантино'),
('Кристофер Нолан'),
('Стивен Спилберг'),
('Джеймс Кэмерон'),
('Мартин Скорсезе'),
('Андрей Тарковский');

INSERT INTO event_type (type_id, event_type) VALUES
(1, 'LIKE'),
(2, 'REVIEW'),
(3, 'FRIEND'),
(4, 'FEEDBACK');

INSERT INTO event_operation (operation_id, operation_type) VALUES
(1, 'REMOVE'),
(2, 'ADD'),
(3, 'UPDATE');

INSERT INTO users (email, login, name, birthday) VALUES
('user1@example.com', 'user1', 'User One', '1990-01-01'),
('user2@example.com', 'user2', 'User Two', '1990-05-05'),
('user3@example.com', 'user3', 'User Three', '1990-03-03');

INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES
('Test Film 1_В', 'Test Description 1', '2020-01-01', 120, 1), --ru
('Test Film 2_ТА', 'Test Description 2', '2021-01-01', 150, 2); --ru

INSERT INTO friends (user_id, friend_id, confirmed) VALUES
(1, 3, TRUE),
(2, 3, TRUE);

INSERT INTO likes (film_id, user_id) VALUES
(1, 1),
(1, 2);

INSERT INTO film_genre (film_id, genre_id) VALUES
(1, 1),
(1, 2);

INSERT INTO film_directors (film_id, director_id) VALUES
(1, 1),
(2, 2);
