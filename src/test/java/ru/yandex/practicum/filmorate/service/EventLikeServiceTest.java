package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dao.interfaces.LikeRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventLikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private EventService eventService;
    @Mock
    private ValidationService validationService; // требуется для работы тестов.


    @InjectMocks
    private LikeService likeService;

    @Test
    void testAddLike_CreatesEvent() {
        Long filmId = 1L;
        Long userId = 2L;

        doNothing().when(likeRepository).addLike(filmId, userId);
        likeService.addLike(filmId, userId);

        verify(likeRepository, times(1)).addLike(filmId, userId);
        verify(eventService, times(1)).addEvent(userId, filmId, 1L, 2L);
    }
}
