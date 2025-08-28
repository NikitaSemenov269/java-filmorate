package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.DirectorRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmRepository;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreRepository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

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
            WHERE YEAR(f.RELEASE_DATE) = :year OR :year = 2999
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                 m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            LIMIT :count
            """;

    private static final String GET_POPULAR_FILM_GENRE_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description,
            COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            JOIN film_genre fg ON fg.FILM_ID = f.FILM_ID AND fg.GENRE_ID = :genreId
            LEFT JOIN likes l ON f.film_id = l.film_id
            WHERE YEAR(f.RELEASE_DATE) = :year OR :year = 2999
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                 m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            LIMIT :count
            """;

    private static final String DELETE_GENRE_FILM_QUERY = "DELETE FROM film_genre WHERE film_id = :filmId";
    private static final String DELETE_DIRECTOR_FILM_QUERY = "DELETE FROM film_directors WHERE film_id = :filmId";
    private static final String INSERT_GENRE_FILM_QUERY = """
            INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)""";
    private static final String INSERT_DIRECTOR_FILM_QUERY = """
            INSERT INTO film_directors(film_id, director_id) VALUES(?, ?)""";
    private static final String GET_DIRECTOR_FILMS_BY_YEAR = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description,
            COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            JOIN film_directors fd ON f.film_id = fd.film_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            WHERE fd.director_id = :directorId
            GROUP BY f.film_id, m.mpa_id
            ORDER BY f.release_date
            """;
    private static final String GET_DIRECTOR_FILMS_BY_LIKES = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description,
            COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            JOIN film_directors fd ON f.film_id = fd.film_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            WHERE fd.director_id = :directorId
            GROUP BY f.film_id, m.mpa_id
            ORDER BY like_count DESC
            """;
    private static final String GET_COMMON_FILMS_WITH_FRIEND = """
            SELECT f.*,m.mpa_id AS mpa_id, m.name AS mpa_name,m.description AS mpa_description,
            (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            WHERE EXISTS (SELECT 1 FROM likes l WHERE l.film_id = f.film_id AND l.user_id = :userId)
            AND EXISTS (SELECT 1 FROM likes l WHERE l.film_id = f.film_id AND l.user_id = :friendId)
            ORDER BY like_count DESC
            """;

    private static final String SEARCH_FILMS_BY_TITLE_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY f.film_id DESC
            """;

    private static final String SEARCH_FILMS_BY_DIRECTOR_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            JOIN film_directors fd ON f.film_id = fd.film_id
            JOIN directors d ON fd.director_id = d.director_id
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY f.film_id DESC
            """;

    private static final String SEARCH_FILMS_BY_TITLE_AND_DIRECTOR_QUERY = """
            SELECT DISTINCT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON f.film_id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.director_id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY f.film_id
            """;

    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, FilmRowMapper mapper, GenreRepository genreRepository,
                              DirectorRepository directorRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
        this.directorRepository = directorRepository;
    }

    @Override
    public List<Film> findAllFilms() {
        return findMany(FIND_ALL_FILMS_QUERY, new HashMap<>()).stream().peek(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
            film.setDirectors(directorRepository.findDirectorByFilmId(film.getId()));
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return findOne(FIND_FILM_BY_ID_QUERY, params).map(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(filmId));
            film.setDirectors(directorRepository.findDirectorByFilmId(filmId));
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
        updateDirectors(film.getDirectors(), film.getId());
        return getFilmById(film.getId()).orElse(film);
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
        updateDirectors(newFilm.getDirectors(), newFilm.getId());
        newFilm.setGenres(genreRepository.findGenreByFilmId(newFilm.getId()));
        return newFilm;
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return delete(DELETE_FILM_QUERY, params);
    }

    @Override
    public Collection<Film> getPopularFilms(int count, Long genreId, int year) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        params.put("genreId", genreId);
        params.put("year", year);
        Collection<Film> films;
        if (genreId != null) {
            films = findMany(GET_POPULAR_FILM_GENRE_QUERY, params);
        } else {
            films = findMany(GET_POPULAR_FILM_QUERY, params);
        }
        for (Film film : films) {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
            film.setDirectors(directorRepository.findDirectorByFilmId(film.getId()));
        }
        return films;
    }

    public void updateDirectors(Set<Director> directors, Long filmId) {
        Set<Director> directorsToUpdate = directors != null ? directors : new HashSet<>();

        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("filmId", filmId);
        jdbc.update(DELETE_DIRECTOR_FILM_QUERY, baseParams);

        if (!directorsToUpdate.isEmpty()) {
            List<Director> directorList = new ArrayList<>(directorsToUpdate);

            jdbc.getJdbcOperations().batchUpdate(INSERT_DIRECTOR_FILM_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, filmId);
                    ps.setInt(2, Math.toIntExact(directorList.get(i).getId()));
                }

                @Override
                public int getBatchSize() {
                    return directorList.size();
                }
            });
        }
    }

    public void updateGenres(Set<Genre> genres, Long filmId) {
        Set<Genre> genresToUpdate = genres != null ? genres : new HashSet<>();

        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("filmId", filmId);
        jdbc.update(DELETE_GENRE_FILM_QUERY, baseParams);

        if (!genresToUpdate.isEmpty()) {
            List<Genre> genreList = new ArrayList<>(genresToUpdate);

            jdbc.getJdbcOperations().batchUpdate(INSERT_GENRE_FILM_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, filmId);
                    ps.setInt(2, Math.toIntExact(genreList.get(i).getId()));
                }

                @Override
                public int getBatchSize() {
                    return genreList.size();
                }
            });
        }
    }

    public Collection<Film> getDirectorFilmsSortedByYear(Long directorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("directorId", directorId);
        Collection<Film> films = findMany(GET_DIRECTOR_FILMS_BY_YEAR, params);
        for (Film film : films) {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
            film.setDirectors(directorRepository.findDirectorByFilmId(film.getId()));
        }
        return films;
    }

    public Collection<Film> getDirectorFilmsSortedByLikes(Long directorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("directorId", directorId);
        Collection<Film> films = findMany(GET_DIRECTOR_FILMS_BY_LIKES, params);
        for (Film film : films) {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
            film.setDirectors(directorRepository.findDirectorByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public Collection<Film> getCommonFilmsWithFriend(Long userId, Long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("friendId", friendId);
        return findMany(GET_COMMON_FILMS_WITH_FRIEND, params).stream().peek(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
            film.setDirectors(directorRepository.findDirectorByFilmId(film.getId()));
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getResultSearchForFilmsByTitle(String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        return findMany(SEARCH_FILMS_BY_TITLE_QUERY, params).stream().peek(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
            film.setDirectors(directorRepository.findDirectorByFilmId(film.getId()));
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getResultSearchForFilmsByDirector(String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        return findMany(SEARCH_FILMS_BY_DIRECTOR_QUERY, params).stream().peek(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
            film.setDirectors(directorRepository.findDirectorByFilmId(film.getId()));
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getResultSearchForFilmsByDirectorAndTitle(String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        return findMany(SEARCH_FILMS_BY_TITLE_AND_DIRECTOR_QUERY, params).stream().peek(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
            film.setDirectors(directorRepository.findDirectorByFilmId(film.getId()));
        }).collect(Collectors.toList());
    }
}
