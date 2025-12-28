package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.rating.RatingRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public final class FilmMapper {
    private RatingRepository ratingRepository;
    private GenreRepository genreRepository;

    public Film mapToFilm(NewFilmRequest request) {
        var film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        var rating = ratingRepository.findById(request.getRatingId());
        if (rating.isEmpty()) {
            throw new ValidationException("Рейтинг, указанный в запросе, отсутствует");
        }

        var genres = getExistingGenres(request.getGenreIds());
        film.setGenres(genres);

        return film;
    }

    public Film updateFilmFields(Film film, UpdateFilmRequest dto) {
        if (dto.hasName()) {
            film.setName(dto.getName());
        }

        if (dto.hasDescription()) {
            film.setDescription(dto.getDescription());
        }

        if (dto.hasReleaseDate()) {
            film.setReleaseDate(dto.getReleaseDate());
        }

        if (dto.hasDuration()) {
            film.setDuration(dto.getDuration());
        }

        if (dto.hasGenres()) {
            var genres = getExistingGenres(dto.getGenreIds());
            film.setGenres(genres);
        }

        if (dto.hasRating()) {
            var ratingOptional = ratingRepository.findById(dto.getRatingId());
            if (ratingOptional.isEmpty()) {
                throw new ValidationException("Рейтинг, указанный в запросе, отсутствует");
            }
            film.setRating(ratingOptional.get());
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
        dto.setLikes(film.getLikes());
        dto.setGenreIds(film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        dto.setRatingId(film.getRating().getId());

        return dto;
    }

    private HashSet<Genre> getExistingGenres(Collection<Long> genreIds) {
        var genres = genreRepository.findAll().stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre, (a, b) -> b, HashMap::new));

        var filmGenres = new HashSet<Genre>();
        for (var genreId : genreIds) {
            if (!genres.containsKey(genreId)) {
                throw new ValidationException("В запросе указан отсутствующий жанр. id: '" + genreId + "'");
            }

            filmGenres.add(genres.get(genreId));
        }

        return filmGenres;
    }
}
