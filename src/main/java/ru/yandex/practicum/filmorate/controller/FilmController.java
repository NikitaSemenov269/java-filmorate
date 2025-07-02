package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Создание нового фильма: {}", film.getName());
        try {
            if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                log.error("Некорректная дата релиза {}", film.getReleaseDate());
                throw new ClassCastException("Дата релиза фильма должна быть не ранее 28.12.1895.");
            }
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Успешное создание фильма: {}", film.getId());
            return film;
        } catch (RuntimeException e) {
            log.error("Ошибка при попытке создания фильма.");
            throw e;
        }
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Обновление данных фильма.");
        try {
            if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
                log.error("Фильма с таким id не существует {}", newFilm.getId());
                throw new NotFoundException("Фильм с id: " + newFilm.getId() + " не найден.");
            }
            if (films.values().stream().anyMatch(film -> film.getName().equalsIgnoreCase(newFilm.getName()))) {
                log.error("Такое название уже существует: {}", newFilm.getName());
                throw new DuplicatedDataException("Фильм с таким названием уже существует");
            }
            Film film = films.get(newFilm.getId());
            Optional.ofNullable(newFilm.getReleaseDate()).ifPresent(film::setReleaseDate);
            if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                log.error("Некорректная дата релиза {}", film.getReleaseDate());
                throw new ValidationException("Дата релиза фильма должна быть не ранее 28.12.1895.");
            }
            Optional.ofNullable(newFilm.getName()).ifPresent(film::setName);
            Optional.ofNullable(newFilm.getDescription()).ifPresent(film::setDescription);
            Optional.ofNullable(newFilm.getDuration()).ifPresent(film::setDuration);
            log.info("Успешное обновление данных фильма: {}", film.getId());
            return film;
        } catch (RuntimeException e) {
            log.error("Ошибка при попытке обновления фильма.");
            throw e;
        }
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получение всех фильмов в количестве: {}", films.size());
        try {
            return films.values();
        } catch (RuntimeException e) {
            log.error("Ошибка при попытке получения списка всех фильмов.");
            throw e;
        }
    }

    private Integer getNextId() {
        return films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
