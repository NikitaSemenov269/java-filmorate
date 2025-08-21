package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAllDirectors() {
        log.info("Запрос на получение всех режиссеров");
        return directorService.findAllDirectors();
    }

    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable Long id) {
        log.info("Запрос на получение режиссера с ID: {}", id);
        return directorService.findDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на создание нового режиссера: {}", director);
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на обновление данных режиссера: {}", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        log.info("Запрос на удаление режиссера с ID: {}", id);
        directorService.deleteDirector(id);
    }
}
