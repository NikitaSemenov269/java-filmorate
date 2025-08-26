package ru.yandex.practicum.filmorate.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JdbcFilmRepository.class, FilmRowMapper.class, JdbcGenreRepository.class, GenreRowMapper.class, JdbcMpaRepository.class, MpaRatingRowMapper.class, DirectorRowMapper.class, JdbcDirectorRepository.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcFilmRepositoryIntegrationTest {

    @Autowired
    private JdbcFilmRepository filmRepository;

    @Test
    public void testCreateFilm() {
        Film film = Film.builder().name("Test Film").description("Test Description").releaseDate(LocalDate.of(2020, 1, 1)).duration(120).mpa(MpaRating.builder().id(1L).name("G").description("General Audiences").build()).genres(new HashSet<>(Arrays.asList(Genre.builder().id(1L).name("Комедия").build(), Genre.builder().id(2L).name("Драма").build()))).directors(new HashSet<>(Arrays.asList(Director.builder().id(1L).name("Квентин Тарантино").build()))).build();

        Film createdFilm = filmRepository.createFilm(film);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull().isPositive();
        assertThat(createdFilm.getName()).isEqualTo("Test Film");
        assertThat(createdFilm.getDescription()).isEqualTo("Test Description");
        assertThat(createdFilm.getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(createdFilm.getDuration()).isEqualTo(120);
        assertThat(createdFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(createdFilm.getGenres()).hasSize(2);
        assertThat(createdFilm.getDirectors()).hasSize(1);
    }

    @Test
    public void testFindFilmById() {
        Film film = Film.builder().name("Test Film").description("Test Description").releaseDate(LocalDate.of(2020, 1, 1)).duration(120).mpa(MpaRating.builder().id(1L).name("G").description("General Audience").build()).genres(new HashSet<>()).directors(new HashSet<>()).build();

        Film createdFilm = filmRepository.createFilm(film);

        Optional<Film> foundFilm = filmRepository.getFilmById(createdFilm.getId());

        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getId()).isEqualTo(createdFilm.getId());
        assertThat(foundFilm.get().getName()).isEqualTo("Test Film");
        assertThat(foundFilm.get().getDescription()).isEqualTo("Test Description");
        assertThat(foundFilm.get().getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(foundFilm.get().getDuration()).isEqualTo(120);
        assertThat(foundFilm.get().getMpa().getId()).isEqualTo(1L);
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testFindAllFilms() {
        List<Film> films = filmRepository.findAllFilms();

        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getName).containsExactlyInAnyOrder("Test Film 1_B", "Test Film 2_ТА");
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testUpdateFilm() {
        Film film = Film.builder().name("Original Film").description("Original Description").releaseDate(LocalDate.of(2020, 1, 1)).duration(120).mpa(MpaRating.builder().id(1L).name("G").build()).genres(new HashSet<>()).directors(new HashSet<>()).build();
        Film createdFilm = filmRepository.createFilm(film);

        Film updatedFilm = Film.builder().id(createdFilm.getId()).name("Updated Film").description("Updated Description").releaseDate(LocalDate.of(2021, 1, 1)).duration(150).mpa(MpaRating.builder().id(2L).name("PG").build()).genres(new HashSet<>(Collections.singletonList(Genre.builder().id(1L).name("Комедия").build()))).directors(new HashSet<>(Arrays.asList(Director.builder().id(1L).name("Квентин Тарантино").build()))).build();

        Film result = filmRepository.updateFilm(updatedFilm);

        assertThat(result.getId()).isEqualTo(createdFilm.getId());
        assertThat(result.getName()).isEqualTo("Updated Film");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(2021, 1, 1));
        assertThat(result.getDuration()).isEqualTo(150);
        assertThat(result.getMpa().getId()).isEqualTo(2L);
        assertThat(result.getGenres()).hasSize(1);
        assertThat(result.getDirectors()).hasSize(1);
    }

    @Test
    public void testDeleteFilm() {
        Film film = Film.builder().name("Test Film").description("Test Description").releaseDate(LocalDate.of(2020, 1, 1)).duration(120).mpa(MpaRating.builder().id(1L).name("G").build()).genres(new HashSet<>()).directors(new HashSet<>()).build();
        Film createdFilm = filmRepository.createFilm(film);

        boolean deleted = filmRepository.deleteFilm(createdFilm.getId());

        assertThat(deleted).isTrue();

        Optional<Film> foundFilm = filmRepository.getFilmById(createdFilm.getId());
        assertThat(foundFilm).isEmpty();
    }

    @Test
    public void testDeleteNonExistentFilm() {
        boolean deleted = filmRepository.deleteFilm(999L);
        assertThat(deleted).isFalse();
    }

    @Test
    public void testGetPopularFilms() {
        Film film1 = Film.builder().name("Popular Film").description("Popular Description").releaseDate(LocalDate.of(2020, 1, 1)).duration(120).mpa(MpaRating.builder().id(1L).name("G").build()).genres(new HashSet<>()).directors(new HashSet<>()).build();

        Film film2 = Film.builder().name("Less Popular Film").description("Less Popular Description").releaseDate(LocalDate.of(2021, 1, 1)).duration(150).mpa(MpaRating.builder().id(2L).name("PG").build()).genres(new HashSet<>()).directors(new HashSet<>()).build();

        filmRepository.createFilm(film1);
        filmRepository.createFilm(film2);

        Collection<Film> popularFilms = filmRepository.getPopularFilms(2, null, 2999);

        assertThat(popularFilms).hasSize(2);
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testGetResultSearchForFilmsByTitle() {
        List<Film> films = filmRepository.findAllFilms();
        assertThat(films).hasSize(2);

        Film film1 = films.get(0); // name = Test Film 1_B
        Film film2 = films.get(1); // name = Test Film 2_ТА
        String query = "а";
        Collection<Film> filmTest = filmRepository.getResultSearchForFilmsByTitle(query);

        assertThat(filmTest).hasSize(1);
        assertFalse(filmTest.contains(film1));
        assertTrue(filmTest.contains(film2));

    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testGetResultSearchForFilmsByDirector() {
        List<Film> films = filmRepository.findAllFilms();
        assertThat(films).hasSize(2);

        Film film1 = films.get(0); // director = Квентин Тарантино
        Film film2 = films.get(1); // director = Кристофер Нолан

        String query = "Венти";
        Collection<Film> filmTest = filmRepository.getResultSearchForFilmsByDirector(query);

        assertThat(filmTest).hasSize(1);
        assertFalse(filmTest.contains(film2));
        assertTrue(filmTest.contains(film1));
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testGetResultSearchForFilmsByDirectorAndTitle() {
        List<Film> films = filmRepository.findAllFilms();
        assertThat(films).hasSize(2);

        Film film1 = films.get(0); // director = Квентин Тарантино
        Film film2 = films.get(1); // name = Test Film 2_ТА

        String query = "та";
        Collection<Film> filmTest = filmRepository.getResultSearchForFilmsByDirectorAndTitle(query);

        assertThat(filmTest).hasSize(2);
        assertTrue(filmTest.contains(film1));
        assertTrue(filmTest.contains(film2));
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void failTestGetResultSearchForFilmsByDirectorAndTitle() {
        List<Film> films = filmRepository.findAllFilms();
        assertThat(films).hasSize(2);

        Film film1 = films.get(0); // director = Квентин Тарантино
        Film film2 = films.get(1); // name = Test Film 2_ТА

        String query = "fail";
        Collection<Film> filmTest = filmRepository.getResultSearchForFilmsByDirectorAndTitle(query);
        assertThat(filmTest).hasSize(0);
    }

    public void testGetPopularFilmsByGenre() {
        Film film1 = Film.builder()
                .name("Popular Film")
                .description("Popular Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(MpaRating.builder().id(1L).name("G").build())
                .genres(new HashSet<>())
                .build();
        Genre genre = new Genre(3L, "Мультфильм");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        Film film2 = Film.builder()
                .name("Less Popular Film")
                .description("Less Popular Description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .mpa(MpaRating.builder().id(2L).name("PG").build())
                .genres(genres)
                .build();

        filmRepository.createFilm(film1);
        filmRepository.createFilm(film2);

        Collection<Film> popularFilms = filmRepository.getPopularFilms(1, genre.getId(), 2999);
        System.out.println(popularFilms);
        assertThat(popularFilms).hasSize(1);
    }

    @Test
    public void testGetPopularFilmsByGenreAndYear() {
        Film film1 = Film.builder()
                .name("Popular Film")
                .description("Popular Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(MpaRating.builder().id(1L).name("G").build())
                .genres(new HashSet<>())
                .build();
        Genre genre = new Genre(3L, "Мультфильм");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        Film film2 = Film.builder()
                .name("Less Popular Film")
                .description("Less Popular Description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .mpa(MpaRating.builder().id(2L).name("PG").build())
                .genres(genres)
                .build();

        filmRepository.createFilm(film1);
        filmRepository.createFilm(film2);

        Collection<Film> popularFilms = filmRepository.getPopularFilms(1, genre.getId(), 2021);
        System.out.println(popularFilms);
        assertThat(popularFilms).hasSize(1);
    }

    @Test
    public void testGetPopularFilmsByYear() {
        Film film1 = Film.builder()
                .name("Popular Film")
                .description("Popular Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(MpaRating.builder().id(1L).name("G").build())
                .genres(new HashSet<>())
                .build();
        Film film2 = Film.builder()
                .name("Less Popular Film")
                .description("Less Popular Description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .mpa(MpaRating.builder().id(2L).name("PG").build())
                .genres(new HashSet<>())
                .build();

        filmRepository.createFilm(film1);
        filmRepository.createFilm(film2);

        Collection<Film> popularFilms = filmRepository.getPopularFilms(1, null, 2021);
        System.out.println(popularFilms);
        assertThat(popularFilms).hasSize(1);
    }
}

