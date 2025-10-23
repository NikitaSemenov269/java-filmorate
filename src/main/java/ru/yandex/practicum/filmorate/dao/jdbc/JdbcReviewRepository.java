package ru.yandex.practicum.filmorate.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.interfaces.ReviewRepository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;

@Repository
@Qualifier("reviewRepository")
public class JdbcReviewRepository extends BaseRepository<Review> implements ReviewRepository {

    private static final String INSERT_REVIEW_QUERY = """
            insert into reviews (content, is_positive, user_id, film_id, useful)
            values
                (:content, :isPositive, :userId, :filmId, :useful)
            ;
            """;

    private static final String UPDATE_REVIEW_QUERY = """
            update reviews
            set
                content = :content,
                is_positive = :isPositive
            where
                review_id = :reviewId
            ;
            """;

    private static final String DELETE_REVIEW_QUERY = """
            delete from reviews
            where
                review_id = :reviewId
            ;
            """;

    private static final String FIND_REVIEW_BY_ID_QUERY = """
            select
                *
            from reviews
            where
                review_id = :reviewId
            ;
            """;

    private static final String GET_POPULAR_REVIEWS_BY_FILM_ID_QUERY = """
                select
                    *
                from reviews
                where
                    film_id = :filmId
                order by useful desc
                limit :count
                ;
            """;

    private static final String GET_ALL_REVIEWS_QUERY = """
                select
                    *
                from reviews
                order by useful desc
                limit :count
                ;
            """;

    private static final String UPDATE_REVIEW_LIKE_QUERY = """
            update reviews
            set
                useful = useful + 1
            where
                review_id = :reviewId
            ;
            """;

    private static final String UPDATE_REVIEW_DISLIKE_QUERY = """
            update reviews
            set
                useful = useful - 1
            where
                review_id = :reviewId
            ;
            """;

    public JdbcReviewRepository(NamedParameterJdbcOperations jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review addReview(Review review) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", review.getContent());
        params.put("isPositive", review.getIsPositive());
        params.put("userId", review.getUserId());
        params.put("filmId", review.getFilmId());
        params.put("useful", 0);

        long id = insert(INSERT_REVIEW_QUERY, params);
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        Review newReview = getReviewById(review.getReviewId()).get();
        newReview.setContent(review.getContent());
        newReview.setIsPositive(review.getIsPositive());
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", review.getReviewId());
        params.put("content", review.getContent());
        params.put("isPositive", review.getIsPositive());

        update(UPDATE_REVIEW_QUERY, params);
        return newReview;
    }

    @Override
    public void deleteReview(Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        delete(DELETE_REVIEW_QUERY, params);
    }

    @Override
    public Optional<Review> getReviewById(Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        return findOne(FIND_REVIEW_BY_ID_QUERY, params);
    }

    @Override
    public Collection<Review> getPopularReviews(Long filmId, int count) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        params.put("count", count);
        return findMany(GET_POPULAR_REVIEWS_BY_FILM_ID_QUERY, params);
    }

    @Override
    public Collection<Review> getAllReviews(int count) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        return findMany(GET_ALL_REVIEWS_QUERY, params);
    }

    @Override
    public void addLikeReview(Long reviewId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        update(UPDATE_REVIEW_LIKE_QUERY, params);
    }

    @Override
    public void addDislikeReview(Long reviewId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        update(UPDATE_REVIEW_DISLIKE_QUERY, params);
    }
}
