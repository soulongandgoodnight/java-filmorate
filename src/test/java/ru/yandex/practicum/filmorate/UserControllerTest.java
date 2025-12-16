package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUserWithValidFields() throws Exception {
        User validUser = new User();
        validUser.setEmail("user@yandex.ru");
        validUser.setLogin("user1234");
        validUser.setName("Lexa");
        validUser.setBirthday(LocalDate.of(2000,1,1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("user@yandex.ru"))
                .andExpect(jsonPath("$.login").value("user1234"))
                .andExpect(jsonPath("$.name").value("Lexa"))
                .andExpect(jsonPath("$.birthday").value("2000-01-01"));
    }

    @Test
    void whenCreatingUser_shouldFailOnEmptyEmail() throws Exception {
        User notValidUser = new User();
        notValidUser.setEmail("");
        notValidUser.setLogin("user1234");
        notValidUser.setName("Lexa");
        notValidUser.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingUser_shouldFailOnNullEmail() throws Exception {
        User notValidUser = new User();
        notValidUser.setEmail(null);
        notValidUser.setLogin("user1234");
        notValidUser.setName("Lexa");
        notValidUser.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingUser_shouldFailOnInvalidEmail_withoutAt() throws Exception {
        User notValidUser = new User();
        notValidUser.setEmail("user.ru");
        notValidUser.setLogin("user1234");
        notValidUser.setName("Lexa");
        notValidUser.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidUser)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void whenCreatingUser_shouldFailOnEmptyLogin() throws Exception {
        User notValidUser = new User();
        notValidUser.setEmail("user@yandex.ru");
        notValidUser.setLogin("");
        notValidUser.setName("Lexa");
        notValidUser.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingUser_shouldFailOnLoginWithSpace() throws Exception {
        User notValidUser = new User();
        notValidUser.setEmail("user@yandex.ru");
        notValidUser.setLogin("user 1234");
        notValidUser.setName("Lexa");
        notValidUser.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingUser_withEmptyName_shouldSetNameToLogin() throws Exception {
        User validUser = new User();
        validUser.setEmail("user@yandex.ru");
        validUser.setLogin("user1234");
        validUser.setName("");
        validUser.setBirthday(LocalDate.of(2000,1,1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user1234"));
    }

    @Test
    void whenCreatingUser_withNullName_shouldSetNameToLogin() throws Exception {
        User validUser = new User();
        validUser.setEmail("user@yandex.ru");
        validUser.setLogin("user1234");
        validUser.setName(null);
        validUser.setBirthday(LocalDate.of(2000,1,1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user1234"));
    }

    @Test
    void whenCreatingUser_shouldFailOnFutureBirthday() throws Exception {
        User notValidUser = new User();
        notValidUser.setEmail("user@yandex.ru");
        notValidUser.setLogin("user1234");
        notValidUser.setName("Lexa");
        notValidUser.setBirthday(LocalDate.of(2100, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingUser_shouldThrowValidationException_onNullBirthday() {
        User notValidUser = new User();
        notValidUser.setEmail("user@yandex.ru");
        notValidUser.setLogin("user1234");
        notValidUser.setName("Lexa");
        notValidUser.setBirthday(null);

        UserController controller = new UserController();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(notValidUser);
        });

        assertEquals("Дата рождения не может быть null", exception.getMessage());
    }

    @Test
    void whenCreatingUser_shouldFailOnEmptyRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdatingUser_shouldThrowValidationException_onNullId() {
        User notValidUser = new User();
        notValidUser.setId(null);
        notValidUser.setEmail("user@yandex.ru");
        notValidUser.setLogin("user1234");
        notValidUser.setName("Lexa");
        notValidUser.setBirthday(LocalDate.of(2000, 1, 1));

        UserController controller = new UserController();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.update(notValidUser);
        });

        assertEquals("Пользователь с ID [не указан] не найден", exception.getMessage());
    }
}
