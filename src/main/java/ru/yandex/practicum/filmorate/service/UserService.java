package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addNewFriend(Integer idUser, Integer idFriend) {
        log.info("Попытка добавления друга: {} для пользователя: {}", idFriend, idUser);
        if (idUser == null || idUser <= 0 || idFriend == null || idFriend <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }

        userStorage.getUserForId(idUser).addNewFriend(idFriend);
        userStorage.getUserForId(idFriend).addNewFriend(idUser);
        return userStorage.getUserForId(idUser);
    }

    public void deleteFriendById(Integer idUser, Integer idFriend) {
        log.info("Попытка удаления друга: {} для пользователя: {}", idFriend, idUser);
        if (idUser == null || idUser <= 0 || idFriend == null || idFriend <= 0) {
            throw new ValidationException("Некорректное значение id пользователя.");
        }
        if (userStorage.getUserForId(idUser) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        userStorage.getUserForId(idUser).deleteFriend(idFriend);
        userStorage.getUserForId(idFriend).deleteFriend(idUser);
    }

    public List<User> getAllFriendsById(Integer idUser) {
        log.info("Попытка получения всех друзей пользователя: {}", idUser);
        if (idUser == null || idUser <= 0) {
            throw new ValidationException("Некорректное значение id пользователя.");
        }
        if (userStorage.getUserForId(idUser) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        return userStorage.getUserForId(idUser).getIdAllfriends().stream()
                .filter(Objects::nonNull)
                .map(userStorage::getUserForId)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Integer idUser, Integer idFriend) {
        log.info("Попытка получения общих друзей пользователей: {} и {}", idUser, idFriend);
        if (idUser == null || idUser <= 0 || idFriend == null || idFriend <= 0) {
            throw new ValidationException("Некорректное значение id пользователя.");
        }
        Set<Integer> friendsOfUser = userStorage.getUserForId(idUser).getIdAllfriends();
        Set<Integer> friendsOfFriend = userStorage.getUserForId(idFriend).getIdAllfriends();

        return friendsOfUser.stream()
                .filter(Objects::nonNull)
                .filter(friendsOfFriend::contains)
                .map(userStorage::getUserForId)
                .collect(Collectors.toList());
    }
}
