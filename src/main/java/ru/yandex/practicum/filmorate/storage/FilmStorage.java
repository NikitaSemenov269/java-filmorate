package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    public Film createFilm(Film newFilm);

    public Film updateFilm(Film updatingFilm);

    public void deleteFilm(Integer id);

    public Film getFilmForId(Integer id);

    public Collection<Film> findAll();
}