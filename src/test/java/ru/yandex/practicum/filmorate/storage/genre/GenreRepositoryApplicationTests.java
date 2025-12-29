package ru.yandex.practicum.filmorate.storage.genre;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@JdbcTest
@AutoConfigureTestDatabase
public class GenreRepositoryApplicationTests {
    private final GenreRepository genreRepository;
    private final FilmRepository filmRepository;
    private final ArrayList<Film> films;

    public GenreRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.genreRepository = new GenreRepository(jdbc, new GenreRowMapper());
        this.filmRepository = new FilmRepository(jdbc, new FilmRowMapper());
        films = new ArrayList<>();
    }


    @Test
    public void testGetGenreById() {
        var genreOptional = genreRepository.findById(1L);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    public void testFindAll() {
        var genres = genreRepository.findAll();
        assertThat(genres.size())
                .isEqualTo(6);
    }

    @Test
    public void testGetGenresByFilmId() {
        createEnoughFilms(10);
        for (var film : films.stream().limit(films.size() / 2).toList()) {
            addGenresForFilmIfNotExist(film);
        }

        for (var film : films) {
            var filmGenres = new HashSet<>(genreRepository.findByFilmId(film.getId()));
            assertThat(filmGenres).isEqualTo(film.getGenres());
        }
    }

    private void createEnoughFilms(int filmsCount) {
        var filmsToCreateCount = filmsCount - films.size();
        for (int i = 0; i < filmsToCreateCount; i++) {
            films.add(createAndSaveFilm());
        }

        Collections.shuffle(films);
    }

    private void addGenresForFilmIfNotExist(Film film) {
        var currentGenres = film.getGenres();
        if (currentGenres != null && !currentGenres.isEmpty()) {
            return;
        }

        var allGenres = genreRepository.findAll();
        Collections.shuffle(allGenres);
        var filmGenres = allGenres.stream().limit(3).collect(Collectors.toSet());
        film.setGenres(filmGenres);
        for (var filmGenre : filmGenres) {
            genreRepository.createGenreForFilm(film.getId(), filmGenre.getId());
        }
    }

    private Film createAndSaveFilm() {
        var defaultRating = new Rating();
        defaultRating.setId(1L);
        defaultRating.setName("0+");

        var film = Instancio.of(Film.class)
                .ignore(field(Film::getId))
                .ignore(field(Film::getLikes))
                .ignore(field(Film::getGenres))
                .set(field(Film::getRating), defaultRating)
                .create();

        return filmRepository.create(film);
    }
}
