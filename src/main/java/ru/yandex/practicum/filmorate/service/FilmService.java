package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.like.LikeRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final GenreRepository genreRepository;
    private final FilmMapper mapper;

    @Value("${popular.default-count:10}")
    private int defaultCount;

    public void addLike(long filmId, long userId) {
        var film = filmRepository.getById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("Film id " + filmId + " not found");
        }

        var user = userRepository.getById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User id " + userId + " not found");
        }

        likeRepository.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        var film = filmRepository.getById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("Film id " + filmId + " not found");
        }

        var user = userRepository.getById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User id " + userId + " not found");
        }

        likeRepository.removeLike(filmId, userId);
    }

    public Collection<FilmDto> getPopular(Integer count) {
        if (count == null || count <= 0) {
            count = defaultCount;
        }

        var mostLikedFilms = likeRepository.getMostLikedFilms(count);
        var result = new ArrayList<FilmDto>();

        for (var filmId : mostLikedFilms) {
            var film = filmRepository.getById(filmId).get();
            var filmGenres = genreRepository.findByFilmId(filmId);
            film.setGenres(new HashSet<>(filmGenres));
            result.add(mapper.mapToDto(film));
        }

        return result;
    }

    public FilmDto create(NewFilmRequest newFilmRequest) {
        var film = mapper.mapToFilm(newFilmRequest);
        var result = filmRepository.create(film);
        return mapper.mapToDto(result);
    }

    public FilmDto update(UpdateFilmRequest updateFilmRequest) {
        var film = filmRepository.getById(updateFilmRequest.getId());
        if (film.isEmpty()) {
            throw new NotFoundException("Film id " + updateFilmRequest.getId() + " not found");
        }

        mapper.updateFilmFields(film.get(), updateFilmRequest);
        var result = filmRepository.update(film.get());

        return mapper.mapToDto(result);
    }

    public FilmDto getById(Long id) {
        var film = filmRepository.getById(id);
        if (film.isEmpty()) throw new NotFoundException("Film id " + id + " not found");
        return mapper.mapToDto(film.get());
    }

    public Collection<FilmDto> findAll() {
        var films = filmRepository.findAll();
        return films.stream().sorted(Comparator.comparing(Film::getId))
                .map(mapper::mapToDto).collect(Collectors.toCollection(ArrayList::new));
    }
}
