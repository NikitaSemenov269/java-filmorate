package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    //UserStorage
    //POST
    @PostMapping
    public User create(@RequestBody @Valid User newUser) {
        return userStorage.createUser(newUser);
    }

    //PUT
    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        return userStorage.updateUser(newUser);
    }

    //GET
    @GetMapping
    //Возвращаем список пользователей
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    //DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userStorage.deleteUser(id);
    }

    //UserService
    //PUT
    @PutMapping("/{id}/friends/{idFriend}")
    public User addNewFriendForId(@PathVariable Integer id,
                                  @PathVariable Integer idFriend) {
        return userService.addNewFriend(id, idFriend);
    }

    //GET
    //Возвращаем список пользователей, являющихся друзьями пользователя
    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriendsForId(@PathVariable Integer id) {
        return userService.getAllFriendsById(id);
    }

    //Возвращаем список друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(
            @PathVariable Integer id,
            @PathVariable Integer otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    //DELETE
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendsById(@PathVariable Integer id,
                                  @PathVariable Integer friendId) {
        userService.deleteFriendById(id, friendId);
    }
}
