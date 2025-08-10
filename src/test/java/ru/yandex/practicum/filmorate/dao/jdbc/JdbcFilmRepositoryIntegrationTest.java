package ru.yandex.practicum.filmorate.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        JdbcFilmRepository.class,
        FilmRowMapper.class,
        JdbcGenreRepository.class,
        GenreRowMapper.class,
        JdbcMpaRepository.class,
        MpaRatingRowMapper.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcFilmRepositoryIntegrationTest {

    @Autowired
    private JdbcFilmRepository filmRepository;

    @Test
    public void testCreateFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(MpaRating.builder().id(1L).name("G").description("General Audiences").build())
                .genres(new HashSet<>(Arrays.asList(
                        Genre.builder().id(1L).name("Комедия").build(),
                        Genre.builder().id(2L).name("Драма").build()
                )))
                .build();

        Film createdFilm = filmRepository.createFilm(film);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull().isPositive();
        assertThat(createdFilm.getName()).isEqualTo("Test Film");
        assertThat(createdFilm.getDescription()).isEqualTo("Test Description");
        assertThat(createdFilm.getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(createdFilm.getDuration()).isEqualTo(120);
        assertThat(createdFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(createdFilm.getGenres()).hasSize(2);
    }

    @Test
    public void testFindFilmById() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(MpaRating.builder().id(1L).name("G").description("General Audience").build())
                .genres(new HashSet<>())
                .build();

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
    public void testFindAllFilms() {
        List<Film> films = filmRepository.findAllFilms();

        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getName)
                .containsExactlyInAnyOrder("Test Film 1", "Test Film 2");
    }

    @Test
    public void testUpdateFilm() {
        Film film = Film.builder()
                .name("Original Film")
                .description("Original Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(MpaRating.builder().id(1L).name("G").build())
                .genres(new HashSet<>())
                .build();
        Film createdFilm = filmRepository.createFilm(film);

        Film updatedFilm = Film.builder()
                .id(createdFilm.getId())
                .name("Updated Film")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .mpa(MpaRating.builder().id(2L).name("PG").build())
                .genres(new HashSet<>(Collections.singletonList(
                        Genre.builder().id(1L).name("Комедия").build()
                )))
                .build();

        Film result = filmRepository.updateFilm(updatedFilm);

        assertThat(result.getId()).isEqualTo(createdFilm.getId());
        assertThat(result.getName()).isEqualTo("Updated Film");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(2021, 1, 1));
        assertThat(result.getDuration()).isEqualTo(150);
        assertThat(result.getMpa().getId()).isEqualTo(2L);
        assertThat(result.getGenres()).hasSize(1);
    }

    @Test
    public void testDeleteFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(MpaRating.builder().id(1L).name("G").build())
                .genres(new HashSet<>())
                .build();
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

        Collection<Film> popularFilms = filmRepository.getPopularFilms(2);

        assertThat(popularFilms).hasSize(2);
        // Проверяем, что фильмы возвращаются (в данном случае без лайков,
        // порядок может быть произвольным)
    }
}
