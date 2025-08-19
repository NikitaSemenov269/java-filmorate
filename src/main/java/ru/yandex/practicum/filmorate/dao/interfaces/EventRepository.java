package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventRepository {

    void addEvent(Long userId, Event event);

    Collection<Event> getLastEvents(int count);

    Collection<Event> getAllEvent();

    Collection<Event> getEventByUserId(Long userId);
}
