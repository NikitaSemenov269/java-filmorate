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
public class User {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long id;
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
    private Set<Integer> friendsId = new HashSet<>();
}



