package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingRepository extends BaseRepository<Rating> {
    private static final String FIND_ALL_QUERY = "SELECT \"id\", \"name\" from PUBLIC.\"ratings\"";
    private static final String FIND_BY_ID_QUERY = "SELECT \"id\", \"name\" from PUBLIC.\"ratings\" WHERE \"id\" = ?";

    public RatingRepository(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public List<Rating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Rating> findById(long ratingId) {
        return findOne(FIND_BY_ID_QUERY, ratingId);
    }
}
