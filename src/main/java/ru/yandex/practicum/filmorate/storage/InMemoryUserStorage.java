package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Service
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User newUser) {
        log.info("Попытка создание нового пользователя: {}", newUser.getEmail());
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getId() == null) {
            newUser.setId(getNextId());
        }
        checkDuplicateLoginUser(newUser);
        checkDuplicateEmailUser(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Создан новой пользователь: {}", newUser.getId());
        return newUser;
    }

    @Override
    public User updateUser(User updatingUser) {
        log.info("Обновление данных пользователя");
        if (updatingUser.getId() == null || updatingUser.getId() < 0) {
            throw new ValidationException("Некорректное значение id пользователя.");
        }
        validateUserId(updatingUser);
        checkDuplicateLoginUser(updatingUser);
        checkDuplicateEmailUser(updatingUser);

        User user = users.get(updatingUser.getId());
        Optional.ofNullable(updatingUser.getLogin()).ifPresent(user::setLogin);
        Optional.ofNullable(updatingUser.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(updatingUser.getBirthday()).ifPresent(user::setBirthday);
        Optional.ofNullable(updatingUser.getName())
                .ifPresentOrElse(user::setName, () -> user.setName(updatingUser.getLogin()));
        log.info("Успешное обновление данных пользователя: {}", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        log.info("Попытка удалить пользователя: {}", id);
        if (!(id == null || id <= 0)) {
            users.remove(id);
        }
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получение всех пользователей в количестве: {}", users.size());
        try {
            return users.values();
        } catch (RuntimeException e) {
            log.error("Ошибка при попытке получения списка пользователей.");
            throw e;
        }
    }

    @Override
    public User getUserForId(Integer id) {
        log.info("Попытка получить пользователя: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное значение id пользователя.");
        }
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден.");
        }
        return user;
    }

    private void validateUserId(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с таким id не существует {}", newUser.getId());
            throw new NotFoundException("Пользователь с id: " + newUser.getId() + " не найден.");
        }
    }

    private void checkDuplicateLoginUser(User newUser) {
        if (users.values().stream()
                .anyMatch(user1 -> user1.getLogin().equals(newUser.getLogin()))) {
            log.error("Пользователь с таким логином уже существует: {}", newUser.getLogin());
            throw new DuplicatedDataException("Login " + newUser.getLogin() + " уже занят.");
        }
    }

    private void checkDuplicateEmailUser(User newUser) {
        if (users.values().stream()
                .anyMatch(user1 -> user1.getEmail().equalsIgnoreCase(newUser.getEmail()))) {
            log.error("Пользователь с таким email уже создан: {}", newUser.getEmail());
            throw new DuplicatedDataException("Email " + newUser.getEmail() + " уже занят.");
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
