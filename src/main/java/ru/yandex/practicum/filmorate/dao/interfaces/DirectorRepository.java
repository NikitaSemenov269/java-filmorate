package ru.yandex.practicum.filmorate.dao.interfaces;

import jakarta.validation.constraints.Min;
import ru.yandex.practicum.filmorate.model.Director;


import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface DirectorRepository {

    Collection<Director> findAllDirectors();

    Director createDirector(Director director);

    Director updateDirector(Director director);

    Optional<Director> getDirectorById(Long id);

    boolean deleteDirector(Long id);

    Set<Director> findDirectorByFilmId(Long id);
}
