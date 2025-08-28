package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final LikeService likeService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "100000") int count, @RequestParam(required = false) Long genreId, @RequestParam(required = false, defaultValue = "2999") int year) {
        return filmService.getTopRatedMovies(count, genreId, year);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(
            @RequestParam String query,
            @RequestParam String by) {
        return filmService.getResultSearchForFilms(query, by);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{filmId}/like/{userId}")

    public void addLikeForFilm(@PathVariable Long filmId,
                               @PathVariable Long userId) {
        likeService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLikeForFilm(@PathVariable Long filmId,
                                  @PathVariable Long userId) {
        likeService.removeLike(filmId, userId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Long directorId, @RequestParam(required = false) String sortBy) {
        log.info("Запрос на получение фильмов режиссера ID: {}, сортировка по: {}", directorId, sortBy);
        return filmService.getSortedFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFriendFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Запрос на получение общих фильмов между режиссера друзьями с ID: {},и {}", userId, friendId);
        return filmService.getTopRatedMoviesAmongFriends(userId, friendId);
    }
}
