package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Event {
    private Long eventId;
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long userId;
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long entityId;
    private String eventType;
    private String operation;
    private Long timestamp;
}
