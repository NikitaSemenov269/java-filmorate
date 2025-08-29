package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final NamedParameterJdbcOperations jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Map<String, ?> params) {
        try {
            T result = jdbc.queryForObject(query, params, mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Map<String, ?> params) {
        return jdbc.query(query, params, mapper);
    }

    protected boolean delete(String query, Map<String, ?> params) {
        int rowsDeleted = jdbc.update(query, params);
        return rowsDeleted > 0;
    }

    protected long insert(String query, Map<String, ?> params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(query, new MapSqlParameterSource(params), keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            return key.longValue();
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    //event
    protected void safeInsert(String query, Map<String, ?> params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(query, new MapSqlParameterSource(params), keyHolder, new String[]{"EVENT_ID"});
        Number key = keyHolder.getKey();
        if (key != null) {
            key.longValue();
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected boolean update(String query, Map<String, ?> params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return rowsUpdated > 0;
    }
}



