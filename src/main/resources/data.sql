MERGE INTO mpa_ratings (mpa_id, name, description)
VALUES
        (1, 'G', 'Без возрастных ограничений'),
        (2, 'PG', 'Детям рекомендуется смотреть с родителями'),
        (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
        (4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого'),
        (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен');

MERGE INTO genres (genre_id, name)
VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

MERGE INTO users (user_id, email, login, name, birthday)
VALUES
    (1, 'user1@mail.ru', 'user1', 'User One', '1990-01-01'),
    (2, 'user2@mail.ru', 'user2', 'User Two', '1995-05-15'),
    (3, 'user3@mail.ru', 'user3', 'User Three', '2000-10-20');

MERGE INTO films (film_id, name, description, release_date, duration, mpa_id)
VALUES
    (1, 'Film 1', 'Description 1', '2000-01-01', 120, 1),
    (2, 'Film 2', 'Description 2', '2010-05-15', 90, 2);

MERGE INTO film_genre (film_id, genre_id)
VALUES
    (1, 1),  -- Film 1: Комедия
    (1, 2),  -- Film 1: Драма
    (2, 2);  -- Film 2: Драма

MERGE INTO friends (user_id, friend_id, confirmed)
VALUES
    (1, 2, TRUE),  -- user1 и user2 - подтвержденные друзья
    (2, 1, TRUE),  -- обратная связь для дружбы
    (1, 3, FALSE), -- user1 добавил user3, но не подтверждено
    (3, 1, TRUE);  -- user3 добавил user1 и подтвердил

MERGE INTO likes (film_id, user_id)
VALUES
    (1, 1), (1, 2),
    (2, 1), (2, 3);