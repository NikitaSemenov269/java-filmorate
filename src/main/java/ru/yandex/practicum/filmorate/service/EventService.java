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

    private final EventRepository eventRepository;
    private final ValidationService validationService;

    public void addEvent(Long userId, Long entityId, Long typeId, Long operationId) {
        log.info("Попытка добавления записи в ленту событий пользователя: {}", userId);
        validationService.validateUserExists(userId);
        eventRepository.addEvent(userId, entityId, typeId, operationId);
    }

    public Collection<Event> getEventList(Long userId) {
        log.info("Попытка получения ленты событий друзей пользователя: {}", userId);
        validationService.validateUserExists(userId);
        return eventRepository.getEventListByUserId(userId);
    }
}
