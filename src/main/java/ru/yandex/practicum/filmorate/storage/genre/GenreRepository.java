package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Component
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY =
            "SELECT ID, NAME " +
                    "FROM PUBLIC.GENRES " +
                    "ORDER BY ID";

    private static final String FIND_BY_ID_QUERY =
            "SELECT ID, NAME " +
                    "FROM PUBLIC.GENRES WHERE ID = ?";

    private static final String FIND_BY_FILM_ID_QUERY =
            "SELECT G.ID, G.NAME FROM PUBLIC.GENRES G " +
                    "LEFT JOIN PUBLIC.FILM_GENRES F " +
                    "ON G.ID = F.GENRE_ID " +
                    "WHERE F.FILM_ID = ? ";

    private static final String CREATE_GENRE_FOR_FILM_QUERY =
            "INSERT INTO PUBLIC.FILM_GENRES (FILM_ID, GENRE_ID) " +
                    "VALUES (?, ?); ";

    private static final String DELETE_GENRE_FOR_FILM_QUERY =
            "DELETE FROM PUBLIC.FILM_GENRES " +
                    "WHERE FILM_ID = ? AND GENRE_ID = ?;";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public Collection<Genre> findByFilmId(long filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }

    public void createGenreForFilm(long filmId, long genreId) {
        jdbc.update(CREATE_GENRE_FOR_FILM_QUERY, filmId, genreId);
    }

    public void deleteGenreForFilm(long filmId, long genreId) {
        jdbc.update(DELETE_GENRE_FOR_FILM_QUERY, filmId, genreId);
    }
}
