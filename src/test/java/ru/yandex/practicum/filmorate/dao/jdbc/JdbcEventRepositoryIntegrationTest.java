package ru.yandex.practicum.filmorate.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.mappers.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        EventRowMapper.class,
        JdbcEventRepository.class,
        UserRowMapper.class,
        JdbcFriendRepository.class,
        JdbcLikeRepository.class
})

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcEventRepositoryIntegrationTest {

    @Autowired
    private JdbcFriendRepository friendRepository;

    @Autowired
    private JdbcLikeRepository likeRepository;

    @Autowired
    private JdbcEventRepository eventRepository;

    @Test
    public void testAddEvent() {
        Long userId = 1L; // Предполагается, что пользователь с id=1 уже существует в БД
        Long friendId = 2L; // Предполагается, что пользователь с id=2 уже существует в БД

        friendRepository.addFriend(userId, friendId);
         eventRepository.getEventListByUserId(userId);

        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getId()).isEqualTo(createdFilm.getId());
        assertThat(foundFilm.get().getName()).isEqualTo("Test Film");
        assertThat(foundFilm.get().getDescription()).isEqualTo("Test Description");
        assertThat(foundFilm.get().getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(foundFilm.get().getDuration()).isEqualTo(120);
        assertThat(foundFilm.get().getMpa().getId()).isEqualTo(1L);

    }

}
