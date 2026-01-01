package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserRelationDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userStorage;

    @BeforeEach
    void clearUsers() {
        List<Long> userIds = userService.findAll().stream()
                .map(UserDto::getId)
                .toList();
        userIds.forEach(userStorage::delete);
    }

    @Test
    void shouldCreateUserWithValidFields() throws Exception {
        User validUser = new User();
        validUser.setEmail("user@yandex.ru");
        validUser.setLogin("user1234");
        validUser.setName("Lexa");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

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
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

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
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

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
    void whenCreatingUser_shouldFailOnEmptyRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUserWithValidFields() throws Exception {
        var user = new NewUserRequest();
        user.setEmail("user@yandex.ru");
        user.setLogin("user1234");
        user.setName("Lexa");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        var savedUser = userService.create(user);
        Long id = savedUser.getId();

        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setEmail("updateUser@yandex.ru");
        updateUser.setLogin("user5678");
        updateUser.setName("Lepexa");
        updateUser.setBirthday(LocalDate.of(1999, 12, 31));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updateUser@yandex.ru"))
                .andExpect(jsonPath("$.login").value("user5678"))
                .andExpect(jsonPath("$.name").value("Lepexa"))
                .andExpect(jsonPath("$.birthday").value("1999-12-31"))
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void whenUpdatingFilm_shouldFailOnUnknownUser() throws Exception {
        User updateUser = new User();
        updateUser.setId(999L);
        updateUser.setEmail("updateUser@yandex.ru");
        updateUser.setLogin("user5678");
        updateUser.setName("Lepexa");
        updateUser.setBirthday(LocalDate.of(1999, 12, 31));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        var user = new NewUserRequest();
        user.setEmail("user@yandex.ru");
        user.setLogin("user1234");
        user.setName("Lexa");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.create(user);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void whenAddFriend_shouldFailOnUnknownId() throws Exception {
        var user2 = new NewUserRequest();
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("user5678");
        user2.setName("Lepexa");
        user2.setBirthday(LocalDate.of(1999, 12, 31));
        userService.create(user2);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", 999L, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnFriend() throws Exception {
        var user1 = new NewUserRequest();
        user1.setEmail("user1@yandex.ru");
        user1.setLogin("user1234");
        user1.setName("Lexa");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        var savedUser1 = userService.create(user1);

        mockMvc.perform(get("/users/{id}/friends", savedUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        var user2 = new NewUserRequest();
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("user5678");
        user2.setName("Lepexa");
        user2.setBirthday(LocalDate.of(1999, 12, 31));
        var savedUser2 = userService.create(user2);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", savedUser1.getId(), savedUser2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends", savedUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void whenReturnFriend_shouldFailOnUnknownId() throws Exception {
        mockMvc.perform(get("/users/{id}/friends", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveFriend() throws Exception {
        var user1 = new NewUserRequest();
        user1.setEmail("user1@yandex.ru");
        user1.setLogin("user1234");
        user1.setName("Lexa");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        var savedUser1 = userService.create(user1);

        var user2 = new NewUserRequest();
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("user5678");
        user2.setName("Lepexa");
        user2.setBirthday(LocalDate.of(1999, 12, 31));
        var savedUser2 = userService.create(user2);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", savedUser1.getId(), savedUser2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", savedUser1.getId(), savedUser2.getId()))
                .andExpect(status().isOk());

        assert !userService.getById(savedUser1.getId()).getRelations().stream().map(UserRelationDto::getFollowedUserId).toList().contains(savedUser2.getId());
        assert !userService.getById(savedUser2.getId()).getRelations().stream().map(UserRelationDto::getFollowedUserId).toList().contains(savedUser1.getId());
    }

    @Test
    void whenRemoveFriend_shouldPassOnNotFriend() throws Exception {
        var user1 = new NewUserRequest();
        user1.setEmail("user1@yandex.ru");
        user1.setLogin("user1234");
        user1.setName("Lexa");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        var savedUser1 = userService.create(user1);

        var user2 = new NewUserRequest();
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("user5678");
        user2.setName("Lepexa");
        user2.setBirthday(LocalDate.of(1999, 12, 31));
        var savedUser2 = userService.create(user2);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", savedUser1.getId(), savedUser2.getId()))
                .andExpect(status().isOk());

        assert userService.getById(savedUser1.getId()).getRelations().isEmpty();
        assert userService.getById(savedUser2.getId()).getRelations().isEmpty();
    }

    @Test
    void whenRemoveFriend_shouldFailOnUnknownId() throws Exception {
        var user2 = new NewUserRequest();
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("user5678");
        user2.setName("Lepexa");
        user2.setBirthday(LocalDate.of(1999, 12, 31));
        userService.create(user2);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", 999L, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenRemoveFriend_shouldFailOnUnknownFriendId() throws Exception {
        var user1 = new NewUserRequest();
        user1.setEmail("user1@yandex.ru");
        user1.setLogin("user1234");
        user1.setName("Lexa");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        var savedUser1 = userService.create(user1);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", savedUser1.getId(), 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCommonFriends() throws Exception {
        var user1 = new NewUserRequest();
        user1.setEmail("user1@yandex.ru");
        user1.setLogin("user1234");
        user1.setName("Lexa");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        var savedUser1 = userService.create(user1);

        var user2 = new NewUserRequest();
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("user5678");
        user2.setName("Lepexa");
        user2.setBirthday(LocalDate.of(1999, 12, 31));
        var savedUser2 = userService.create(user2);

        var user3 = new NewUserRequest();
        user3.setEmail("user3@yandex.ru");
        user3.setLogin("user9101");
        user3.setName("Kartoxa");
        user3.setBirthday(LocalDate.of(1989, 5, 5));
        var savedUser3 = userService.create(user3);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", savedUser1.getId(), savedUser3.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", savedUser2.getId(), savedUser3.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", savedUser1.getId(), savedUser2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("[0].id").value(savedUser3.getId()));
    }
}