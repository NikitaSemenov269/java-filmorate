package ru.yandex.practicum.filmorate.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JdbcGenreRepository.class, GenreRowMapper.class}) // Импортируем необходимые бины
@DirtiesContext // Очищаем контекст после каждого теста
public class JdbcGenreRepositoryIntegrationTest {

    @Autowired
    private JdbcGenreRepository genreRepository;

    @Test
    public void testFindGenreById() {
        Long genreId = 1L; // Предполагается, что жанр с id=1 уже существует в БД

        Optional<Genre> genreOptional = genreRepository.findGenreById(genreId);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", genreId)
                );
    }

    @Test
    public void testFindAllGenres() {
        List<Genre> genres = genreRepository.findAllGenres();

        assertThat(genres).isNotEmpty(); // Проверяем, что список не пустой
    }

    @Test
    public void testFindGenreByFilmId() {
        Long filmId = 1L; // Предполагается, что фильм с id=1 уже существует в БД и имеет жанры

        Set<Genre> filmGenres = genreRepository.findGenreByFilmId(filmId);

        assertThat(filmGenres).isNotEmpty(); // Проверяем, что множество жанров не пустое
    }
}
