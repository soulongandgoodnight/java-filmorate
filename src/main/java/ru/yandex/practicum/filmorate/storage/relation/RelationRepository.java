package ru.yandex.practicum.filmorate.storage.relation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class RelationRepository extends BaseRepository<Relation> {
    public RelationRepository(JdbcTemplate jdbc, RowMapper<Relation> mapper) {
        super(jdbc, mapper);
    }

    private static final String ADD_RELATION_QUERY =
            "MERGE INTO PUBLIC.RELATIONS AS T " +
                    "USING (VALUES(?, ?, ?)) AS S (FOLLOWING_USER_ID, FOLLOWED_USER_ID, IS_FRIENDSHIP_CONFIRMED) " +
                    "ON T.FOLLOWING_USER_ID = S.FOLLOWING_USER_ID AND T.FOLLOWED_USER_ID = S.FOLLOWED_USER_ID " +
                    "WHEN NOT MATCHED THEN " +
                    "INSERT VALUES(S.FOLLOWING_USER_ID, S.FOLLOWED_USER_ID, S.IS_FRIENDSHIP_CONFIRMED); ";

    private static final String UPDATE_RELATION_QUERY =
            "UPDATE PUBLIC.RELATIONS " +
                    "SET IS_FRIENDSHIP_CONFIRMED = ? " +
                    "WHERE FOLLOWING_USER_ID = ? AND FOLLOWED_USER_ID = ?;";

    private static final String REMOVE_RELATION_QUERY =
            "DELETE FROM PUBLIC.RELATIONS " +
                    "WHERE FOLLOWING_USER_ID = ? AND FOLLOWED_USER_ID = ?;";

    private static final String GET_ALL_BY_USER_ID_QUERY =
            "SELECT FOLLOWING_USER_ID, FOLLOWED_USER_ID, IS_FRIENDSHIP_CONFIRMED " +
                    "FROM PUBLIC.RELATIONS " +
                    "WHERE FOLLOWING_USER_ID = ?;";

    private static final String FIND_RELATION_QUERY =
            "SELECT FOLLOWING_USER_ID, FOLLOWED_USER_ID, IS_FRIENDSHIP_CONFIRMED " +
                    "FROM PUBLIC.RELATIONS " +
                    "WHERE FOLLOWING_USER_ID = ? AND FOLLOWED_USER_ID = ?;";

    public void addRelation(Relation relation) {
        jdbc.update(ADD_RELATION_QUERY, relation.getFollowingUserId(), relation.getFollowedUserId(), relation.getIsFriendshipConfirmed());
    }

    public void updateRelation(Relation relation) {
        jdbc.update(UPDATE_RELATION_QUERY, relation.getIsFriendshipConfirmed(), relation.getFollowingUserId(), relation.getFollowedUserId());
    }

    public void removeRelation(Relation relation) {
        jdbc.update(REMOVE_RELATION_QUERY, relation.getFollowingUserId(), relation.getFollowedUserId());
    }

    public Collection<Relation> getAllByUserId(long userId) {
        return findMany(GET_ALL_BY_USER_ID_QUERY, userId);
    }

    public Optional<Relation> findRelation(long followingUserId, long followedUserId) {
        return findOne(FIND_RELATION_QUERY, followingUserId, followedUserId);
    }
}
