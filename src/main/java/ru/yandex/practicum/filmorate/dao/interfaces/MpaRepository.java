package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaRepository {

    Optional<MpaRating> findMpaById(Long mpaId);

    List<MpaRating> findAllMpa();
}
