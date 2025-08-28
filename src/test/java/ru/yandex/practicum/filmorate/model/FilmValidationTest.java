package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testFilmNameCannotBeEmpty() {
        Film film = mock(Film.class);
        film.setName("");
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFilmDescriptionWithMoreThan200Size() {
        Film film = mock(Film.class);
        film.setDescription("d".repeat(201));
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testReleaseDateBeforeCinemaBirthday() {
        Film film = mock(Film.class);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testDurationMustBePositive() {
        Film film = mock(Film.class);
        film.setDuration(Integer.valueOf(0));
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}
