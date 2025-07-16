package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Service
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void addUser(User newUser) {
        users.put(newUser.getId(), newUser);
    }

    @Override
    public User updateUser(User updatingUser) {
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
    public User getUserForId(Integer id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден.");
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }
}
