package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User newUser);

    Optional<User> getUserById(Long id);

    boolean deleteUser(Long id);
}
