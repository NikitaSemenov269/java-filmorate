package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.EstimationRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Estimation;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ValidationService validationService;
    private final ReviewRepository reviewRepository;
    private final EstimationRepository estimationRepository;

    public Review addReview(Review review) {
        log.info("Попытка добавления отзыва");
        validationService.validateReview(review);
        return reviewRepository.addReview(review);
    }

    public Review updateReview(Review review) {
        log.info("Попытка обновления отзыва");
        System.out.println(review.getReviewId());
        System.out.println(review);
        validationService.validateReview(review);
        validationService.validateReviewExists(review.getReviewId());
        return reviewRepository.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
        log.info("Попытка удаления отзыва с ID: {}", reviewId);
        validationService.validateReviewExists(reviewId);
        reviewRepository.deleteReview(reviewId);
        log.info("Отзыв с ID {} удален", reviewId);
    }

    public Review getReviewById(Long reviewId) {
        log.info("Попытка получения отзыва по ID: {}", reviewId);
        validationService.validateReviewExists(reviewId);
        return reviewRepository.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + reviewId + " не найден"));
    }

    public Collection<Review> getPopularReviews(Long filmId, int count) {
        log.info("Попытка получения популярных отзывов по фильму в количестве {} штук", count);
        if (count <= 0) {
            throw new ValidationException("Количество отзывов должно быть положительным числом.");
        }
        return reviewRepository.getPopularReviews(filmId, count);
    }

    public void addLikeReview(Long reviewId, Long userId) {
        log.info("Попытка добавления лайка: пользователь {} ставит лайк отзыву {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        Optional<Estimation> estimation = estimationRepository.getEstimation(reviewId, userId);
        if (estimation.isPresent()) {
            if (estimation.get().getIsLike().equals(Boolean.TRUE)) {
                log.trace("Лайк отзыву {} от пользователя {} уже существует", reviewId, userId);
                return;
            } else {
                log.trace("На отзыве {} стоит дизлайк от пользователя {}. Ставим лайк", reviewId, userId);
                estimationRepository.deleteEstimation(reviewId, userId);
                reviewRepository.addLikeReview(reviewId, userId);

            }
        }
        estimationRepository.addEstimation(reviewId, userId, Boolean.TRUE);
        log.info("Пользователь {} лайкнул отзыв {}", userId, reviewId);
        reviewRepository.addLikeReview(reviewId, userId);
    }

    public void addDislikeReview(Long reviewId, Long userId) {
        log.info("Попытка добавления дизлайка: пользователь {} ставит дизлайк отзыву {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        /*Review review = reviewRepository.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Оценка с review_id " + reviewId + " не найдена"));*/
        Optional<Estimation> estimation = estimationRepository.getEstimation(reviewId, userId);
        if (estimation.isPresent()) {
            if (estimation.get().getIsLike().equals(Boolean.FALSE)) {
                log.trace("Дизайк отзыву {} от пользователя {} уже существует", reviewId, userId);
                return;
            } else {
                log.trace("На отзыве {} стоит лайк от пользователя {}. Ставим дизлайк", reviewId, userId);
                estimationRepository.deleteEstimation(reviewId, userId);
                reviewRepository.addDislikeReview(reviewId, userId);

            }
        }
        estimationRepository.addEstimation(reviewId, userId, Boolean.FALSE);
        log.info("Пользователь {} дизлайкнул отзыв {}", userId, reviewId);
        reviewRepository.addDislikeReview(reviewId, userId);
    }

    public void deleteLikeReview(Long reviewId, Long userId) {
        log.info("Попытка удаления лайка: пользователь {} убирает лайк отзыву {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        Optional<Estimation> estimation = estimationRepository.getEstimation(reviewId, userId);
        if (estimation.isPresent()) {
            estimationRepository.deleteEstimation(reviewId, userId);
            if (estimation.get().getIsLike().equals(Boolean.TRUE)) {
                log.trace("Удаляем лайк отзыву {} от пользователя {}", reviewId, userId);
                reviewRepository.addDislikeReview(reviewId, userId);
            }
        }
    }

    public void deleteDislikeReview(Long reviewId, Long userId) {
        log.info("Попытка удаления дизлайка: пользователь {} убирает дизлайк отзыву {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        Optional<Estimation> estimation = estimationRepository.getEstimation(reviewId, userId);
        if (estimation.isPresent()) {
            estimationRepository.deleteEstimation(reviewId, userId);
            if (estimation.get().getIsLike().equals(Boolean.FALSE)) {
                log.trace("Удаляем дизлайк отзыву {} от пользователя {}", reviewId, userId);
                reviewRepository.addLikeReview(reviewId, userId);
            }
        }
    }
}
