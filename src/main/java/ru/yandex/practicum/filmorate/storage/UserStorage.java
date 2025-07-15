package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User createUser(User newUser);

    public User updateUser(User updatingUse);

    public void deleteUser(Integer id);

    public User getUserForId(Integer id);

    public Collection<User> findAll();

}
