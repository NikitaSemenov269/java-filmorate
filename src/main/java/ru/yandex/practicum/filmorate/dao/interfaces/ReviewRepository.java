package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewRepository {

    Review addReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(Long reviewId);

    Optional<Review> getReviewById(Long reviewId);

    Collection<Review> getPopularReviews(Long filmId, int count);

    void addLikeReview(Long reviewId, Long userId);

    void addDislikeReview(Long reviewId, Long userId);
}
