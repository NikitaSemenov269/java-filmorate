package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    //FilmStorage
    //POST
    @PostMapping
    public Film create(@RequestBody @Valid Film newFilm) {
        return filmStorage.createFilm(newFilm);
    }

    //PUT
    @PutMapping
    public Film update(@RequestBody @Valid Film updatingFilm) {
        return filmStorage.updateFilm(updatingFilm);
    }

    //GET
    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    //DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        filmStorage.deleteFilm(id);
    }

    //FilmService
    //PUT
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id,
                        @PathVariable Integer userId) {
        return filmService.addLike(id, userId);
    }

    //GET
    @GetMapping("/popular")
    public Collection<Film> getTopRatedMovies(@RequestParam(value = "count", defaultValue = "10")
                                              @Min(value = 1, message = "Количество фильмов в списке" +
                                                      " должно быть положительным числом.") Integer count) {
        return filmService.getTopRatedMovies(count);
    }

    //DELETE
    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        return filmService.deleteLike(id, userId);
    }
}
