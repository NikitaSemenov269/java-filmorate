package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(Integer idFilm, Integer idUser) {
        log.info("Попытка добавления лайка от: {} для фильма: {}", idUser, idFilm);
        if (idFilm == null || idFilm <= 0 || idUser == null || idUser <= 0) {
            throw new ValidationException("Некорректный формат id.");
        }
        Film film = filmStorage.getFilmForId(idFilm);
        //Проверка существования пользователя
        User user = userStorage.getUserForId(idUser);
        film.addLike(idUser);
        return film;
    }

    public Film deleteLike(Integer idFilm, Integer idUser) {
        log.info("Попытка удаления лайка от: {} для фильма: {}", idUser, idFilm);
        if (idFilm == null || idFilm <= 0 || idUser == null || idUser <= 0) {
            throw new ValidationException("Некорректный формат id.");
        }
        Film film = filmStorage.getFilmForId(idFilm);
        //Проверка существования пользователя
        User user = userStorage.getUserForId(idUser);
        film.deleteLike(idUser);
        return film;
    }

    public Collection<Film> getTopRatedMovies(Integer count) {
        log.info("Попытка получения списка рейтинга фильмов в количестве: {}", count);
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(Film::getNumberOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
