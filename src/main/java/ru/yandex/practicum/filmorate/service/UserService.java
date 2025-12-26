package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.relation.RelationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final RelationStorage relationStorage;

    public void addFriend(Long userId, Long friendId) {
        var userRelation = relationStorage.getByUserId(userId);
        var friendRelation = relationStorage.getByUserId(friendId);

        if (userRelation == null && friendRelation == null) {
            var newRelation = new Relation();
            newRelation.setUserId(userId);
            newRelation.setFriendId(friendId);
            newRelation.setStatus(Relation.FriendshipStatus.PENDING);
            relationStorage.addRelation(newRelation);
            return;
        }

        if (friendRelation != null && friendRelation.getStatus() == Relation.FriendshipStatus.PENDING) {
            friendRelation.setStatus(Relation.FriendshipStatus.CONFIRMED);
            relationStorage.updateRelation(friendRelation);
            if (userRelation == null) {
                userRelation = new Relation();
                userRelation.setUserId(userId);
                userRelation.setFriendId(friendId);
                userRelation.setStatus(Relation.FriendshipStatus.CONFIRMED);
                relationStorage.addRelation(userRelation);
            } else {
                userRelation.setStatus(Relation.FriendshipStatus.CONFIRMED);
                relationStorage.updateRelation(userRelation);
            }
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        var userRelation = relationStorage.getByUserId(userId);
        var friendRelation = relationStorage.getByUserId(friendId);
        if (userRelation != null) {
            relationStorage.removeRelation(userRelation);
        }

        if (friendRelation != null) {
            relationStorage.removeRelation(friendRelation);
        }
    }


    public Collection<User> getFriends(Long userId) {
        var relations = relationStorage.getAllByUserId(userId);
        var confirmedFriends = getFriendsSet(relations);
        return userStorage.getByIds(confirmedFriends);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        var userRelations = relationStorage.getAllByUserId(userId);
        var otherPersonRelations = relationStorage.getAllByUserId(otherId);
        var userFriends = getFriendsSet(userRelations);
        var otherPersonFriends = getFriendsSet(otherPersonRelations);

        userFriends.retainAll(otherPersonFriends);
        return userFriends.stream().map(this::getById).collect(Collectors.toList());
    }

    private Set<Long> getFriendsSet(Collection<Relation> relations) {
        Set<Long> result = new HashSet<>();
        for (var relation : relations) {
            if (relation.getStatus() == Relation.FriendshipStatus.CONFIRMED) {
                result.add(relation.getFriendId());
            }
        }

        return result;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getById(Long id) {
        User user = userStorage.getById(id);
        if (user == null) throw new NotFoundException("User with id " + id + " not found");
        return user;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }
}
