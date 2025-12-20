package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;  // Для дефолта из properties (из теории @Value)
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Value("${popular.default-count:10}")
    private int defaultCount;

    public void addLike(Long filmId, Long userId) {
        Film film = getById(filmId);
        userService.getById(userId);
        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getById(filmId);
        userService.getById(userId);
        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    public Collection<Film> getPopular(Integer count) {
        if (count == null || count <= 0) count = defaultCount;
        return filmStorage.findAll().stream()
                .sorted(
                        Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(Long id) {
        Film film = filmStorage.getById(id);
        if (film == null) throw new NotFoundException("Film with id " + id + " not found");
        return film;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }
}
