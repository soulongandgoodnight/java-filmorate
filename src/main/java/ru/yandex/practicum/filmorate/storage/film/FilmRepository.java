package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collection;
import java.util.List;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Film create(Film film) {
        return null;
    }

    public Film update(Film film) {
        return null;
    }

    public void delete(Long id) {

    }

    public Film getById(Long id) {
        return null;
    }

    public Collection<Film> findAll() {
        return List.of();
    }
}
