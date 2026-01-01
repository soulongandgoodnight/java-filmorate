package ru.yandex.practicum.filmorate.storage.relation;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@JdbcTest
@AutoConfigureTestDatabase
public class RelationRepositoryApplicationTests {
    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private ArrayList<User> users;

    public RelationRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.userRepository = new UserRepository(jdbc, new UserRowMapper());
        this.relationRepository = new RelationRepository(jdbc, new RelationRowMapper());
    }

    @BeforeEach
    public void beforeEach() {
        users = new ArrayList<>();
        createEnoughUsers(2);
    }

    @Test
    public void testAddRelationSeveralTimes() {
        var followingId = users.get(0).getId();
        var followedId = users.get(1).getId();

        var relationToAdd = new Relation();
        relationToAdd.setFollowingUserId(followingId);
        relationToAdd.setFollowedUserId(followedId);
        relationRepository.addRelation(relationToAdd);

        var userFriends = userRepository.findFriends(followingId);
        assertThat(userFriends.size()).isEqualTo(1);
        assertThat(relationToAdd.getFollowedUserId()).isEqualTo(userFriends.stream().findFirst().get().getId());
        for (int i = 0; i < 10; i++) {
            relationRepository.addRelation(relationToAdd);
        }

        userFriends = userRepository.findFriends(followingId);
        assertThat(userFriends.size()).isEqualTo(1);
        assertThat(relationToAdd.getFollowedUserId()).isEqualTo(userFriends.stream().findFirst().get().getId());
    }

    @Test
    public void testAddAndThenRemoveRelation() {
        var followingId = users.get(0).getId();
        var followedId = users.get(1).getId();

        var relationToAdd = new Relation();
        relationToAdd.setFollowingUserId(followingId);
        relationToAdd.setFollowedUserId(followedId);
        relationRepository.addRelation(relationToAdd);
        var relationsByUser = userRepository.findFriends(followingId);
        assertThat(relationsByUser.size()).isEqualTo(1);
        assertThat(relationToAdd.getFollowedUserId()).isEqualTo(relationsByUser.stream().findFirst().get().getId());

        relationRepository.removeRelation(relationToAdd);
        relationsByUser = userRepository.findFriends(followingId);
        assertThat(relationsByUser.size()).isEqualTo(0);
        for (int i = 0; i < 10; i++) {
            relationRepository.removeRelation(relationToAdd);
        }

        relationsByUser = userRepository.findFriends(followingId);
        assertThat(relationsByUser.size()).isEqualTo(0);
    }


    @Test
    public void testFindRelation() {
        createEnoughUsers(100);
        var rnd = new Random();
        for (var user : users) {
            createUnconfirmedRelations(user.getId(), rnd.nextInt(3, 5));
        }

        var relationToFind = new Relation();
        relationToFind.setFollowingUserId(users.get(50).getId());
        relationToFind.setFollowedUserId(users.get(78).getId());
        relationRepository.addRelation(relationToFind);
        var actualRelation = relationRepository.findRelation(relationToFind.getFollowingUserId(), relationToFind.getFollowedUserId());
        assertThat(actualRelation).isPresent();
        assertThat(actualRelation.get()).isEqualTo(relationToFind);
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
}
