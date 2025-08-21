package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final ValidationService validationService;
    private final DirectorRepository directorRepository;

    public Collection<Director> findAllDirectors() {
        log.info("Попытка получения всех режиссёров");
        return directorRepository.findAllDirectors();
    }

    public Director getDirectorById(Long directorId) {
        log.info("Попытка получения режиссёра по ID: {}", directorId);
        validationService.validateDirectorExists(directorId);
        return directorRepository.getDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссёр с ID " + directorId + " не найден"));
    }

    public Director createDirector(Director director) {
        log.info("Попытка создания режиссёра: {}", director.getName());
        Director createdDirector = directorRepository.createDirector(director);
        log.info("Создан режиссёр с ID: {}", createdDirector.getId());
        return createdDirector;
    }

    public Director updateDirector(Director newDirector) {
        log.info("Попытка обновления режиссёра с ID: {}", newDirector.getId());
        validationService.validateDirectorExists(newDirector.getId());
        Director updatedDirector = directorRepository.updateDirector(newDirector);
        log.info("Режиссёр с ID {} обновлён", newDirector.getId());
        return updatedDirector;
    }

    public void deleteDirector(Long id) {
        log.info("Попытка удаления режиссёра с ID: {}", id);
        validationService.validateDirectorExists(id);
        directorRepository.deleteDirector(id);
        log.info("Режиссёр с ID {} удалён", id);
    }
}
