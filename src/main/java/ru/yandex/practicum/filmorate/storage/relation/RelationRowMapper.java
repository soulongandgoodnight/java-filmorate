package ru.yandex.practicum.filmorate.storage.relation;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Relation;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class RelationRowMapper implements RowMapper<Relation> {

    @Override
    public Relation mapRow(ResultSet rs, int rowNum) throws SQLException {
        var relation = new Relation();
        relation.setFollowingUserId(rs.getLong("FOLLOWING_USER_ID"));
        relation.setFollowedUserId(rs.getLong("FOLLOWED_USER_ID"));
        relation.setIsFriendshipConfirmed(rs.getBoolean("IS_FRIENDSHIP_CONFIRMED"));
        return relation;
    }
}
