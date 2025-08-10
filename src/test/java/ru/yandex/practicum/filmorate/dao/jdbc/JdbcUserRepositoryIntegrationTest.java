package ru.yandex.practicum.filmorate.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JdbcUserRepository.class, UserRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcUserRepositoryIntegrationTest {

    @Autowired
    private JdbcUserRepository userRepository;

    @Test
    public void testCreateUser() {
        User user = User.builder()
                .email("test@example.com")
                .login("testlogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userRepository.createUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull().isPositive();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
        assertThat(createdUser.getLogin()).isEqualTo("testlogin");
        assertThat(createdUser.getName()).isEqualTo("Test User");
        assertThat(createdUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }
    @Test
    public void testFindAllUsers() {
        List<User> users = userRepository.findAllUsers();

        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com", "user3@example.com");
    }
    @Test
    public void testUpdateUser() {
        User user = User.builder()
                .email("original@example.com")
                .login("original")
                .name("Original Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userRepository.createUser(user);

        User updatedUser = User.builder()
                .id(createdUser.getId())
                .email("updated@example.com")
                .login("updated")
                .name("Updated Name")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        User result = userRepository.updateUser(updatedUser);

        assertThat(result.getId()).isEqualTo(createdUser.getId());
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getLogin()).isEqualTo("updated");
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));
    }
    @Test
    public void testDeleteUser() {
        User user = User.builder()
                .email("test@example.com")
                .login("testlogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userRepository.createUser(user);

        boolean deleted = userRepository.deleteUser(createdUser.getId());

        assertThat(deleted).isTrue();

        Optional<User> foundUser = userRepository.getUserById(createdUser.getId());
        assertThat(foundUser).isEmpty();
    }
    @Test
    public void testDeleteNonExistentUser() {
        boolean deleted = userRepository.deleteUser(999L);
        assertThat(deleted).isFalse();
    }

    @Test
    public void testFindNonExistentUser() {
        Optional<User> foundUser = userRepository.getUserById(999L);
        assertThat(foundUser).isEmpty();
    }
}
