package ru.yandex.practicum.filmorate.storage.film;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@JdbcTest
@AutoConfigureTestDatabase
public class FilmRepositoryApplicationTests {
    private final FilmRepository filmRepository;
    private static Rating defaultRating;

    public FilmRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.filmRepository = new FilmRepository(jdbc, new FilmRowMapper());
        defaultRating = new Rating();
        defaultRating.setId(1L);
        defaultRating.setName("G");
    }

    @Test
    public void testCreateAndGetFilm() {
        var film = Instancio.of(Film.class)
                .ignore(field(Film::getId))
                .ignore(field(Film::getLikes))
                .ignore(field(Film::getGenres))
                .set(field(Film::getRating), defaultRating)
                .create();

        var resultFilm = filmRepository.create(film);
        assertThat(resultFilm.getId()).isNotNull();
        var recentlyCreatedFilm = filmRepository.getById(resultFilm.getId());
        assertThat(recentlyCreatedFilm).isPresent();
        assertThat(recentlyCreatedFilm.get()).isEqualTo(film);
    }

    @Test
    public void testCreateAndUpdateAndGetFilm() {
        var film = Instancio.of(Film.class)
                .ignore(field(Film::getId))
                .ignore(field(Film::getLikes))
                .ignore(field(Film::getGenres))
                .set(field(Film::getRating), defaultRating)
                .create();

        var resultFilm = filmRepository.create(film);
        assertThat(resultFilm.getId()).isNotNull();
        resultFilm.setName("updated " + resultFilm.getName());
        resultFilm.setDescription("updated " + resultFilm.getDescription());
        resultFilm.setReleaseDate(resultFilm.getReleaseDate().minusYears(3));
        resultFilm.setDuration(resultFilm.getDuration() * 2);

        filmRepository.update(film);
        var recentlyUpdatedFilm = filmRepository.getById(resultFilm.getId());
        assertThat(recentlyUpdatedFilm).isPresent();
        assertThat(recentlyUpdatedFilm.get()).isEqualTo(resultFilm);
    }

    @Test
    public void testCreateAndDeleteFilm() {
        var film = Instancio.of(Film.class)
                .ignore(field(Film::getId))
                .ignore(field(Film::getLikes))
                .ignore(field(Film::getGenres))
                .set(field(Film::getRating), defaultRating)
                .create();

        var resultFilm = filmRepository.create(film);
        assertThat(resultFilm.getId()).isNotNull();
        var deletionResult = filmRepository.delete(film.getId());
        assertThat(deletionResult).isTrue();

        var recentlyCreatedFilm = filmRepository.getById(film.getId());
        assertThat(recentlyCreatedFilm).isNotPresent();

        var oneMoreDeletionAttempt = filmRepository.delete(film.getId());
        assertThat(oneMoreDeletionAttempt).isFalse();
    }

    @Test
    public void testFindAll() {
        var totalCount = 10;
        var films = Instancio.ofList(Film.class)
                .size(totalCount)
                .ignore(field(Film::getId))
                .ignore(field(Film::getLikes))
                .ignore(field(Film::getGenres))
                .set(field(Film::getRating), defaultRating)
                .create();

        for (var film : films) {
            filmRepository.create(film);
        }

        var actualFilms = filmRepository.findAll();
        assertThat(actualFilms.size()).isEqualTo(totalCount);
        assertThat(actualFilms).isEqualTo(films);
    }

}
