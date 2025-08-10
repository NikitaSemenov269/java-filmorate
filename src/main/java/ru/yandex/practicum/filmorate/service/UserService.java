package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.interfaces.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public Collection<User> findAllUsers() {
        log.info("Попытка получения списка всех пользователей.");
        return userRepository.findAllUsers();
    }

    public User getUserById(Long userId) {
        log.info("Попытка получения пользователя по ID: {}", userId);
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null.");
        }
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
    }
    public User createUser(User user) {
        log.info("Попытка создания нового пользователя: email={}, login={}", user.getEmail(), user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User createdUser = userRepository.createUser(user);
        log.info("Создан пользователь с ID: {}", createdUser.getId());
        return createdUser;
    }
    public User updateUser(User newUser) {
        log.info("Попытка обновления пользователя с ID: {}", newUser.getId());
        if (newUser.getId() == null) {
            throw new ValidationException("ID пользователя не может быть null.");
        }
        validationService.validateUserExists(newUser.getId());
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        User updatedUser = userRepository.updateUser(newUser);
        log.info("Пользователь с ID {} обновлен", newUser.getId());
        return updatedUser;
    }
}
