package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getId() == null) {
            newUser.setId(getNextId());
        }
        checkDuplicateLoginUser(newUser);
        checkDuplicateEmailUser(newUser);
        userStorage.addUser(newUser);
        log.info("Создан новой пользователь: {}", newUser.getId());
        return newUser;
    }

    public User updateUser(User updatingUser) {
        if (userStorage.getUserForId(updatingUser.getId()) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        return userStorage.updateUser(updatingUser);
    }

    public User addNewFriend(Integer idUser, Integer idFriend) {
        if (idUser.equals(idFriend)) {
            throw new DuplicatedDataException("Пользователь не может быть другом самому себе.");
        }
        userStorage.getUserForId(idUser).getFriendsId().add(idFriend);
        userStorage.getUserForId(idFriend).getFriendsId().add(idUser);
        return userStorage.getUserForId(idUser);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void deleteUser(Integer id) {
        if (userStorage.getUserForId(id) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        userStorage.deleteUser(id);
    }

    public void deleteFriendById(Integer idUser, Integer idFriend) {
        if (userStorage.getUserForId(idUser) == null || userStorage.getUserForId(idFriend) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        userStorage.getUserForId(idFriend).getFriendsId().remove(idUser);
        userStorage.getUserForId(idUser).getFriendsId().remove(idFriend);
    }

    public List<User> getAllFriendsById(Integer idUser) {
        if (userStorage.getUserForId(idUser) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        return userStorage.getUserForId(idUser).getFriendsId().stream()
                .filter(Objects::nonNull)
                .map(userStorage::getUserForId)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Integer idUser, Integer idFriend) {
        if (userStorage.getUserForId(idUser) == null || userStorage.getUserForId(idFriend) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        Set<Integer> friendsOfUser = userStorage.getUserForId(idUser).getFriendsId();
        Set<Integer> friendsOfFriend = userStorage.getUserForId(idFriend).getFriendsId();

        return friendsOfUser.stream()
                .filter(Objects::nonNull)
                .filter(friendsOfFriend::contains)
                .map(userStorage::getUserForId)
                .collect(Collectors.toList());
    }

    private void checkDuplicateLoginUser(User newUser) {
        if (userStorage.findAll().stream()
                .anyMatch(user1 -> user1.getLogin().equals(newUser.getLogin()))) {
            log.error("Пользователь с таким логином уже существует: {}", newUser.getLogin());
            throw new DuplicatedDataException("Login " + newUser.getLogin() + " уже занят.");
        }
    }

    private void checkDuplicateEmailUser(User newUser) {
        if (userStorage.findAll().stream()
                .anyMatch(user1 -> user1.getEmail().equalsIgnoreCase(newUser.getEmail()))) {
            log.error("Пользователь с таким email уже создан: {}", newUser.getEmail());
            throw new DuplicatedDataException("Email " + newUser.getEmail() + " уже занят.");
        }
    }

    private Integer getNextId() {
        return userStorage.findAll()
                .stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0) + 1;
    }
}
