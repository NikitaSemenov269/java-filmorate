package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Estimation;

import java.util.Optional;

public interface EstimationRepository {

    void addEstimation(Long reviewId, Long userId, Boolean isLike);

    void deleteEstimation(Long reviewId, Long userId);

    Optional<Estimation> getEstimation(Long reviewId, Long userId);
}
