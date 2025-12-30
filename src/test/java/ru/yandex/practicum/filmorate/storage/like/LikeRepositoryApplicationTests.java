package ru.yandex.practicum.filmorate.storage.like;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@JdbcTest
@AutoConfigureTestDatabase
public class LikeRepositoryApplicationTests {
    private final LikeRepository likeRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private ArrayList<User> users;
    private ArrayList<Film> films;

    public LikeRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.likeRepository = new LikeRepository(jdbc);
        this.filmRepository = new FilmRepository(jdbc, new FilmRowMapper());
        this.userRepository = new UserRepository(jdbc, new UserRowMapper());
    }

    @BeforeEach
    public void beforeEach() {
        users = new ArrayList<>();
        films = new ArrayList<>();
        users.add(createAndSaveUser());
        films.add(createAndSaveFilm());
    }

    @Test
    public void testAddLikeSeveralTimes() {
        // Ставим лайк 1 раз
        likeRepository.addLike(films.getFirst().getId(), users.getFirst().getId());
        var filmLikes = likeRepository.getLikesByFilm(films.getFirst().getId());
        assertThat(filmLikes.size()).isEqualTo(1);
        assertThat(filmLikes.getFirst()).isEqualTo(users.getFirst().getId());

        // Ставим лайк несколько раз, чтобы проверить уникальность лайка от пользователя
        for (int i = 0; i < 10; i++) {
            likeRepository.addLike(films.getFirst().getId(), users.getFirst().getId());
        }

        filmLikes = likeRepository.getLikesByFilm(films.getFirst().getId());
        assertThat(filmLikes.size()).isEqualTo(1);
        assertThat(filmLikes.getFirst()).isEqualTo(users.getFirst().getId());
    }

    @Test
    public void testAddAndThenRemoveLike() {
        // Ставим лайк
        likeRepository.addLike(films.getFirst().getId(), users.getFirst().getId());
        var filmLikes = likeRepository.getLikesByFilm(films.getFirst().getId());
        assertThat(filmLikes.size()).isEqualTo(1);
        assertThat(filmLikes.getFirst()).isEqualTo(users.getFirst().getId());

        // Убираем лайк
        likeRepository.removeLike(films.getFirst().getId(), users.getFirst().getId());
        filmLikes = likeRepository.getLikesByFilm(films.getFirst().getId());
        assertThat(filmLikes.size()).isEqualTo(0);
    }

    @Test
    public void testGetLikesByFilm() {
        var likesForFirstFilmCount = 4;
        var likesForSecondFilmCount = 13;
        createEnoughFilms(2);
        createEnoughUsers(20);
        var firstFilm = films.get(0);
        var secondFilm = films.get(1);

        setLikesForFilm(firstFilm, likesForFirstFilmCount);
        setLikesForFilm(secondFilm, likesForSecondFilmCount);
        var likesForFirstFilm = likeRepository.getLikesByFilm(firstFilm.getId());
        var likesForSecondFilm = likeRepository.getLikesByFilm(secondFilm.getId());
        assertThat(likesForFirstFilm.size()).isEqualTo(likesForFirstFilmCount);
        assertThat(likesForSecondFilm.size()).isEqualTo(likesForSecondFilmCount);
    }


    private void createEnoughFilms(int filmsCount) {
        var filmsToCreateCount = filmsCount - films.size();
        for (int i = 0; i < filmsToCreateCount; i++) {
            films.add(createAndSaveFilm());
        }

        Collections.shuffle(films);
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
