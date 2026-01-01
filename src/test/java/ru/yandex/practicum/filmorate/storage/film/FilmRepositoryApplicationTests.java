package ru.yandex.practicum.filmorate.storage.film;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.like.LikeRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@JdbcTest
@AutoConfigureTestDatabase
public class FilmRepositoryApplicationTests {
    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ArrayList<User> users;
    private final ArrayList<Film> films;

    private static Rating defaultRating;

    public FilmRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.likeRepository = new LikeRepository(jdbc);
        this.filmRepository = new FilmRepository(jdbc, new FilmRowMapper());
        this.userRepository = new UserRepository(jdbc, new UserRowMapper());
        users = new ArrayList<>();
        films = new ArrayList<>();
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
    public void testGetMostLikedFilms() {
        createEnoughFilms(10);
        createEnoughUsers(100);
        var mostPopularFilmLikesCount = 70;
        var secondPopularFilmLikesCount = 48;
        var thirdPopularFilmLikesCount = 22;
        var defaultLikesCount = 8;

        var mostPopularFilm = films.get(0);
        var secondPopularFilm = films.get(1);
        var thirdPopularFilm = films.get(2);

        setLikesForFilm(mostPopularFilm, mostPopularFilmLikesCount);
        setLikesForFilm(secondPopularFilm, secondPopularFilmLikesCount);
        setLikesForFilm(thirdPopularFilm, thirdPopularFilmLikesCount);

        for (int i = 3; i < films.size(); i++) {
            setLikesForFilm(films.get(i), defaultLikesCount);
        }

        var mostLikedFilms = filmRepository.getMostLikedFilms(4);
        assertThat(mostLikedFilms.size()).isEqualTo(4);
        assertThat(mostLikedFilms.get(0)).isEqualTo(mostPopularFilm);
        assertThat(mostLikedFilms.get(1)).isEqualTo(secondPopularFilm);
        assertThat(mostLikedFilms.get(2)).isEqualTo(thirdPopularFilm);
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

    private void setLikesForFilm(Film film, int likesCount) {
        if (likesCount > users.size()) {
            createEnoughUsers(likesCount);
        }
        var userIds = users.stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(userIds);
        for (int i = 0; i < likesCount; i++) {
            likeRepository.addLike(film.getId(), userIds.get(i));
        }
    }

    private void createEnoughFilms(int filmsCount) {
        var filmsToCreateCount = filmsCount - films.size();
        for (int i = 0; i < filmsToCreateCount; i++) {
            films.add(createAndSaveFilm());
        }

        Collections.shuffle(films);
    }

    private void createEnoughUsers(int usersCount) {
        var usersToCreateCount = usersCount - users.size();
        for (int i = 0; i < usersToCreateCount; i++) {
            users.add(createAndSaveUser());
        }

        Collections.shuffle(users);
    }

    private User createAndSaveUser() {
        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getRelations))
                .create();
        return userRepository.create(user);
    }

    private Film createAndSaveFilm() {
        var defaultRating = new Rating();
        defaultRating.setId(1L);
        defaultRating.setName("G");

        var film = Instancio.of(Film.class)
                .ignore(field(Film::getId))
                .ignore(field(Film::getLikes))
                .ignore(field(Film::getGenres))
                .set(field(Film::getRating), defaultRating)
                .create();

        return filmRepository.create(film);
    }
}
