package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DirectorControllerTest {

    @Mock
    private DirectorService directorService;

    @InjectMocks
    private DirectorController directorController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllDirectors() {
        Director director1 = mock(Director.class);
        Director director2 = mock(Director.class);
        Collection<Director> directors = Arrays.asList(director1, director2);

        when(directorService.findAllDirectors()).thenReturn(directors);

        Collection<Director> result = directorController.findAllDirectors();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(director1, director2);
        verify(directorService, times(1)).findAllDirectors();
    }

    @Test
    public void testFindDirectorById() {
        Long directorId = 1L;
        Director director = mock(Director.class);

        when(directorService.getDirectorById(directorId)).thenReturn(director);

        Director result = directorController.findDirectorById(directorId);

        assertThat(result).isEqualTo(director);
        verify(directorService, times(1)).getDirectorById(directorId);
    }

    @Test
    public void testCreateDirector() {
        Director director = mock(Director.class);

        when(directorService.createDirector(director)).thenReturn(director);

        Director result = directorController.createDirector(director);

        assertThat(result).isEqualTo(director);
        verify(directorService, times(1)).createDirector(director);
    }

    @Test
    public void testUpdateDirector() {
        Director director = mock(Director.class);

        when(directorService.updateDirector(director)).thenReturn(director);

        Director result = directorController.updateDirector(director);

        assertThat(result).isEqualTo(director);
        verify(directorService, times(1)).updateDirector(director);
    }

    @Test
    public void testDeleteDirector() {
        Long directorId = 1L;

        directorController.deleteDirector(directorId);

        verify(directorService, times(1)).deleteDirector(directorId);
    }
}
