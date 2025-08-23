package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.EstimationRepository;
import ru.yandex.practicum.filmorate.model.Estimation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("estimationRepository")
public class JdbcEstimationRepository extends BaseRepository<Estimation> implements EstimationRepository {

    private static final String INSERT_ESTIMATION_QUERY = """
            insert into estimations (review_id, user_id, is_like)
            values
                (:reviewId, :userId, :isLike)
            ;
            """;

    private static final String DELETE_ESTIMATION_QUERY = """
            delete from estimations
            where
                review_id = :reviewId
                and user_id = :userId
            ;
            """;

    private static final String FIND_ESTIMATION_BY_ID_QUERY = """
            select
                e.*
            from estimations e
            where
                e.review_id = :reviewId
                and e.user_id = :userId
            ;
            """;

    public JdbcEstimationRepository(NamedParameterJdbcOperations jdbc, RowMapper<Estimation> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addEstimation(Long reviewId, Long userId, Boolean isLike) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        params.put("isLike", isLike);
        update(INSERT_ESTIMATION_QUERY, params);
    }

    @Override
    public void deleteEstimation(Long reviewId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        update(DELETE_ESTIMATION_QUERY, params);
    }

    @Override
    public Optional<Estimation> getEstimation(Long reviewId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        return findOne(FIND_ESTIMATION_BY_ID_QUERY, params);
    }
}