package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Genre {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Integer id;
    @NotBlank(message = "Жанр обязателен к заполнению.")
    private String genre;
}
