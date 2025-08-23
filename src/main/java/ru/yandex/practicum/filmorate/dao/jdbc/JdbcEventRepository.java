package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.EventRepository;
import ru.yandex.practicum.filmorate.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Qualifier("eventRepository")
public class JdbcEventRepository extends BaseRepository<Event> implements EventRepository {

    private static final String INSERT_USERS_QUERY = "INSERT INTO event_feed (user_id, type_id," +
            " operation_id, entity_id, created_at)" +
            "VALUES (:userId, :typeId, :operationId, :entityId, :creationTime)";

    private static final String FIND_EVENT_LIST_BY_USER_ID_QUERY = """
            SELECT ef.*,
                et.event_type type,
                eo.operation_type operation
            FROM event_feed ef
            JOIN event_type et ON ef.type_id = et.type_id
            JOIN event_operation eo ON ef.operation_id = eo.operation_id
            JOIN friends f ON (
                     (f.user_id = :userId AND f.friend_id = ef.user_id) OR
                     (f.friend_id = :userId AND f.user_id = ef.user_id)
            )
            WHERE f.confirmed = true
            ORDER BY ef.created_at DESC
            """;

    public JdbcEventRepository(NamedParameterJdbcOperations jdbc, EventRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addEvent(Long userId, Long entityId, Long typeId, Long operationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("entityId", entityId);
        params.put("typeId", typeId);
        params.put("operationId", operationId);
        params.put("creationTime", LocalDateTime.now());

        safeInsert(INSERT_USERS_QUERY, params);
    }

    @Override
    public Collection<Event> getEventListByUserId(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return findMany(FIND_EVENT_LIST_BY_USER_ID_QUERY, params);
    }
}



