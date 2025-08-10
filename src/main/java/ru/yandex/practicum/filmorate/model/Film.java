package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Film {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long id;
    @NotBlank(message = "Название обязательно для заполнения.")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов.")
    private String description;
    @PastOrPresent(message = "Фильм не может быть выпущен в будущем.")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Длительность должна быть не менее 1 минуты.")
    private Integer duration;
    @NotNull(message = "Рейтинг MPA не может быть пустой")
    private MpaRating mpa;
    private Set<Long> idUsersWhoLiked = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
}

