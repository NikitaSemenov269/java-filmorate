package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import ru.yandex.practicum.filmorate.dao.interfaces.GenreRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmRepository")
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {
    private static final String FIND_ALL_FILMS_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            ORDER BY f.film_id
            """;

    private static final String FIND_FILM_BY_ID_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            WHERE f.film_id = :filmId
            """;

    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (:name, :description, :releaseDate, :duration, :mpaId)
            """;

    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = :name, description = :description, release_date = :releaseDate, duration = :duration, mpa_id = :mpaId
            WHERE film_id = :filmId
            """;

    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id = :filmId";

    private static final String GET_POPULAR_FILM_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description,
            COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                 m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            LIMIT :count
            """;

    private static final String DELETE_GENRE_FILM_QUERY = "DELETE FROM film_genre WHERE film_id = :filmId";
    private static final String INSERT_GENRE_FILM_QUERY = """
            INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)""";

    private final GenreRepository genreRepository;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, RowMapper<Film> mapper, GenreRepository genreRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Film> findAllFilms() {
        return findMany(FIND_ALL_FILMS_QUERY, new HashMap<>()).stream().peek(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return findOne(FIND_FILM_BY_ID_QUERY, params).map(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(filmId));
            return film;
        });
    }

    @Override
    public Film createFilm(Film film) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("releaseDate", film.getReleaseDate());
        params.put("duration", film.getDuration());
        params.put("mpaId", film.getMpa().getId());

        long id = insert(INSERT_FILM_QUERY, params);
        film.setId(id);
        updateGenres(film.getGenres(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", newFilm.getName());
        params.put("description", newFilm.getDescription());
        params.put("releaseDate", newFilm.getReleaseDate());
        params.put("duration", newFilm.getDuration());
        params.put("mpaId", newFilm.getMpa().getId());
        params.put("filmId", newFilm.getId());

        update(UPDATE_FILM_QUERY, params);
        updateGenres(newFilm.getGenres(), newFilm.getId());
        return newFilm;
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return delete(DELETE_FILM_QUERY, params);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        return findMany(GET_POPULAR_FILM_QUERY, params);
    }

    public void updateGenres(Set<Genre> genres, Long filmId) {
        Set<Genre> genresToUpdate = genres != null ? genres : new HashSet<>();

        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("filmId", filmId);
        jdbc.update(DELETE_GENRE_FILM_QUERY, baseParams);

        if (!genresToUpdate.isEmpty()) {
            List<Genre> genreList = new ArrayList<>(genresToUpdate);

            jdbc.getJdbcOperations().batchUpdate(
                    INSERT_GENRE_FILM_QUERY,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, filmId);
                            ps.setInt(2, Math.toIntExact(genreList.get(i).getId()));
                        }

                        @Override
                        public int getBatchSize() {
                            return genreList.size();
                        }
                    }
            );
        }
    }
}
