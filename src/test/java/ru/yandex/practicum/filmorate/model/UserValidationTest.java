package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

public class UserValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testEmailIsRequired() {
        User user = mock(User.class);
        user.setEmail(null);
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testLoginCannotContainSpaces() {
        User user = mock(User.class);
        user.setLogin("l o g i n");
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testBirthdayCannotBeInFuture() {
        User user = mock(User.class);
        user.setBirthday(LocalDate.now().plusDays(1));
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}
