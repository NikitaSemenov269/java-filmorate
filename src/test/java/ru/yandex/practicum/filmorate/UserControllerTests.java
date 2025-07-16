package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void userValidData() {
        User user = createValidUser();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userInvalidEmail() {
        User user = createValidUser();
        user.setEmail("invalid-email");
        assertSingleViolation(user, "Некорректный формат email.");

    }

    @Test
    void userCreateValidUser() {
        User user = createValidUser();
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    private User createValidUser() {
        User user = new User();
        user.setId(1); // Добавляем ID
        user.setEmail("valid@test.com");
        user.setLogin("validLogin");
        user.setName("TestUser");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    private <T> void assertSingleViolation(T object, String expectedMessage) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @Test
    void updateUser() {
        User user = createValidUser();
        ResponseEntity<User> createResponse = restTemplate.postForEntity("/users", user, User.class);
        User createdUser = createResponse.getBody();

        User updatedUser = createValidUser();
        updatedUser.setId(createdUser.getId());
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@test.com");

        ResponseEntity<User> updateResponse = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                User.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated Name", updateResponse.getBody().getName());
        assertEquals("updated@test.com", updateResponse.getBody().getEmail());
    }

    @Test
    void addFriend() {
        User user1 = createValidUser();
        user1.setLogin("user1");
        User user2 = createValidUser();
        user2.setId(2);
        user2.setEmail("valid2@test.com");
        user2.setLogin("user2");

        ResponseEntity<User> response1 = restTemplate.postForEntity("/users", user1, User.class);
        ResponseEntity<User> response2 = restTemplate.postForEntity("/users", user2, User.class);

        restTemplate.put("/users/{id}/friends/{friendId}", null,
                response1.getBody().getId(), response2.getBody().getId());

        ResponseEntity<User[]> friendsResponse = restTemplate.getForEntity(
                "/users/{id}/friends", User[].class, response1.getBody().getId());

        assertEquals(1, friendsResponse.getBody().length);
        assertEquals(response2.getBody().getId(), friendsResponse.getBody()[0].getId());
    }

    @Test
    void getMutualFriends() {
        User user1 = createValidUser();
        user1.setLogin("user1");
        User user2 = createValidUser();
        user2.setId(2);
        user2.setEmail("valid2@test.com");
        user2.setLogin("user2");
        User commonFriend = createValidUser();
        commonFriend.setId(3);
        commonFriend.setEmail("valid3@test.com");
        commonFriend.setLogin("common");

        ResponseEntity<User> response1 = restTemplate.postForEntity("/users", user1, User.class);
        ResponseEntity<User> response2 = restTemplate.postForEntity("/users", user2, User.class);
        ResponseEntity<User> commonResponse = restTemplate.postForEntity("/users", commonFriend, User.class);

        restTemplate.put("/users/{id}/friends/{friendId}", null,
                response1.getBody().getId(), commonResponse.getBody().getId());
        restTemplate.put("/users/{id}/friends/{friendId}", null,
                response2.getBody().getId(), commonResponse.getBody().getId());

        ResponseEntity<User[]> mutualResponse = restTemplate.getForEntity(
                "/users/{id}/friends/common/{otherId}", User[].class,
                response1.getBody().getId(), response2.getBody().getId());

        assertEquals(1, mutualResponse.getBody().length);
        assertEquals(commonResponse.getBody().getId(), mutualResponse.getBody()[0].getId());
    }

    @Test
    void deleteFriend() {
        User user1 = createValidUser();
        user1.setLogin("user1");
        User user2 = createValidUser();
        user2.setId(2);
        user2.setEmail("valid2@test.com");
        user2.setLogin("user2");

        ResponseEntity<User> response1 = restTemplate.postForEntity("/users", user1, User.class);
        ResponseEntity<User> response2 = restTemplate.postForEntity("/users", user2, User.class);

        restTemplate.put("/users/{id}/friends/{friendId}", null,
                response1.getBody().getId(), response2.getBody().getId());
        restTemplate.delete("/users/{id}/friends/{friendId}",
                response1.getBody().getId(), response2.getBody().getId());

        ResponseEntity<User[]> friendsResponse = restTemplate.getForEntity(
                "/users/{id}/friends", User[].class, response1.getBody().getId());

        assertEquals(0, friendsResponse.getBody().length);
    }
}

