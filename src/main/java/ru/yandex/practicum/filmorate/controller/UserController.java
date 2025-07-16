package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@RequestBody @Valid User newUser) {
        log.info("Попытка создания пользователя: {}", newUser.getLogin());
        return userService.createUser(newUser);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Попытка обновления данных пользователя: {}", newUser.getId());
        if (newUser.getId() == null || newUser.getId() <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }
        return userService.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{idFriend}")
    public User addNewFriendForId(@PathVariable Integer id,
                                  @PathVariable Integer idFriend) {
        log.info("Попытка добавления друга: {} для пользователя: {}", idFriend, id);
        if (id == null || id <= 0 || idFriend == null || idFriend <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }
        return userService.addNewFriend(id, idFriend);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Попытка получения списка всех пользователей.");
        return userService.findAll();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriendsForId(@PathVariable Integer id) {
        log.info("Попытка получения всех друзей пользователя: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }
        return userService.getAllFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(
            @PathVariable Integer id,
            @PathVariable Integer otherId) {
        log.info("Попытка получения общих друзей пользователей: {} и {}", id, otherId);
        if (id == null || id <= 0 || otherId == null || otherId <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }
        return userService.getMutualFriends(id, otherId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Попытка удаления пользователя: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }
        userService.deleteUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendsById(@PathVariable Integer id,
                                  @PathVariable Integer friendId) {
        log.info("Попытка удаления друга пользователя: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }
        userService.deleteFriendById(id, friendId);
    }
}
