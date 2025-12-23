package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void clearData() {
        List<Long> filmIds = filmService.findAll().stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        filmIds.forEach(filmStorage::delete);

        List<Long> userIds = userService.findAll().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        userIds.forEach(userStorage::delete);
    }

    @Test
    void shouldCreateFilmWithValidFields() throws Exception {
        Film validFilm = new Film();
        validFilm.setName("film");
        validFilm.setDescription("description");
        validFilm.setReleaseDate(LocalDate.of(2007,7,7));
        validFilm.setDuration(70);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.releaseDate").value("2007-07-07"))
                .andExpect(jsonPath("$.duration").value(70));

    }

    @Test
    void whenCreatingFilm_shouldFailOnEmptyName() throws Exception {
        Film notValidFilm = new Film();
        notValidFilm.setName("");
        notValidFilm.setDescription("description");
        notValidFilm.setReleaseDate(LocalDate.of(2007,7,7));
        notValidFilm.setDuration(70);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingFilm_shouldFailOnNullName() throws Exception {
        Film notValidFilm = new Film();
        notValidFilm.setName(null);
        notValidFilm.setDescription("description");
        notValidFilm.setReleaseDate(LocalDate.of(2007,7,7));
        notValidFilm.setDuration(70);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingFilm_shouldFailOnLongDescription() throws Exception {
        Film notValidFilm = new Film();
        notValidFilm.setName("film");
        notValidFilm.setDescription("7".repeat(201));
        notValidFilm.setReleaseDate(LocalDate.of(2007,7,7));
        notValidFilm.setDuration(70);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingFilm_shouldFailOnZeroDuration() throws Exception {
        Film notValidFilm = new Film();
        notValidFilm.setName("film");
        notValidFilm.setDescription("description");
        notValidFilm.setReleaseDate(LocalDate.of(2007,7,7));
        notValidFilm.setDuration(0);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingFilm_shouldFailOnNegativeDuration() throws Exception {
        Film notValidFilm = new Film();
        notValidFilm.setName("film");
        notValidFilm.setDescription("description");
        notValidFilm.setReleaseDate(LocalDate.of(2007,7,7));
        notValidFilm.setDuration(-1);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingFilm_shouldFailOnEmptyRequest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateFilmWithValidFields() throws Exception {
        Film createdFilm = new Film();
        createdFilm.setName("film1");
        createdFilm.setDescription("description1");
        createdFilm.setReleaseDate(LocalDate.of(2007,7,7));
        createdFilm.setDuration(70);
        Film savedFilm = filmService.create(createdFilm);
        Long id = savedFilm.getId();

        Film updateFilm = new Film();
        updateFilm.setId(id);
        updateFilm.setName("film2");
        updateFilm.setDescription("description2");
        updateFilm.setReleaseDate(LocalDate.of(2008,8,8));
        updateFilm.setDuration(80);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("film2"))
                .andExpect(jsonPath("$.description").value("description2"))
                .andExpect(jsonPath("$.releaseDate").value("2008-08-08"))
                .andExpect(jsonPath("$.duration").value(80));
    }

    @Test
    void whenUpdatingFilm_shouldFailOnUnknownId() throws Exception {
        Film updateFilm = new Film();
        updateFilm.setId(999L);
        updateFilm.setName("film2");
        updateFilm.setDescription("description2");
        updateFilm.setReleaseDate(LocalDate.of(2008,8,8));
        updateFilm.setDuration(80);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilm)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllFilms() throws Exception {
        Film validFilm = new Film();
        validFilm.setName("film");
        validFilm.setDescription("description");
        validFilm.setReleaseDate(LocalDate.of(2007,7,7));
        validFilm.setDuration(70);
        filmService.create(validFilm);

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void shouldReturnPopularFilms() throws Exception {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("user1234");
        user.setName("Lexa");
        user.setBirthday(LocalDate.of(2000,1,1));
        User savedUser = userService.create(user);
        Long userId = savedUser.getId();

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("description1");
        film1.setReleaseDate(LocalDate.of(2007,7,7));
        film1.setDuration(70);
        Film savedFilm1 = filmService.create(film1);

        Film film2 = new Film();
        film2.setName("film2");
        film2.setDescription("description2");
        film2.setReleaseDate(LocalDate.of(2008,8,8));
        film2.setDuration(80);
        Film savedFilm2 = filmService.create(film2);

        mockMvc.perform(put("/films/{id}/like/{userId}", savedFilm1.getId(), userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/popular?count=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("[0].id").value(savedFilm1.getId()))
                .andExpect(jsonPath("[1].id").value(savedFilm2.getId()));

        String response = mockMvc.perform(get("/films/popular?count=2"))
                .andReturn().getResponse().getContentAsString();
        List<Film> popularFilms = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Film.class));
        assertEquals(savedFilm1.getId(), popularFilms.get(0).getId());
    }

    @Test
    void shouldAddValidLike() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2007, 7, 7));
        film.setDuration(70);
        Film savedFilm = filmService.create(film);
        Long filmId = savedFilm.getId();

        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("user1234");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User savedUser = userService.create(user);
        Long userId = savedUser.getId();

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        assert savedFilm.getLikes().contains(userId);
    }

    @Test
    void shouldFailOnAddingLike_toUnknownFilm() throws Exception {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("user1234");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User savedUser = userService.create(user);
        Long userId = savedUser.getId();

        mockMvc.perform(put("/films/{id}/like/{userId}", 999L, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailOnAddingLike_toUnknownUser() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2007, 7, 7));
        film.setDuration(70);
        filmService.create(film);

        mockMvc.perform(put("/films/{id}/like/{userId}", 1L, 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveValidLike() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2007, 7, 7));
        film.setDuration(70);
        Film savedFilm = filmService.create(film);
        Long filmId = savedFilm.getId();

        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("user1234");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User savedUser = userService.create(user);
        Long userId = savedUser.getId();

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        assert !savedFilm.getLikes().contains(userId);
    }

    @Test
    void shouldFailOnRemovingLike_onUnknownFilm() throws Exception {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("user1234");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.create(user);

        mockMvc.perform(delete("/films/{id}/like/{userId}", 999L, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailOnRemovingLike_onUnknownUser() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2007, 7, 7));
        film.setDuration(70);
        filmService.create(film);

        mockMvc.perform(delete("/films/{id}/like/{userId}", 1L, 999L))
                .andExpect(status().isNotFound());
    }
}