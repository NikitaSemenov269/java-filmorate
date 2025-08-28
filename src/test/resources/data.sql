MERGE INTO mpa_ratings (mpa_id, name, description) VALUES
(1, 'G', 'General Audiences'),
(2, 'PG', 'Parental Guidance Suggested'),
(3, 'PG-13', 'Parents Strongly Cautioned'),
(4, 'R', 'Restricted'),
(5, 'NC-17', 'Adults Only');

MERGE INTO genres (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO users (user_id, email, login, name, birthday) VALUES
(1, 'user1@example.com', 'user1', 'User One', '1990-01-01'),
(2, 'user2@example.com', 'user2', 'User Two', '1990-05-05'),
(3, 'user3@example.com', 'user3', 'User Three', '1990-03-03');

MERGE INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
(1, 'Test Film 1', 'Test Description 1', '2020-01-01', 120, 1),
(2, 'Test Film 2', 'Test Description 2', '2021-01-01', 150, 2);

MERGE INTO friends (user_id, friend_id, confirmed) VALUES
(1, 3, TRUE),
(2, 3, TRUE);

MERGE INTO likes (film_id, user_id) VALUES
(1, 1),
(1, 2);

MERGE INTO film_genre (film_id, genre_id) VALUES
(1, 1),
(1, 2),
(2, 3);