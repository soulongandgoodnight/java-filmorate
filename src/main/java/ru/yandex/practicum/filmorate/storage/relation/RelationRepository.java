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
            "MERGE INTO PUBLIC.RELATIONS (FOLLOWING_USER_ID, FOLLOWED_USER_ID) " +
                    "VALUES(?, ?) ";

    private static final String REMOVE_RELATION_QUERY =
            "DELETE FROM PUBLIC.RELATIONS " +
                    "WHERE FOLLOWING_USER_ID = ? AND FOLLOWED_USER_ID = ?;";

    private static final String GET_ALL_BY_USER_ID_QUERY =
            "SELECT FOLLOWING_USER_ID, FOLLOWED_USER_ID " +
                    "FROM PUBLIC.RELATIONS " +
                    "WHERE FOLLOWING_USER_ID = ?;";

    private static final String FIND_RELATION_QUERY =
            "SELECT FOLLOWING_USER_ID, FOLLOWED_USER_ID " +
                    "FROM PUBLIC.RELATIONS " +
                    "WHERE FOLLOWING_USER_ID = ? AND FOLLOWED_USER_ID = ?;";

    public void addRelation(Relation relation) {
        jdbc.update(ADD_RELATION_QUERY, relation.getFollowingUserId(), relation.getFollowedUserId());
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
