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
    private final EventService eventService;

    public Review addReview(Review review) {
        log.info("Попытка добавления отзыва");
        validationService.validateReview(review);
        Review newReview = reviewRepository.addReview(review);
        log.info("Попытка записи: пользователь {} оставил отзыв {}", review.getUserId(), review.getReviewId());
        eventService.addEvent(review.getUserId(), review.getReviewId(), 2L /* отзыв */, 2L /* добавление*/);
        return newReview;
    }

    public Review updateReview(Review review) {
        log.info("Попытка обновления отзыва");
        System.out.println(review.getReviewId());
        System.out.println(review);
        validationService.validateReview(review);
        validationService.validateReviewExists(review.getReviewId());
        Review newReview = reviewRepository.updateReview(review);
        log.info("Попытка записи: пользователь {} обновил отзыв {}", review.getUserId(), review.getReviewId());
        eventService.addEvent(review.getUserId(), review.getReviewId(), 2L /* отзыв */, 3L /* обновление */);
        return newReview;
    }

    public void deleteReview(Long reviewId) {
        log.info("Попытка удаления отзыва с ID: {}", reviewId);
        validationService.validateReviewExists(reviewId);
        Review review = reviewRepository.getReviewById(reviewId) // вызов метода для получения id пользователя.
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + reviewId + " не найден"));
        reviewRepository.deleteReview(reviewId);
        log.info("Отзыв с ID {} удален", reviewId);
        log.info("Попытка записи: пользователь {} удалил отзыв {}", review.getUserId(), reviewId);
        eventService.addEvent(review.getUserId(), reviewId, 2L /* отзыв */, 1L /* удаление */);
    }

    public Review getReviewById(Long reviewId) {
        log.info("Попытка получения отзыва по ID: {}", reviewId);
        validationService.validateReviewExists(reviewId);
        return reviewRepository.getReviewById(reviewId).orElseThrow(() -> new NotFoundException("Отзыв с ID " + reviewId + " не найден"));
    }

    public Collection<Review> getPopularReviews(Long filmId, int count) {
        log.info("Попытка получения популярных отзывов по фильму в количестве {} штук", count);
        if (count <= 0) {
            throw new ValidationException("Количество отзывов должно быть положительным числом.");
        }
        if (filmId == null) {
            log.info("Получение всех отзывов в количестве: {}", count);
            return reviewRepository.getAllReviews(count);
        } else {
            log.info("Получение отзывов для filmId: {} в количестве: {}", filmId, count);
            return reviewRepository.getPopularReviews(filmId, count);
        }
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
        log.info("Попытка записи: пользователь {} поставил лайк {}", userId, reviewId);
        eventService.addEvent(userId, reviewId, 1L /* лайк */, 2L /* добавление*/);
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
        log.info("Попытка записи: пользователь {} ставит дизлайк {}", userId, reviewId);
        eventService.addEvent(userId, reviewId, 2L /* дизлайк */, 2L /* добавление*/);
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
                log.info("Попытка записи: пользователь {} удалил лайк {}", userId, reviewId);
                eventService.addEvent(userId, reviewId, 1L /* лайк */, 1L /* удаление */);
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
                log.info("Попытка записи: пользователь {} удалил дизлайк {}", userId, reviewId);
                eventService.addEvent(userId, reviewId, 2L /* дизлайк */, 1L /* удаление */);
            }
        }
    }
}
