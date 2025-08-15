package ru.yandex.practicum.filmorate.dao.interfaces;

public interface LikeRepository {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}
