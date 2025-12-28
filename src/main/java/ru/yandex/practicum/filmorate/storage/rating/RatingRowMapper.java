package ru.yandex.practicum.filmorate.storage.rating;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<Rating> {
    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        var rating = new Rating();
        rating.setId(rs.getLong("id"));
        rating.setName(rs.getString("name"));

        return rating;
    }
}
