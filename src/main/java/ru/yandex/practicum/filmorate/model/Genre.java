package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Genre {
    private Long id;
    @NotBlank(message = "Жанр не может быть пустым")
    private String name;
}
