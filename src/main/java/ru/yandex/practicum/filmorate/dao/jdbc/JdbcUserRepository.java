package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.UserRepository;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
@Qualifier("userRepository")
public class JdbcUserRepository extends BaseRepository<User> implements UserRepository {
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users ORDER BY user_id";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :userId";
    private static final String INSERT_USERS_QUERY = "INSERT INTO users (email, login, name, birthday)" + "VALUES (:email, :login, :name, :birthday)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = :email, login = :login, name = :name, " + "birthday = :birthday WHERE user_id = :userId";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = :userId";
    private static final String FIND_USER_RECOMMENDATIONS_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description  FROM LIKES l3
            LEFT JOIN FILMS f ON f.film_id = l3.FILM_ID
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            WHERE l3.USER_ID in (SELECT l2.USER_ID FROM LIKES l2
                        WHERE l2.FILM_ID IN (SELECT FILM_ID FROM LIKES l
                                   WHERE l.USER_ID = :user_id)
                        AND USER_ID <> :user_id
                        GROUP BY l2.USER_ID
                        HAVING count(l2.FILM_ID) = (SELECT count(l5.FILM_ID) FROM LIKES l5
                               WHERE l5.FILM_ID IN (SELECT FILM_ID FROM LIKES l
                               WHERE l.USER_ID = :user_id)
                               AND USER_ID <> :user_id
                               GROUP BY l5.USER_ID
                               ORDER BY count(l5.USER_ID) DESC
                               LIMIT 1))
            AND l3.FILM_ID NOT IN (SELECT l4.film_id FROM LIKES l4
                WHERE l4.USER_ID = :user_id)
            """;

    public JdbcUserRepository(NamedParameterJdbcOperations jdbc, UserRowMapper mapper) {
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

    @Override
    public Collection<Film> getUserRecommendations(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", id);
        return jdbc.query(FIND_USER_RECOMMENDATIONS_QUERY, params, new FilmRowMapper());
    }
}

