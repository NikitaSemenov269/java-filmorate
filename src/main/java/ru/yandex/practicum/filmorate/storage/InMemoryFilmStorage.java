package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void addFilm(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
    }

    @Override
    public Film updateFilm(Film updatingFilm) {
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
        return films.values();
    }

    @Override
    public Film getFilmForId(Integer id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id: " + id + " не найден.");
        }
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        films.remove(id);
        log.info("Фильм удален: {}", id);
    }
}
