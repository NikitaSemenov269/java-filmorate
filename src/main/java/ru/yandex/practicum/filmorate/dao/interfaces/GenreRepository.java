package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {

    Optional<Genre> findGenreById(Long genreId);

    List<Genre> findAllGenres();

    Set<Genre> findGenreByFilmId(Long filmId);
}
