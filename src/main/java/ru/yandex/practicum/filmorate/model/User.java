package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
public class User {
    private Set<Integer> friendsId = new HashSet<>();
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Integer id;
    @NotBlank(message = "Email не может быть пустой строкой.")
    @Email(message = "Некорректный формат email.")
    private String email;
    @NotBlank(message = "Логин обязателен для заполнения.")
    @Size(max = 30, message = "Логин пользователя не может быть больше 30 символов.")
    private String login;
    @Size(max = 30, message = "Имя пользователя не может быть больше 30 символов.")
    private String name;
    @Past(message = "Дата рождения не должна быть в настоящем, или будущем.")
    private LocalDate birthday;

    public void addNewFriend(Integer id) {
        log.info("Попытка добавления в список друзей: {}", id);
        if (!friendsId.add(id)) {
            throw new DuplicatedDataException("Пользователь с таким id уже добавлен в друзья: " + id);
        }
    }

    public void deleteFriend(Integer id) {
        log.info("Попытка удаления из списка друзей: {}", id);
        friendsId.remove(id);
    }

    public Set<Integer> getIdAllfriends() {
        log.info("Получение всех id друзей пользователя.");
        return new HashSet<>(friendsId);
    }
}


