package ru.yandex.practicum.filmorate.storage.user;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.relation.RelationRepository;
import ru.yandex.practicum.filmorate.storage.relation.RelationRowMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@JdbcTest
@AutoConfigureTestDatabase
public class UserRepositoryApplicationTests {
    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private ArrayList<User> users;

    public UserRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.userRepository = new UserRepository(jdbc, new UserRowMapper());
        this.relationRepository = new RelationRepository(jdbc, new RelationRowMapper());

    }

    @BeforeEach
    public void beforeEach() {
        users = new ArrayList<>();
        createEnoughUsers(2);
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
    public void testfindFriendsByUserId() {
        createEnoughUsers(100);
        var firstUserRelationsCount = 50;
        var secondUserRelationsCount = 23;
        var thirdUserRelationsCount = 78;
        var firstUser = users.get(0);
        var secondUser = users.get(1);
        var thirdUser = users.get(2);
        createUnconfirmedRelations(firstUser.getId(), firstUserRelationsCount);
        createUnconfirmedRelations(secondUser.getId(), secondUserRelationsCount);
        createUnconfirmedRelations(thirdUser.getId(), thirdUserRelationsCount);

        var firstUserRelations = userRepository.findFriends(firstUser.getId());
        var secondUserRelations = userRepository.findFriends(secondUser.getId());
        var thirdUserRelations = userRepository.findFriends(thirdUser.getId());
        assertThat(firstUserRelations.size()).isEqualTo(firstUserRelationsCount);
        assertThat(secondUserRelations.size()).isEqualTo(secondUserRelationsCount);
        assertThat(thirdUserRelations.size()).isEqualTo(thirdUserRelationsCount);
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
        createEnoughUsers(100);
        var actualUsers = userRepository.findAll();
        assertThat(actualUsers.size()).isEqualTo(users.size());
        assertThat(actualUsers.stream().sorted(Comparator.comparingLong(User::getId)).toList())
                .isEqualTo(users.stream().sorted(Comparator.comparingLong(User::getId)).toList());
    }

    private void createEnoughUsers(int usersCount) {
        var usersToCreateCount = usersCount - users.size();
        for (int i = 0; i < usersToCreateCount; i++) {
            users.add(createAndSaveUser());
        }

        Collections.shuffle(users);
    }

    private void createUnconfirmedRelations(long userId, int count) {
        // Для N связей нужно N + 1 человек, так как один из них - это сам пользователь
        createEnoughUsers(count + 1);
        var userIdsToFollow = new ArrayList<>(users.stream().map(User::getId).filter(id -> id != userId).limit(count).toList());
        Collections.shuffle(userIdsToFollow);
        for (var followedId : userIdsToFollow) {
            var relation = new Relation();
            relation.setFollowingUserId(userId);
            relation.setFollowedUserId(followedId);
            relationRepository.addRelation(relation);
        }
    }

    private User createAndSaveUser() {
        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getRelations))
                .create();
        return userRepository.create(user);
    }
}
