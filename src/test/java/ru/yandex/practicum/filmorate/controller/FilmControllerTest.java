package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FilmControllerTest {

    @Mock
    private FilmService filmService;
    @Mock
    private LikeService likeService;

    @InjectMocks
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllFilms() {
        Film film1 = mock(Film.class);
        Film film2 = mock(Film.class);
        Collection<Film> films = Arrays.asList(film1, film2);

        when(filmService.findAllFilms()).thenReturn(films);

        Collection<Film> result = filmController.findAllFilms();

        assertEquals(2, result.size());
        verify(filmService, times(1)).findAllFilms();
    }

    @Test
    public void testGetPopulateFilms() {
        int count = 10;
        Film film1 = mock(Film.class);
        Film film2 = mock(Film.class);
        Collection<Film> popularFilms = Arrays.asList(film1, film2);

        when(filmService.getTopRatedMovies(count, null, 2999)).thenReturn(popularFilms);

        Collection<Film> result = filmController.getPopularFilms(count, null, 2999);

        assertEquals(2, result.size());
        verify(filmService, times(1)).getTopRatedMovies(count, null, 2999);
    }

    @Test
    public void testGetPopulateFilmsByYear() {
        int count = 10;
        Film film1 = mock(Film.class);
        Film film2 = mock(Film.class);
        Collection<Film> popularFilms = Arrays.asList(film1, film2);

        when(filmService.getTopRatedMovies(count, null, 2010)).thenReturn(popularFilms);

        Collection<Film> result = filmController.getPopularFilms(count, null, 2010);

        assertEquals(2, result.size());
        verify(filmService, times(1)).getTopRatedMovies(count, null, 2010);
    }

    @Test
    public void testGetPopulateFilmsByGenre() {
        int count = 10;
        Film film1 = mock(Film.class);
        Film film2 = mock(Film.class);
        Collection<Film> popularFilms = Arrays.asList(film1, film2);

        when(filmService.getTopRatedMovies(count, 3L, 2999)).thenReturn(popularFilms);

        Collection<Film> result = filmController.getPopularFilms(count, 3L, 2999);

        assertEquals(2, result.size());
        verify(filmService, times(1)).getTopRatedMovies(count, 3L, 2999);
    }

    @Test
    public void testGetPopulateFilmsByGenreAndYear() {
        int count = 10;
        Film film1 = mock(Film.class);
        Film film2 = mock(Film.class);
        Collection<Film> popularFilms = Arrays.asList(film1, film2);

        when(filmService.getTopRatedMovies(count, 3L, 2021)).thenReturn(popularFilms);

        Collection<Film> result = filmController.getPopularFilms(count, 3L, 2021);

        assertEquals(2, result.size());
        verify(filmService, times(1)).getTopRatedMovies(count, 3L, 2021);
    }

    @Test
    public void testCreateFilm() {
        Film film = mock(Film.class);

        when(filmService.createFilm(film)).thenReturn(film);

        Film result = filmController.createFilm(film);

        assertEquals(film, result);
        verify(filmService, times(1)).createFilm(film);
    }

    @Test
    public void testUpdateFilm() {
        Film newFilm = mock(Film.class);

        when(filmService.updateFilm(newFilm)).thenReturn(newFilm);

        Film result = filmController.updateFilm(newFilm);

        assertEquals(newFilm, result);
        verify(filmService, times(1)).updateFilm(newFilm);
    }

    @Test
    public void testAddLike() {
        Long filmId = 1L;
        Long userId = 2L;

        filmController.addLikeForFilm(filmId, userId);

        verify(likeService, times(1)).addLike(filmId, userId);
    }

    @Test
    public void testRemoveLike() {
        Long filmId = 1L;
        Long userId = 2L;

        filmController.removeLikeForFilm(filmId, userId);

        verify(likeService, times(1)).removeLike(filmId, userId);
    }
}
