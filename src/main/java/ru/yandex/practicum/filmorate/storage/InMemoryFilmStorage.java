package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film newFilm) {
        log.info("Попытка создания нового фильма: {}", newFilm.getName());
        checkReleaseDateFilm(newFilm);
        checkDuplicateNameFilm(newFilm);
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film updatingFilm) {
        log.info("Попытка обновления данных фильма: {}.", updatingFilm);
        if (updatingFilm.getId() == null || updatingFilm.getId() <= 0) {
            throw new ValidationException("Некорректное значение id пользователя.");
        }
        validateFilmId(updatingFilm);
        checkDuplicateNameFilm(updatingFilm);
        checkReleaseDateFilm(updatingFilm);
        Film film = films.get(updatingFilm.getId());
        Optional.ofNullable(updatingFilm.getReleaseDate()).ifPresent(film::setReleaseDate);
        Optional.ofNullable(updatingFilm.getName()).ifPresent(film::setName);
        Optional.ofNullable(updatingFilm.getDescription()).ifPresent(film::setDescription);
        Optional.ofNullable(updatingFilm.getDuration()).ifPresent(film::setDuration);
        log.info("Успешное обновление данных фильма: {}", film.getId());
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Попытка получения всех фильмов в количестве: {}", films.size());
        return films.values();
    }

    @Override
    public Film getFilmForId(Integer id) {
        log.info("Попытка получить фильм: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное значение id пользователя.");
        }
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id: " + id + " не найден.");
        }
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        log.info("Попытка удалить фильм: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное значение id пользователя.");
        }
        validateFilmId(films.get(id));
        films.remove(id);
        log.info("Фильм удален: {}", id);
    }

    private void validateFilmId(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id: " + newFilm.getId() + " не найден.");
        }
    }

    private void checkDuplicateNameFilm(Film newFilm) {
        if (films.values().stream().anyMatch(film -> newFilm.getName().equalsIgnoreCase(film.getName()))) {
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
        return films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
