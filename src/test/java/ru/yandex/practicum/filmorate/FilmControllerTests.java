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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void filmValidation() {
        Film film = createValidFilm();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void filmValidationBlankName() {
        Film film = createValidFilm();
        film.setName("");
        assertSingleViolation(film, "Название обязательно для заполнения.");
    }

    @Test
    void userCreateValidFilm() {
        Film film = createValidFilm();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }


    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }

    private <T> void assertSingleViolation(T object, String expectedMessage) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    private Film createTestFilm(String name, int likesCount) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        for (int i = 1; i <= likesCount; i++) {
            film.getIdUsersWhoLiked().add(i);
        }
        return film;
    }

    @Test
    void getPopularFilms() {
        restTemplate.postForEntity("/films", createTestFilm("Most Popular", 10), Film.class);
        restTemplate.postForEntity("/films", createTestFilm("Medium Popular", 5), Film.class);
        restTemplate.postForEntity("/films", createTestFilm("Least Popular", 1), Film.class);

        ResponseEntity<Film[]> response = restTemplate.getForEntity(
                "/films/popular?count=2",
                Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Film[] popularFilms = response.getBody();
        assertNotNull(popularFilms);
        assertEquals(2, popularFilms.length);
        assertEquals("Most Popular", popularFilms[0].getName());
        assertEquals("Medium Popular", popularFilms[1].getName());
    }

    @Test
    void getPopularFilmsWhenCountGreaterThanTotal() {
        restTemplate.postForEntity("/films", createTestFilm("Film 1", 3), Film.class);
        restTemplate.postForEntity("/films", createTestFilm("Film 2", 2), Film.class);

        ResponseEntity<Film[]> response = restTemplate.getForEntity(
                "/films/popular?count=3",
                Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Film[] popularFilms = response.getBody();
        assertNotNull(popularFilms);
        assertEquals(3, popularFilms.length);
    }

    @Test
    void getPopularFilmsWhenNoFilms() {
        ResponseEntity<Film[]> response = restTemplate.getForEntity(
                "/films/popular?count=5",
                Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Film[] popularFilms = response.getBody();
        assertNotNull(popularFilms);
        assertEquals(5, popularFilms.length);
    }

    @Test
    void getPopularFilmsWithInvalidCount() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/films/popular?count=-1",
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getPopularFilmsWithoutCountParam() {
        restTemplate.postForEntity("/films", createTestFilm("Film A", 15), Film.class);
        restTemplate.postForEntity("/films", createTestFilm("Film B", 10), Film.class);
        restTemplate.postForEntity("/films", createTestFilm("Film C", 5), Film.class);

        // Запрос без параметра count (значение по умолчанию = 10)
        ResponseEntity<Film[]> response = restTemplate.getForEntity(
                "/films/popular",
                Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Film[] popularFilms = response.getBody();
        assertNotNull(popularFilms);
        assertTrue(popularFilms.length >= 3);
    }
}
