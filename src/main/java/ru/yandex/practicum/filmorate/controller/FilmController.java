package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film create(@RequestBody @Valid Film newFilm) {
        return filmService.createFilm(newFilm);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film updatingFilm) {
        log.info("Попытка обновления данных фильма: {}", updatingFilm.getId());
        if (updatingFilm.getId() == null || updatingFilm.getId() <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }
        return filmService.updateFilm(updatingFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id,
                        @PathVariable Integer userId) {
        log.info("Попытка добавления лайка от: {} для фильма: {}", userId, id);
        if (id == null || id <= 0 || userId == null || userId <= 0) {
            throw new ValidationException("Некорректное значение id.");
        }
        return filmService.addLike(id, userId);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Попытка получения списка всех фильмов.");
        return filmService.findAll();
    }

    @GetMapping("/popular")
    public Collection<Film> getTopRatedMovies(@RequestParam(value = "count", defaultValue = "10")
                                              @Min(value = 1, message = "Количество фильмов в списке" +
                                                      " должно быть положительным числом.") Integer count) {
        log.info("Попытка получения списка популярных фильмов.");
        return filmService.getTopRatedMovies(count);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Попытка удаления фильма: {}", id);
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректный формат id.");
        }
        filmService.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        log.info("Попытка удаления лайка от: {} для фильма: {}", userId, id);
        if (userId == null || userId <= 0 || id == null || id <= 0) {
            throw new ValidationException("Некорректный формат id.");
        }
        return filmService.deleteLike(id, userId);
    }
}
