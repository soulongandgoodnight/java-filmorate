package ru.yandex.practicum.filmorate.storage.user;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@JdbcTest
@AutoConfigureTestDatabase
public class UserRepositoryApplicationTests {
    private final UserRepository userRepository;

    public UserRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.userRepository = new UserRepository(jdbc, new UserRowMapper());
    }

    @Test
    public void testCreateAndGetUser() {
        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getRelations))
                .create();

        var resultUser = userRepository.create(user);
        assertThat(resultUser.getId()).isNotNull();
        var recentlyCreatedUser = userRepository.getById(resultUser.getId());
        assertThat(recentlyCreatedUser).isPresent();
        assertThat(recentlyCreatedUser.get()).isEqualTo(user);
    }

    @Test
    public void testCreateAndUpdateAndGetUser() {
        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getRelations))
                .create();

        var resultUser = userRepository.create(user);
        assertThat(resultUser.getId()).isNotNull();
        resultUser.setLogin("updated " + resultUser.getLogin());
        resultUser.setEmail("updated " + resultUser.getEmail());
        resultUser.setName("uodated " + resultUser.getName());
        resultUser.setBirthday(resultUser.getBirthday().minusYears(3));
        userRepository.update(user);
        var recentlyUpdatedUser = userRepository.getById(resultUser.getId());
        assertThat(recentlyUpdatedUser).isPresent();
        assertThat(recentlyUpdatedUser.get()).isEqualTo(resultUser);
    }

    @Test
    public void testCreateAndDeleteUser() {
        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getRelations))
                .create();

        var resultUser = userRepository.create(user);
        assertThat(resultUser.getId()).isNotNull();
        var deletionResult = userRepository.delete(user.getId());
        assertThat(deletionResult).isTrue();

        var recentlyCreatedUser = userRepository.getById(user.getId());
        assertThat(recentlyCreatedUser).isNotPresent();

        var oneMoreDeletionAttempt = userRepository.delete(user.getId());
        assertThat(oneMoreDeletionAttempt).isFalse();
    }

    @Test
    public void testGetByIds() {
        var totalCount = 10;
        var countToGet = 5;
        var users = Instancio.ofList(User.class)
                .size(totalCount)
                .ignore(field(User::getId))
                .ignore(field(User::getRelations))
                .create();

        for (var user : users) {
            userRepository.create(user);
        }

        var usersById = users.stream().collect(Collectors.toMap(User::getId, v -> v));
        var idsToGet = usersById.keySet().stream().limit(countToGet).collect(Collectors.toSet());

        var actualUsers = userRepository.getByIds(idsToGet);
        assertThat(actualUsers).isNotNull();
        assertThat(actualUsers.size()).isEqualTo(countToGet);

        for (var actualUser : actualUsers) {
            var expectedUser = usersById.get(actualUser.getId());
            assertThat(actualUser).isEqualTo(expectedUser);
        }
    }

    @Test
    public void testFindAll() {
        var totalCount = 10;
        var users = Instancio.ofList(User.class)
                .size(totalCount)
                .ignore(field(User::getId))
                .ignore(field(User::getRelations))
                .create();

        for (var user : users) {
            userRepository.create(user);
        }

        var actualUsers = userRepository.findAll();
        assertThat(actualUsers.size()).isEqualTo(totalCount);
        assertThat(actualUsers).isEqualTo(users);
    }

}
