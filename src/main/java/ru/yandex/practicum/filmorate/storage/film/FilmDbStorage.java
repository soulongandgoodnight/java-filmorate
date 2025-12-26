package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Film getById(Long id) {
        return null;
    }

    @Override
    public Collection<Film> findAll() {
        return List.of();
    }
}
