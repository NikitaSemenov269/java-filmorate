package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventRepository {

    void addEvent(Long userId, Long entityId, Long typeId, Long operationId);

    Collection<Event> getEventListByUserId(Long userId);
}
