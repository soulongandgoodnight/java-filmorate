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
            "INSERT INTO PUBLIC.\"likes\" (\"film_id\", \"user_id\") " +
                    "VALUES(?, ?) " +
                    "ON CONFLICT DO NOTHING;";

    private static final String REMOVE_LIKE_QUERY =
            "DELETE FROM PUBLIC.\"likes\" VALUES " +
                    "WHERE \"film_id\" = ? AND \"user_id\" = ?;";

    private static final String GET_LIKES_BY_FILM_QUERY =
            "SELECT \"user_id\" " +
                    "FROM PUBLIC.\"likes\" " +
                    "WHERE \"film_id\" = ?;";

    private static final String GET_MOST_LIKED_FILMS_QUERY =
            "SELECT \"film_id\"\n" +
                    "FROM PUBLIC.\"likes\"\n" +
                    "GROUP BY \"film_id\"\n" +
                    "ORDER BY count(\"film_id\") DESC LIMIT ?";

    private final JdbcTemplate jdbc;

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public List<Long> getLikesByFilm(long filmId) {
        var result = jdbc.queryForList(GET_LIKES_BY_FILM_QUERY, Long.class, filmId);
        return result;
    }

    public List<Long> getMostLikedFilms(long maxCount) {
        if (maxCount <= 0) {
            return List.of();
        }

        var result = jdbc.queryForList(GET_MOST_LIKED_FILMS_QUERY, Long.class, maxCount);
        return result;
    }
}
