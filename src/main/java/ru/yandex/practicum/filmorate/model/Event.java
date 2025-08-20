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
    private Long id;
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long friendId;
    private String eventType;
    private String operation;
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long entityId;
    private LocalDateTime createDate;
}
