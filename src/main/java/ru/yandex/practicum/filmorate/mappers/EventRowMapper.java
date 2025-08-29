package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getLong("event_id"))
                .userId(resultSet.getLong("user_id"))
                .eventType(resultSet.getString("type"))
                .operation(resultSet.getString("operation_type"))
                .entityId(resultSet.getLong("entity_id"))
                .timestamp(resultSet.getTimestamp("created_at").toInstant().toEpochMilli())
                .build();
    }
}