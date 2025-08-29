package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dao.interfaces.EventRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.FriendRepository;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventFriendServiceTest {

    @Mock
    private FriendRepository friendRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ValidationService validationService; // требуется для работы тестов.

    @InjectMocks
    private FriendService friendService;

    @Test
    void testEventAddFriend() {
        Long userId = 1L;
        Long friendId = 2L;

        doNothing().when(friendRepository).addFriend(userId, friendId);
        friendService.addFriend(userId, friendId);

        verify(friendRepository, times(1)).addFriend(userId, friendId);
        verify(eventRepository, times(1)).addEvent(userId, friendId, 3L, 2L);
    }

    @Test
    void testEventRemoveFriend() {
        Long userId = 1L;
        Long friendId = 2L;

        doNothing().when(friendRepository).removeFriend(userId, friendId);
        friendService.removeFriend(userId, friendId);

        verify(friendRepository, times(1)).removeFriend(userId, friendId);
        verify(eventRepository, times(1)).addEvent(userId, friendId, 3L, 1L);
    }
}
