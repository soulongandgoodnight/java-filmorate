package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeRepository {
    private static final String ADD_LIKE_QUERY =
            "MERGE INTO PUBLIC.LIKES (FILM_ID, USER_ID) " +
                    "VALUES (?, ?)";


    private static final String REMOVE_LIKE_QUERY =
            "DELETE FROM PUBLIC.LIKES  " +
                    "WHERE FILM_ID = ? AND USER_ID = ?;";

    private static final String GET_LIKES_BY_FILM_QUERY =
            "SELECT USER_ID " +
                    "FROM PUBLIC.LIKES " +
                    "WHERE FILM_ID = ?;";

    private final JdbcTemplate jdbc;

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public List<Long> getLikesByFilm(long filmId) {
        return jdbc.queryForList(GET_LIKES_BY_FILM_QUERY, Long.class, filmId);
    }
}
