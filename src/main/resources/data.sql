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

MERGE INTO event_type (type_id, event_type)
VALUES
     (1, 'LIKE'),
     (2, 'REVIEW'),
     (3, 'FRIEND'),
     (4, 'FEEDBACK');

MERGE INTO event_operation (operation_id, operation_type)
VALUES
    (1, 'REMOVE'),
    (2, 'ADD'),
    (3, 'UPDATE');