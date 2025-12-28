package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    private static final String CREATE_QUERY =
            "INSERT INTO PUBLIC.\"users\" (\"email\", \"name\", \"login\", \"birthday\")" +
                    "VALUES (?, ?, ?, ?) ;";

    private static final String UPDATE_QUERY =
            "UPDATE PUBLIC.\"users\" SET " +
                    "\"email\" = ?," +
                    "\"name\" = ?," +
                    "\"login\" = ?," +
                    "\"birthday\" = ? " +
                    "WHERE \"id\" = ?";

    private static final String DELETE_QUERY =
            "DELETE FROM PUBLIC.\"users\" " +
                    "WHERE \"id\" = ?;";

    private static final String GET_BY_ID_QUERY =
            "SELECT \"id\", \"email\", \"name\", \"login\", \"birthday\" " +
                    "FROM PUBLIC.\"users\" " +
                    "WHERE \"id\" = ?";

    private static final String GET_BY_MANY_IDS_QUERY =
            "SELECT \"id\", \"email\", \"name\", \"login\", \"birthday\" " +
                    "FROM PUBLIC.\"users\" " +
                    "WHERE \"id\" in (%s)";

    private static final String FIND_ALL_QUERY =
            "SELECT \"id\", \"email\", \"login\", \"name\", \"birthday\" " +
                    "FROM PUBLIC.\"users\"";

    public User create(User user) {
        var id = insert(CREATE_QUERY, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());

        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(UPDATE_QUERY, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }

    public boolean delete(Long id) {
        return super.delete(DELETE_QUERY, id);
    }

    public Optional<User> getById(Long id) {
        return super.findOne(GET_BY_ID_QUERY, id);
    }

    public Collection<User> getByIds(Collection<Long> ids) {
        var idsArray = ids.toArray();
        var inSql = String.join(",", Collections.nCopies(idsArray.length, "?"));
        var sql = String.format(GET_BY_MANY_IDS_QUERY, inSql);
        return findMany(sql, idsArray);
    }

    public Collection<User> findAll() {
        return super.findMany(FIND_ALL_QUERY);
    }
}
