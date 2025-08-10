package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class MpaRating {
    private Long id;
    @NotBlank(message = "Рейтинг не может быть пустым")
    private String name;
    private String description;
}
