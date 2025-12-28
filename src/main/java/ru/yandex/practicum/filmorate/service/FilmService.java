package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.like.LikeRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

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
//        if (count == null || count <= 0) {
//            count = defaultCount;
//        }
//
//        var mostLikedFilms = likeRepository.getMostLikedFilms(count);
//        var result = new ArrayList<FilmDto>();
//
//        for(var filmId: mostLikedFilms) {
//            var film = filmRepository.getById(filmId);
//            var filmGenre = genreRepository.findById(film.get);
//        }
        return List.of();
    }

    public Film create(Film film) {
        return filmRepository.create(film);
    }

    public Film update(Film film) {
        return filmRepository.update(film);
    }

    public Film getById(Long id) {
        var film = filmRepository.getById(id);
        if (film.isEmpty()) throw new NotFoundException("Film id " + id + " not found");
        return film.get();
    }

    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }
}
