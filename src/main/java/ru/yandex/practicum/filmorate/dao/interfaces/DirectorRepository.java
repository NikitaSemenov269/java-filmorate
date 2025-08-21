package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Director;


import java.util.Collection;
import java.util.Optional;

public interface DirectorRepository {

    Collection<Director> findAllDirectors();

    Director createDirector(Director director);

    Director updateDirector(Director director);

    Optional<Director> getDirectorById(Long id);

    boolean deleteDirector(Long id);
}
