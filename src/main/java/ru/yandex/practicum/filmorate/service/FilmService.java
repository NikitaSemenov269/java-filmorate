package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final ValidationService validationService;
    private final FilmRepository filmRepository;

    public Collection<Film> findAllFilms() {
        log.info("Попытка получения всех фильмов");
        return filmRepository.findAllFilms();
    }

    public Film getFilmById(Long filmId) {
        log.info("Попытка получения фильма по ID: {}", filmId);
        validationService.validateFilmExists(filmId);
        return filmRepository.getFilmById(filmId).orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    public Film createFilm(Film film) {
        log.info("Попытка создания фильма: {}", film.getName());
        validationService.validateFilm(film);
        Film createdFilm = filmRepository.createFilm(film);
        log.info("Создан фильм с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film newFilm) {
        log.info("Попытка обновления фильма с ID: {}", newFilm.getId());
        validationService.validateFilm(newFilm);
        Film updatedFilm = filmRepository.updateFilm(newFilm);
        log.info("Фильм с ID {} обновлен", newFilm.getId());
        return updatedFilm;
    }

    public void deleteFilm(Long id) {
        log.info("Попытка удаления фильма с ID: {}", id);
        validationService.validateFilmExists(id);
        filmRepository.deleteFilm(id);
        log.info("Фильм с ID {} удален", id);
    }

    public Collection<Film> getTopRatedMovies(int count, Long genreId, int year) {
        log.info("Попытка получения популярных фильмов в количестве {} штук", count);
        if (count <= 0) {
            throw new ValidationException("Количество фильмов должно быть положительным числом.");
        }

        if (year <= 0) {
            throw new ValidationException("Год фильма должен быть положительным числом.");
        }

        if (genreId != null) {
            validationService.validateGenreExists(genreId);
        }
        return filmRepository.getPopularFilms(count, genreId, year);
    }

    public Collection<Film> getSortedFilmsByDirector(Long directorId, String sortBy) {
        log.info("Попытка получения списка фильмов режиссера с ID: {}", directorId);
        validationService.validateDirectorExists(directorId);
        if (sortBy.equalsIgnoreCase("year")) {
            return filmRepository.getDirectorFilmsSortedByYear(directorId);
        } else if (sortBy.equalsIgnoreCase("likes")) {
            return filmRepository.getDirectorFilmsSortedByLikes(directorId);
        } else {
            throw new ValidationException("В SortBy передан неизвестный параметр");
        }
    }

    public Collection<Film> getTopRatedMoviesAmongFriends(Long userId, Long friendId) {
        log.info("проверка что пользователи с ID: {} и {} являются друзьями", userId, friendId);
        return filmRepository.getCommonFilmsWithFriend(userId, friendId);
    }

    public Collection<Film> getResultSearchForFilms(String query, String by) {
        if (by == null && query == null) {
            log.info("Попытка получения списка всех фильмов отсортированных по популярности.");
            return filmRepository.getPopularFilms(findAllFilms().size(), null, 0);
        }
        return switch (by) {
            case "title" -> {
                log.info("Попытка получения списка фильмов отсортированных по названию.");
                yield filmRepository.getResultSearchForFilmsByTitle(query);
            }
            case "director" -> {
                log.info("Попытка получения списка фильмов отсортированных по режиссерам.");
                yield filmRepository.getResultSearchForFilmsByDirector(query);
            }
            case "title,director", "director,title" -> {
                log.info("Попытка получения списка фильмов отсортированных по режиссерам и названию.");
                yield filmRepository.getResultSearchForFilmsByDirectorAndTitle(query);
            }
            default -> throw new IllegalArgumentException("Неверный параметр поиска");
        };
    }
}