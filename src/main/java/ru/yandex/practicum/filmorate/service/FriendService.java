package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.EventRepository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.interfaces.FriendRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final ValidationService validationService;
    private final EventRepository eventRepository;

    public void addFriend(Long userId, Long friendId) {
        log.info("Попытка добавления в друзья: пользователь {} добавляет {}", userId, friendId);
        validationService.validateUsersExist(userId, friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья.");
        }
        friendRepository.addFriend(userId, friendId);
        log.info("Пользователь {} отправил запрос на дружбу пользователю {}", userId, friendId);
        eventRepository.addEvent(userId, friendId, 3L /* друг */, 2L /* добавление */);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Попытка удаления пользователя {} из друзей пользователя {}", userId, friendId);
        validationService.validateUsersExist(userId, friendId);
        friendRepository.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
        eventRepository.addEvent(userId, friendId, 3L /* друг */, 1L /* удаление */);
    }

    public Collection<User> getFriends(Long userId) {
        log.info("Попытка получения списка друзей для пользователя {}", userId);
        validationService.validateUserExists(userId);
        return friendRepository.getFriends(userId);
    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        log.info("Получение общих друзей пользователей {} и {}", userId1, userId2);
        validationService.validateUsersExist(userId1, userId2);
        return friendRepository.getCommonFriends(userId1, userId2);
    }
}
