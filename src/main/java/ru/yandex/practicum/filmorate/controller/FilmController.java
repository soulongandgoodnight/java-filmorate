package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Validated(Marker.OnCreate.class) @RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {} (id={})", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Film update(@Validated(Marker.OnUpdate.class) @RequestBody Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            String message = "Фильм с ID " + (film.getId() != null ? film.getId() : "[не указан]") + " не найден";
            log.error("Ошибка: {}", message);
            throw new ValidationException(message);
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {} (id={}", film.getName(), film.getId());
        return film;
    }

    private void validateFilm(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, Month.DECEMBER, 28);
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(minReleaseDate)) {
            String message = "Дата релиза не может быть раньше 28 декабря 1895 года";
            log.error("Ошибка валидации фильма: {}", message);
            throw new ValidationException(message);
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
