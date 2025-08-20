package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.EventRepository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private EventRepository eventRepository;

    protected void addEvent(Long userId, Long entityId, Long typeId, Long operationId) {
        log.info("Попытка добавления записи в ленту событий пользователя: {}", userId);
        eventRepository.addEvent(userId,entityId, typeId, operationId);
        log.info("Запись успешно добавлена.");
    }

    public Collection<Event> getEventList(Long userId) {
        log.info("Попытка получения ленты событий пользователя: {}", userId);
        return eventRepository.getEventListByUserId(userId);
    }
}
