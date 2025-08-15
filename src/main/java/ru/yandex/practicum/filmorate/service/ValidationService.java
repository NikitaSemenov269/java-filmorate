package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.UserRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.MpaRepository;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    public void validateUserExists(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    public void validateUsersExist(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new ValidationException("ID пользователей не могут быть null");
        }
        validateUserExists(userId1);
        validateUserExists(userId2);
    }

    public void validateFilmAndUserIds(Long filmId, Long userId) {
        if (filmId == null) {
            throw new ValidationException("ID фильма не может быть null");
        }
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
    }

    public void validateFilmExists(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("ID фильма не может быть null");
        }
        filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    public void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Фильм не может быть null.");
        }
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("Фильм должен иметь рейтинг MPA");
        }
        validateMpaExists(film.getMpa().getId());
        if (film.getGenres() != null) {
            for (var genre : film.getGenres()) {
                if (genre.getId() == null) {
                    throw new ValidationException("Жанр должен иметь ID.");
                }
                validateGenreExists(genre.getId());
            }
        }
    }

    public void validateGenreExists(Long genreId) {
        if (genreId == null) {
            throw new ValidationException("ID жанра не может быть null");
        }
        genreRepository.findGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр с ID " + genreId + " не найден"));
    }

    public void validateMpaExists(Long mpaId) {
        if (mpaId == null) {
            throw new ValidationException("ID рейтинга не может быть null");
        }
        mpaRepository.findMpaById(mpaId)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с ID " + mpaId + " не найден"));
    }
}