package ru.yandex.practicum.filmorate.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JdbcFriendRepository.class, UserRowMapper.class}) // Импортируем необходимые бины
@DirtiesContext // Очищаем контекст после каждого теста
public class JdbcFriendRepositoryIntegrationTest {

    @Autowired
    private JdbcFriendRepository friendRepository;

    @Test
    public void testAddFriend() {
        Long userId = 1L; // Предполагается, что пользователь с id=1 уже существует в БД
        Long friendId = 2L; // Предполагается, что пользователь с id=2 уже существует в БД

        friendRepository.addFriend(userId, friendId);

        boolean hasFriendship = friendRepository.hasFriendship(userId, friendId);
        assertThat(hasFriendship).isTrue();
    }

    @Test
    public void testRemoveFriend() {
        Long userId = 1L; // Предполагается, что пользователь с id=1 уже существует в БД
        Long friendId = 2L; // Предполагается, что пользователь с id=2 уже существует в БД

        friendRepository.addFriend(userId, friendId); // Сначала добавляем друга
        friendRepository.removeFriend(userId, friendId); // Затем удаляем

        boolean hasFriendship = friendRepository.hasFriendship(userId, friendId);
        assertThat(hasFriendship).isFalse();
    }

    @Test
    public void testGetFriends() {
        Long userId = 1L; // Предполагается, что пользователь с id=1 уже существует в БД и имеет друзей

        List<User> friends = friendRepository.getFriends(userId);

        assertThat(friends).isNotEmpty(); // Проверяем, что список друзей не пустой
    }

    @Test
    public void testGetCommonFriends() {
        Long userId1 = 1L; // Предполагается, что пользователь с id=1 уже существует в БД и имеет общих друзей
        Long userId2 = 2L; // Предполагается, что пользователь с id=2 уже существует в БД и имеет общих друзей

        List<User> commonFriends = friendRepository.getCommonFriends(userId1, userId2);

        assertThat(commonFriends).isNotEmpty(); // Проверяем, что список общих друзей не пустой
        assertThat(commonFriends).hasSize(1);    // Ожидаем один общий друг
        assertThat(commonFriends.get(0).getId()).isEqualTo(3L); // Общий друг имеет id = 3
    }
}