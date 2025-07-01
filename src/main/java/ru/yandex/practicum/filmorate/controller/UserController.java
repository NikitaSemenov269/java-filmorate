package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Попытка создание нового пользователя: {}", user.getEmail());
        try {
            if (users.values().stream().anyMatch(user1 -> user1.getLogin().equals(user.getLogin()))) {
                log.error("Пользователь с таким логином уже создан: {}", user.getLogin());
                throw new DuplicatedDataException("Login " + user.getLogin() + " уже занят.");
            }
            if (users.values().stream().anyMatch(user1 -> user1.getEmail().equalsIgnoreCase(user.getEmail()))) {
                log.error("Пользователь с таким email уже создан: {}", user.getEmail());
                throw new DuplicatedDataException("Email " + user.getEmail() + " уже занят.");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }

            user.setId(getNextId());

            users.put(user.getId(), user);
            log.info("Создан новой пользователь: {}", user.getId());
            return user;
        } catch (RuntimeException e) {
            log.error("Ошибка при попытке создания пользователя.");
            throw e;
        }
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Обновление данных пользователя");
        try {
            if (newUser.getId() == null || !users.containsKey(newUser.getId())) {
                log.error("Пользователь с таким id не существует {}", newUser.getId());
                throw new NotFoundException("Пользователь с id: " + newUser.getId() + " не найден.");
            }
            User user = users.get(newUser.getId());
        if (!newUser.getLogin().equals(user.getLogin()) && users.values().stream()
        .anyMatch(user1 -> user1.getLogin().equals(newUser.getLogin()))) {
            log.error("Пользователь с таким логином уже создан: {}", user.getLogin());
            throw new DuplicatedDataException("Login " + user.getLogin() + " уже занят.");
        }
        if (!newUser.getEmail().equals(user.getEmail()) && users.values().stream()
        .anyMatch(user1 -> user1.getEmail().equalsIgnoreCase(newUser.getEmail()))) {
            log.error("Пользователь с таким email уже создан: {}", user.getEmail());
            throw new DuplicatedDataException("Email " + user.getEmail() + " уже занят.");
        }

            Optional.ofNullable(newUser.getLogin()).ifPresent(user::setLogin);
            Optional.ofNullable(newUser.getEmail()).ifPresent(user::setEmail);
            Optional.ofNullable(newUser.getBirthday()).ifPresent(user::setBirthday);
            Optional.ofNullable(newUser.getName())
                    .ifPresentOrElse(user::setName, () -> user.setName(newUser.getLogin()));

            log.info("Успешное обновление данных пользователя: {}", user.getId());
            return user;
        } catch (RuntimeException e) {
            log.error("Ошибка при попытке обновления пользователя.");
            throw e;
        }
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение всех пользователей в количестве: {}", users.size());
        try {
            return users.values();
        } catch (RuntimeException e) {
            log.error("Ошибка при попытке получения списка пользователей.");
            throw e;
        }
    }

    private Integer getNextId() {
        return users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
