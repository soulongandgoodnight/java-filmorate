package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void whenCreatingFilm_shouldThrowValidationException_onEarlyReleaseDate() throws Exception {
        Film notValidFilm = new Film();
        notValidFilm.setName("film");
        notValidFilm.setDescription("description");
        notValidFilm.setReleaseDate(LocalDate.of(1895,12,27));
        notValidFilm.setDuration(70);

        FilmController controller = new FilmController();
        ValidationException exception = assertThrows(ValidationException.class,() -> {
            controller.create(notValidFilm);
        });

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года",  exception.getMessage());
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
    void whenUpdatingFilm_shouldThrowValidationException_onNullId() throws Exception {
        Film notValidFilm = new Film();
        notValidFilm.setId(null);
        notValidFilm.setName("film");
        notValidFilm.setDescription("description");
        notValidFilm.setReleaseDate(LocalDate.of(2007,7,7));
        notValidFilm.setDuration(70);

        FilmController controller = new FilmController();
        ValidationException exception = assertThrows(ValidationException.class,() -> {
            controller.update(notValidFilm);
        });

        assertEquals("Фильм с ID [не указан] не найден",  exception.getMessage());
    }


}