package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Estimation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EstimationRowMapper implements RowMapper<Estimation> {
    @Override
    public Estimation mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Estimation.builder().reviewId(resultSet.getLong("review_id")).userId(resultSet.getLong("user_id")).isLike(resultSet.getBoolean("is_like")).build();
    }
}