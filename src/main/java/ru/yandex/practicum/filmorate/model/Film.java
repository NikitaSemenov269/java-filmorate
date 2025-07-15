package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
public class Film {
    private Set<Integer> idUsersWhoLiked = new HashSet<>();
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

    public void addLike(Integer id) {
        log.info("Попытка добавления лайка от пользователя: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное значение id пользователя: " + id);
        }
        if (idUsersWhoLiked.contains(id)) {
            throw new DuplicatedDataException("Пользователь с таким id уже поставил лайк: " + id);
        }
        idUsersWhoLiked.add(id);
    }

    public void deleteLike(Integer id) {
        log.info("Попытка удаления лайка от пользователя: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное значение id пользователя: " + id);
        }
        if (!idUsersWhoLiked.contains(id)) {
            throw new NotFoundException("Пользователь с id не ставил лайк: " + id);
        }
        idUsersWhoLiked.remove(id);
    }

    public Set<Integer> getIdAllUsersWhoLiked() {
        log.info("Получение всех id пользователей поставивших лайк.");
        return new HashSet<>(idUsersWhoLiked);
    }

    public Integer getNumberOfLikes() {
        return idUsersWhoLiked.size();
    }
}
