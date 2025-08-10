package ru.yandex.practicum.filmorate.dao.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.FriendRepository;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("friendRepository")
@RequiredArgsConstructor
public class JdbcFriendRepository implements FriendRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final UserRowMapper userRowMapper;

    private static final String ADD_FRIEND_QUERY = """
            INSERT INTO friends (user_id, friend_id, confirmed)
            VALUES (:userId, :friendId, TRUE)
            """;

    private static final String CONFIRM_FRIENDSHIP_QUERY = """
            UPDATE friends
            SET confirmed = TRUE
            WHERE user_id = :userId AND friend_id = :friendId
            """;

    private static final String REMOVE_FRIEND_QUERY = """
            DELETE FROM friends
            WHERE (user_id = :userId AND friend_id = :friendId)
            """;

    private static final String GET_FRIENDS_QUERY = """
            SELECT u.user_id, u.email, u.login, u.name, u.birthday
            FROM users u
            JOIN friends f ON u.user_id = f.friend_id
            WHERE f.user_id = :userId
            """;

    private static final String GET_COMMON_FRIENDS_QUERY = """
            SELECT u.user_id, u.email, u.login, u.name, u.birthday
            FROM users u
            WHERE u.user_id IN (
                SELECT f1.friend_id
                FROM friends f1
                JOIN friends f2 ON f1.friend_id = f2.friend_id
                WHERE f1.user_id = :userId1 AND f2.user_id = :userId2
               )
            """;

    private static final String CHECK_FRIENDSHIP_STATUS_QUERY = """
            SELECT COUNT(*) > 0 AS has_friendship
            FROM friends
            WHERE user_id = :userId AND friend_id = :friendId AND confirmed = :confirmed
            """;

    @Override
    public void addFriend(Long userId, Long friendId) {
        boolean hasReverseRequest = checkFriendshipStatus(userId, friendId, false);

        if (hasReverseRequest) {
        Map<String, Object> confirmParams1 = new HashMap<>();
        confirmParams1.put("userId", userId);
        confirmParams1.put("friendId", friendId);

        Map<String, Object> confirmParams2 = new HashMap<>();
        confirmParams2.put("userId", friendId);
        confirmParams2.put("friendId", userId);

        jdbc.update(CONFIRM_FRIENDSHIP_QUERY, confirmParams1);
        jdbc.update(CONFIRM_FRIENDSHIP_QUERY, confirmParams2);
        } else {
            Map<String, Object> addParams = new HashMap<>();
            addParams.put("userId", userId);
            addParams.put("friendId", friendId);
            jdbc.update(ADD_FRIEND_QUERY, addParams);
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("friendId", friendId);
        jdbc.update(REMOVE_FRIEND_QUERY, params);
    }

    @Override
    public List<User> getFriends(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return jdbc.query(GET_FRIENDS_QUERY, params, userRowMapper);
    }

    @Override
    public List<User> getCommonFriends(Long userId1, Long userId2) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId1", userId1);
        params.put("userId2", userId2);
        return jdbc.query(GET_COMMON_FRIENDS_QUERY, params, userRowMapper);
    }

    @Override
    public boolean hasFriendship(Long userId, Long friendId) {
        return checkFriendshipStatus(userId, friendId, true);
    }

    private boolean checkFriendshipStatus(Long userId, Long friendId, boolean confirmed) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("friendId", friendId);
        params.put("confirmed", confirmed);
        Boolean result = jdbc.queryForObject(CHECK_FRIENDSHIP_STATUS_QUERY, params, Boolean.class);
        return result != null ? result : false;
    }
}
