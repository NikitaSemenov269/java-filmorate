package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Data
public class User {
    @Positive(message = "Id должно быть положительным числом.")
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
}


