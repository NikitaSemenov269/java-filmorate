package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public Film createFilm(Film newFilm) {
        checkReleaseDateFilm(newFilm);
        checkDuplicateNameFilm(newFilm);
        newFilm.setId(getNextId());
        filmStorage.addFilm(newFilm);
        log.info("Добавлен новой фильм: {}", newFilm.getId());
        return newFilm;
    }

    public Film updateFilm(Film updatingFilm) {
        if (filmStorage.getFilmForId(updatingFilm.getId()) == null) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        return filmStorage.updateFilm(updatingFilm);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmForId(Integer id) {
        if (filmStorage.getFilmForId(id) == null) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        return getFilmForId(id);
    }

    public void deleteFilm(Integer id) {
        if (filmStorage.getFilmForId(id) == null) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        filmStorage.deleteFilm(id);
    }

    public Film addLike(Integer idFilm, Integer idUser) {
        if (filmStorage.getFilmForId(idFilm) == null) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        if (userStorage.getUserForId(idUser) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        Film film = filmStorage.getFilmForId(idFilm);
        film.getIdUsersWhoLiked().add(idUser);
        return film;
    }

    public Film deleteLike(Integer idFilm, Integer idUser) {
        if (filmStorage.getFilmForId(idFilm) == null) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        if (userStorage.getUserForId(idUser) == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        Film film = filmStorage.getFilmForId(idFilm);
        film.getIdUsersWhoLiked().remove(idUser);
        return film;
    }

    public Collection<Film> getTopRatedMovies(Integer count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> {
                    int likes1 = f1.getIdUsersWhoLiked().size();
                    int likes2 = f2.getIdUsersWhoLiked().size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkDuplicateNameFilm(Film newFilm) {
        if (filmStorage.findAll().stream().anyMatch(film -> newFilm.getName().equalsIgnoreCase(film.getName()))) {
            throw new DuplicatedDataException("Фильм с таким названием уже существует");
        }
    }

    private void checkReleaseDateFilm(Film newFilm) {
        if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Некорректная дата релиза {}", newFilm.getReleaseDate());
            throw new ClassCastException("Дата релиза фильма должна быть не ранее 28.12.1895.");
        }
    }

    private Integer getNextId() {
        return filmStorage.findAll()
                .stream()
                .mapToInt(Film::getId)
                .max()
                .orElse(0) + 1;
    }
}


