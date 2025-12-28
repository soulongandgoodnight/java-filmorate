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
            "MERGE INTO PUBLIC.LIKES AS T " +
                    "USING (VALUES (?, ?)) AS S(FILM_ID, USER_ID) " +
                    "ON T.FILM_ID = S.FILM_ID AND T.USER_ID = S.USER_ID " +
                    "WHEN NOT MATCHED THEN " +
                    "INSERT VALUES(S.FILM_ID, S.USER_ID)";


    private static final String REMOVE_LIKE_QUERY =
            "DELETE FROM PUBLIC.LIKES  " +
                    "WHERE FILM_ID = ? AND USER_ID = ?;";

    private static final String GET_LIKES_BY_FILM_QUERY =
            "SELECT USER_ID " +
                    "FROM PUBLIC.LIKES " +
                    "WHERE FILM_ID = ?;";

    private static final String GET_MOST_LIKED_FILMS_QUERY =
            "SELECT FILM_ID " +
                    "FROM PUBLIC.LIKES " +
                    "GROUP BY FILM_ID " +
                    "ORDER BY count(FILM_ID) DESC LIMIT ?";

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

    public List<Long> getMostLikedFilms(long maxCount) {
        if (maxCount <= 0) {
            return List.of();
        }

        return jdbc.queryForList(GET_MOST_LIKED_FILMS_QUERY, Long.class, maxCount);
    }
}
