package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.DirectorRepository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;

@Repository
public class JdbcDirectorRepository extends BaseRepository<Director> implements DirectorRepository {
    private static final String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";
    private static final String FIND_DIRECTOR_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = :directorId";
    private static final String INSERT_DIRECTOR_QUERY = """
            INSERT INTO directors (name)
            VALUES (:name)
            """;
    private static final String UPDATE_DIRECTOR_QUERY = """
            UPDATE directors
            SET name = :name
            WHERE director_id = :directorId
            """;
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE director_id = :directorId";


    public JdbcDirectorRepository(NamedParameterJdbcOperations jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public Collection<Director> findAllDirectors() {
        return findMany(FIND_ALL_DIRECTORS_QUERY, new HashMap<>());
    }

    @Override
    public Optional<Director> getDirectorById(Long directorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("directorId", directorId);
        return findOne(FIND_DIRECTOR_BY_ID_QUERY, params);
    }

    @Override
    public Director createDirector(Director director) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", director.getName());
        long id = insert(INSERT_DIRECTOR_QUERY, params);
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", director.getName());
        update(UPDATE_DIRECTOR_QUERY, params);
        return director;
    }

    @Override
    public boolean deleteDirector(Long directorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("directorId", directorId);
        return delete(DELETE_DIRECTOR_QUERY, params);

    }
}
