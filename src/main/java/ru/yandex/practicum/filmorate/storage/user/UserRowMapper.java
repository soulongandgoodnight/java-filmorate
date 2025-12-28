package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        var user = new User();
        user.setId(rs.getLong("ID"));
        user.setName(rs.getString("NAME"));
        user.setEmail(rs.getString("EMAIL"));
        user.setLogin(rs.getString("LOGIN"));
        user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());

        return user;
    }
}
