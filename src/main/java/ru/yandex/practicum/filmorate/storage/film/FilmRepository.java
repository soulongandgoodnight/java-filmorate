package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    private static final String CREATE_QUERY =
            "INSERT  INTO PUBLIC.FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
                    "VALUES(?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE PUBLIC.FILMS SET " +
                    "NAME = ?," +
                    "DESCRIPTION = ?," +
                    "RELEASE_DATE = ?," +
                    "DURATION = ?," +
                    "RATING_ID = ? " +
                    "WHERE ID = ?;";

    private static final String DELETE_QUERY =
            "DELETE FROM PUBLIC.FILMS " +
                    "WHERE ID = ?;";

    private static final String GET_BY_ID_QUERY =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME as RATING_NAME " +
                    "FROM PUBLIC.FILMS f " +
                    "LEFT JOIN PUBLIC.RATINGS r " +
                    "ON  f.RATING_ID = r.ID " +
                    "WHERE f.id = ?";

    private static final String GET_MOST_LIKED_FILMS_QUERY =
            "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATING_ID, R.NAME as RATING_NAME " +
                    "FROM PUBLIC.FILMS F " +
                    "LEFT JOIN PUBLIC.LIKES L " +
                    "ON F.ID = L.FILM_ID " +
                    "LEFT JOIN PUBLIC.RATINGS R " +
                    "ON  F.RATING_ID = R.ID " +
                    "GROUP BY F.ID " +
                    "ORDER BY count(L.FILM_ID) DESC LIMIT ?";

    private static final String FIND_ALL_QUERY =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME as RATING_NAME " +
                    "FROM PUBLIC.FILMS f " +
                    "LEFT JOIN PUBLIC.RATINGS r " +
                    "ON  f.RATING_ID = r.ID ";

    public Film create(Film film) {
        Long ratingId = null;
        if (film.getRating() != null) {
            ratingId = film.getRating().getId();
        }

        var id = insert(CREATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), ratingId);
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        Long ratingId = null;
        if (film.getRating() != null) {
            ratingId = film.getRating().getId();
        }

        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), ratingId, film.getId());
        return film;
    }

    public boolean delete(Long id) {
        return delete(DELETE_QUERY, id);
    }

    public Optional<Film> getById(Long id) {
        return findOne(GET_BY_ID_QUERY, id);
    }

    public Collection<Film> findAll() {

        return findMany(FIND_ALL_QUERY);
    }

    public List<Film> getMostLikedFilms(long maxCount) {
        if (maxCount <= 0) {
            return List.of();
        }

        return findMany(GET_MOST_LIKED_FILMS_QUERY, maxCount);
    }
}
