package ru.yandex.practicum.filmorate.dao.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.LikeRepository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcLikeRepository implements LikeRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId)";
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        params.put("userId", userId);
        jdbc.update(sql, params);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        params.put("userId", userId);
        jdbc.update(sql, params);
    }
}
