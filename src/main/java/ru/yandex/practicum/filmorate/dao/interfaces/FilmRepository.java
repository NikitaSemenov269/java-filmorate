package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {

    Collection<Film> findAllFilms();

    Collection<Film> getPopularFilms(int count);

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    Optional<Film> getFilmById(Long id);

    boolean deleteFilm(Long id);
}
