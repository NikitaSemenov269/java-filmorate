package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.UserRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.BaseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("userRepository")
public class JdbcUserRepository extends BaseRepository<User> implements UserRepository {
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users ORDER BY user_id";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :userId";
    private static final String INSERT_USERS_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (:email, :login, :name, :birthday)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = :email, login = :login, name = :name, " +
            "birthday = :birthday WHERE user_id = :userId";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = :userId";

    public JdbcUserRepository(NamedParameterJdbcOperations jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAllUsers() {
        return findMany(FIND_ALL_USERS_QUERY, new HashMap<>());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return findOne(FIND_USER_BY_ID_QUERY, params);
    }

    @Override
    public User createUser(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", user.getEmail());
        params.put("login", user.getLogin());
        params.put("name", user.getName());
        params.put("birthday", user.getBirthday());

        long id = insert(INSERT_USERS_QUERY, params);
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", newUser.getEmail());
        params.put("login", newUser.getLogin());
        params.put("name", newUser.getName());
        params.put("birthday", newUser.getBirthday());
        params.put("userId", newUser.getId());

        update(UPDATE_USER_QUERY, params);
        return newUser;
    }

    @Override
    public boolean deleteUser(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", id);
        return delete(DELETE_USER_QUERY, params);
    }
}

