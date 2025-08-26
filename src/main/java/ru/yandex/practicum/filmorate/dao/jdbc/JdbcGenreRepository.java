package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreRepository;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {
    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :genreId";
    private static final String FIND_FILM_GENRES_BY_ID_QUERY = """
            SELECT g.genre_id, g.name
            FROM film_genre fg
            JOIN genres g ON fg.genre_id = g.genre_id
            WHERE fg.film_id = :filmId
            ORDER BY g.genre_id
            """;

    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Genre> findGenreById(Long genreId) {
        Map<String, Object> params = new HashMap<>();
        params.put("genreId", genreId);
        return findOne(FIND_GENRE_BY_ID_QUERY, params);
    }

    @Override
    public List<Genre> findAllGenres() {
        return findMany(FIND_ALL_GENRES_QUERY, new HashMap<>());
    }

    @Override
    public Set<Genre> findGenreByFilmId(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return findMany(FIND_FILM_GENRES_BY_ID_QUERY, params).stream().collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(Genre::getId))));
    }

}

