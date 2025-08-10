package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.dao.interfaces.MpaRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRepository mpaRepository;

    public MpaRating findMpaById(Long mpaId) {
        return mpaRepository.findMpaById(mpaId)
                .orElseThrow(() -> new NotFoundException("Рейтинг с таким ID: " + mpaId + " не найден."));
    }
    public List<MpaRating> findAllMpa() {
        return mpaRepository.findAllMpa();
    }
}
