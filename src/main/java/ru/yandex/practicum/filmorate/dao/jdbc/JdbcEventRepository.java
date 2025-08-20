package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.EventRepository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Qualifier("eventRepository")
public class JdbcEventRepository extends BaseRepository<Event> implements EventRepository {

    private static final String INSERT_USERS_QUERY = "INSERT INTO event_feed (user_id, type_id," +
            " operation_id, entity_id)" +
            "VALUES (:userId, :typeId, :operationId, :entityId)";
    private static final String FIND_EVENT_LIST_BY_USER_ID_QUERY = """
            SELECT
                ef.created_at date,
                f.friend_id,
                ef.entity_id,
                ef.event_id,
                et.event_type type,
                eo.operation_type operation
            FROM event_feed ef
            JOIN friends f ON ef.user_id = f.friend_id
            JOIN event_type et ON ef.type_id = et.type_id
            JOIN event_operation eo ON ef.operation_id = eo.operation_id
            WHERE f.user_id = :userId
            ORDER BY ef.created_at DESC
            """;

    public JdbcEventRepository(NamedParameterJdbcOperations jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addEvent(Long userId, Long entityId, Long typeId, Long operationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("entityId", entityId);
        params.put("typeId", typeId);
        params.put("operationId", operationId);

        insert(INSERT_USERS_QUERY, params);
    }

    @Override
    public Collection<Event> getEventListByUserId(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return findMany(FIND_EVENT_LIST_BY_USER_ID_QUERY, params);
    }
}



