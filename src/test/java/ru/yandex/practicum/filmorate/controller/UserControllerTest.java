package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private FriendService friendService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllUsers() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        Collection<User> users = Arrays.asList(user1, user2);

        when(userService.findAllUsers()).thenReturn(users);

        Collection<User> result = userController.findAllUsers();

        assertEquals(2, result.size());
        verify(userService, times(1)).findAllUsers();
    }

    @Test
    public void testGetFriends() {
        Long friendId = (Long) 1L;
        User friend1 = mock(User.class);
        User friend2 = mock(User.class);
        Collection<User> friends = Arrays.asList(friend1, friend2);

        when(friendService.getFriends(friendId)).thenReturn(friends);

        Collection<User> result = userController.getFriends(friendId);

        assertEquals(2, result.size());
        verify(friendService, times(1)).getFriends(friendId);
    }

    @Test
    public void testGetCommonFriends() {
        Long friendId = (Long) 1L;
        Long userId = (Long) 2L;
        User commonFriend = mock(User.class);
        Collection<User> commonFriends = Arrays.asList(commonFriend);

        when(friendService.getCommonFriends(friendId, userId)).thenReturn(commonFriends);

        Collection<User> result = userController.getCommonFriends(friendId, userId);

        assertEquals(1, result.size());
        verify(friendService, times(1)).getCommonFriends(friendId, userId);
    }

    @Test
    public void testCreateUser() {
        User user = mock(User.class);

        when(userService.createUser(user)).thenReturn(user);

        User result = userController.createUser(user);

        assertEquals(user, result);
        verify(userService, times(1)).createUser(user);
    }

    @Test
    public void testUpdateUser() {
        User user = mock(User.class);

        when(userService.updateUser(user)).thenReturn(user);

        User result = userController.updateUser(user);

        assertEquals(user, result);
        verify(userService, times(1)).updateUser(user);
    }

    @Test
    public void testAddFriend() {
        Long userId = (Long) 1L;
        Long friendId = (Long) 2L;

        userController.addFriend(userId, friendId);

        verify(friendService, times(1)).addFriend(userId, friendId);
    }

    @Test
    public void testRemoveFriend() {
        Long userId = (Long) 1L;
        Long friendId = (Long) 2L;

        userController.removeFriend(userId, friendId);

        verify(friendService, times(1)).removeFriend(userId, friendId);
    }
}

