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
        relationToAdd.setIsFriendshipConfirmed(true);
        relationRepository.addRelation(relationToAdd);

        var relationsByUser = relationRepository.getAllByUserId(followingId);
        assertThat(relationsByUser.size()).isEqualTo(1);
        assertThat(relationToAdd).isEqualTo(relationsByUser.stream().findFirst().get());
        for (int i = 0; i < 10; i++) {
            relationRepository.addRelation(relationToAdd);
        }

        relationsByUser = relationRepository.getAllByUserId(followingId);
        assertThat(relationsByUser.size()).isEqualTo(1);
        assertThat(relationToAdd).isEqualTo(relationsByUser.stream().findFirst().get());
    }

    @Test
    public void testAddAndThenRemoveRelation() {
        var followingId = users.get(0).getId();
        var followedId = users.get(1).getId();

        var relationToAdd = new Relation();
        relationToAdd.setFollowingUserId(followingId);
        relationToAdd.setFollowedUserId(followedId);
        relationToAdd.setIsFriendshipConfirmed(true);
        relationRepository.addRelation(relationToAdd);
        var relationsByUser = relationRepository.getAllByUserId(followingId);
        assertThat(relationsByUser.size()).isEqualTo(1);
        assertThat(relationToAdd).isEqualTo(relationsByUser.stream().findFirst().get());

        relationRepository.removeRelation(relationToAdd);
        relationsByUser = relationRepository.getAllByUserId(followingId);
        assertThat(relationsByUser.size()).isEqualTo(0);
        for (int i = 0; i < 10; i++) {
            relationRepository.removeRelation(relationToAdd);
        }
    }

    @Test
    public void testAddAndThenUpdateRelation() {
        var followingId = users.get(0).getId();
        var followedId = users.get(1).getId();

        var relation = new Relation();
        relation.setFollowingUserId(followingId);
        relation.setFollowedUserId(followedId);
        relation.setIsFriendshipConfirmed(true);
        relationRepository.addRelation(relation);
        var relationsByUser = relationRepository.getAllByUserId(followingId);
        assertThat(relationsByUser.size()).isEqualTo(1);
        assertThat(relation).isEqualTo(relationsByUser.stream().findFirst().get());

        relation.setIsFriendshipConfirmed(!relation.getIsFriendshipConfirmed());
        relationRepository.updateRelation(relation);
        relationsByUser = relationRepository.getAllByUserId(followingId);
        assertThat(relationsByUser.size()).isEqualTo(1);
        assertThat(relation).isEqualTo(relationsByUser.stream().findFirst().get());
    }

    @Test
    public void testGetAllByUesrId() {
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

        var firstUserRelations = relationRepository.getAllByUserId(firstUser.getId());
        var secondUserRelations = relationRepository.getAllByUserId(secondUser.getId());
        var thirdUserRelations = relationRepository.getAllByUserId(thirdUser.getId());
        assertThat(firstUserRelations.size()).isEqualTo(firstUserRelationsCount);
        assertThat(secondUserRelations.size()).isEqualTo(secondUserRelationsCount);
        assertThat(thirdUserRelations.size()).isEqualTo(thirdUserRelationsCount);
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
        relationToFind.setIsFriendshipConfirmed(false);
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
            relation.setIsFriendshipConfirmed(false);
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
