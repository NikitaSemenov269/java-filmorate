package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.LikeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ValidationService validationService;
    private final EventService eventService;

    public void addLike(Long filmId, Long userId) {
        log.info("Попытка добавления лайка фильму {} от пользователя {}", filmId, userId);
        validationService.validateFilmExists(filmId);
        validationService.validateUserExists(userId);
        likeRepository.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        eventService.addEvent(userId, filmId, 1L /* лайк */, 2L /* добавление*/);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удаления лайка у фильма {} от пользователя {}", filmId, userId);
        validationService.validateFilmAndUserIds(filmId, userId);
        likeRepository.removeLike(filmId, userId);
        log.info("Пользователь {} убрал лайк у фильма {}", userId, filmId);
        eventService.addEvent(userId, filmId, 1L /* лайк */, 1L /* удаление */);
    }
}
