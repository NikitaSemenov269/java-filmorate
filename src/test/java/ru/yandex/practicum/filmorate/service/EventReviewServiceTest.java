package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dao.interfaces.EstimationRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.EventRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.ReviewRepository;
import ru.yandex.practicum.filmorate.model.Estimation;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private EventService eventService;
    @Mock
    private EstimationRepository estimationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ValidationService validationService;


    @InjectMocks
    private ReviewService reviewService;

    @Test
    void testEventAddReview() {
        Review review = Review.builder()
                .reviewId(156L)
                .content("Test")
                .isPositive(true)
                .userId(15L)
                .filmId(3L)
                .useful(1)
                .build();

        when(reviewRepository.addReview(review)).thenReturn(review);

        reviewService.addReview(review);

        verify(eventService, times(1)).addEvent(
                review.getUserId(), review.getReviewId(), 2L /* отзыв */, 2L /* добавление */
        );
    }

    @Test
    void testEventUpdateReview() {
        Review review = Review.builder()
                .reviewId(134L)
                .content("Test")
                .isPositive(true)
                .userId(15L)
                .filmId(3L)
                .useful(1)
                .build();

        when(reviewRepository.updateReview(review)).thenReturn(review);

        reviewService.updateReview(review);

        verify(eventService, times(1)).addEvent(
                review.getUserId(), review.getReviewId(), 2L /* отзыв */, 3L /* обновление */
        );
    }

    @Test
    void testEventDeleteReview() {
        Long reviewId = 134L;
        Long userId = 15L;

        Review review = Review.builder()
                .reviewId(reviewId)
                .userId(userId)
                .build();

        when(reviewRepository.getReviewById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).deleteReview(reviewId);

        reviewService.deleteReview(reviewId);

        verify(eventService, times(1)).addEvent(userId, reviewId, 2L /* отзыв */, 1L /* удаление */
        );
    }

    @Test
    void testEventAddLikeReview() {
        Long reviewId = 134L;
        Long userId = 15L;

        when(estimationRepository.getEstimation(reviewId, userId)).thenReturn(Optional.empty());
        doNothing().when(estimationRepository).addEstimation(reviewId, userId, Boolean.TRUE);
        doNothing().when(reviewRepository).addLikeReview(reviewId, userId);

        reviewService.addLikeReview(reviewId, userId);

        verify(eventService, times(1)).addEvent(userId, reviewId, 1L, 2L
        );
    }

    @Test
    void testEventAddDislikeReview() {
        Long reviewId = 134L;
        Long userId = 15L;

        when(estimationRepository.getEstimation(reviewId, userId)).thenReturn(Optional.empty());
        doNothing().when(estimationRepository).addEstimation(reviewId, userId, Boolean.FALSE);
        doNothing().when(reviewRepository).addDislikeReview(reviewId, userId);

        reviewService.addDislikeReview(reviewId, userId);

        verify(eventService, times(1)).addEvent(userId, reviewId, 2L, 2L
        );
    }

    @Test
    void testEventDeleteLikeReview() {
        Long reviewId = 134L;
        Long userId = 15L;

        Estimation existingLike = mock(Estimation.class);
        when(existingLike.getIsLike()).thenReturn(Boolean.TRUE);

        doNothing().when(validationService).validateReviewExists(reviewId);
        when(estimationRepository.getEstimation(reviewId, userId)).thenReturn(Optional.of(existingLike));
        doNothing().when(estimationRepository).deleteEstimation(reviewId, userId);
        doNothing().when(reviewRepository).addDislikeReview(reviewId, userId);

        reviewService.deleteLikeReview(reviewId, userId);

        verify(eventService, times(1)).addEvent(userId, reviewId, 1L, 1L
        );
    }

    @Test
    void testEventDeleteDislikeReview() {
        Long reviewId = 134L;
        Long userId = 15L;

        Estimation existingDislike = mock(Estimation.class);
        when(existingDislike.getIsLike()).thenReturn(Boolean.FALSE);

        doNothing().when(validationService).validateReviewExists(reviewId);
        when(estimationRepository.getEstimation(reviewId, userId)).thenReturn(Optional.of(existingDislike));
        doNothing().when(estimationRepository).deleteEstimation(reviewId, userId);
        doNothing().when(reviewRepository).addLikeReview(reviewId, userId);

        reviewService.deleteDislikeReview(reviewId, userId);

        verify(eventService, times(1)).addEvent(userId, reviewId, 2L, 1L
        );
    }
}
