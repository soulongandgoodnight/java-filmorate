package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class FilmMapper {
    private final RatingMapper ratingMapper;

    public Film mapToFilm(NewFilmRequest request, Rating rating, Set<Genre> genres) {
        var film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        LocalDate minReleaseDate = LocalDate.of(1895, Month.DECEMBER, 28);
        if (request.getReleaseDate() != null && request.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setRating(rating);
        film.setGenres(genres);
        return film;
    }

    public Film updateFilmFields(Film film, UpdateFilmRequest dto, Rating rating, Set<Genre> genres) {
        if (dto.hasName()) {
            film.setName(dto.getName());
        }

        if (dto.hasDescription()) {
            film.setDescription(dto.getDescription());
        }

        if (dto.hasReleaseDate()) {
            LocalDate minReleaseDate = LocalDate.of(1895, Month.DECEMBER, 28);
            if (dto.getReleaseDate().isBefore(minReleaseDate)) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
            film.setReleaseDate(dto.getReleaseDate());
        }

        if (dto.hasDuration()) {
            film.setDuration(dto.getDuration());
        }

        if (genres != null && !genres.isEmpty()) {
            film.setGenres(genres);
        }

        if (rating != null) {
            film.setRating(rating);
        }

        return film;
    }

    public FilmDto mapToDto(Film film) {
        var dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        var genres = film.getGenres().stream().distinct()
                .sorted(Comparator.comparing(Genre::getId))
                .map(this::mapToDto).collect(Collectors.toCollection(ArrayList::new));
        dto.setGenres(genres);

        dto.setMpa(ratingMapper.mapToDto(film.getRating()));

        return dto;
    }

    private GenreDto mapToDto(Genre genre) {
        var dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }
}
