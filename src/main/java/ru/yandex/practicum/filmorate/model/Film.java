package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Data
public class Film {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Integer id;
    @NotBlank(message = "Название обязательно для заполнения.")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов.")
    private String description;
    @PastOrPresent(message = "Фильм не может быть выпущен в будущем.")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Длительность должна быть не менее 1 минуты.")
    private Integer duration;
    @NotNull(message = "Нужно указать возрастные ограничения.")
    private MPA mpa;
    @NotEmpty(message = "Жанр обязателен для заполнения")
    private List<Genre> genre = new ArrayList<>();
    private Set<Integer> idUsersWhoLiked = new HashSet<>();
}

