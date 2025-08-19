//package ru.yandex.practicum.filmorate.dao.jdbc;
//
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class JdbcEventRepository {
//
//    private static final String INSERT_EVENT_QUERY = """
//                   INSERT INTO event_feed (user_id, description)
//                   VALUES (:user_id, :description)
//            """;
//
//    private static final String FIND_LAST_EVENT_QUERY = """
//            SELECT event_id as №, user_id, description, created_at as date_of_creation FROM event_feed
//            ORDER BY date_of_creation DESC
//            LIMIT :count
//            """;
//
//    private static final String FIND_EVENT_BY_ID_USER_QUERY = """
//            SELECT event_id as №, user_id, description, created_at as date_of_creation FROM event_feed
//            WHERE user_id = :user_id
//            ORDER BY date_of_creation DESC
//            LIMIT :count
//            """;
//
//    private static final String FIND_ALL_QUERY = """
//            SELECT event_id as №, user_id, description, created_at as date_of_creation FROM event_feed
//            ORDER BY date_of_creation DESC
//            """;
//}
//
//
