package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.relation.RelationRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    @Qualifier("userDbStorage")
    private final UserRepository userRepository;
    private final RelationRepository relationRepository;

    public void addFriend(Long userId, Long friendId) {
        var userRelationOptional = relationRepository.findRelation(userId, friendId);
        var friendRelationOptional = relationRepository.findRelation(friendId, userId);

        if (userRelationOptional.isEmpty() && friendRelationOptional.isEmpty()) {
            var newRelation = new Relation();
            newRelation.setFollowingUserId(userId);
            newRelation.setFollowedUserId(friendId);
            newRelation.setIsFriendshipConfirmed(false);
            relationRepository.addRelation(newRelation);
            return;
        }

        if (friendRelationOptional.isPresent() && !friendRelationOptional.get().getIsFriendshipConfirmed()) {
            var friendRelation = friendRelationOptional.get();
            friendRelation.setIsFriendshipConfirmed(true);
            relationRepository.updateRelation(friendRelation);
            if (userRelationOptional.isEmpty()) {
                var userRelation = new Relation();
                userRelation.setFollowingUserId(userId);
                userRelation.setFollowedUserId(friendId);
                userRelation.setIsFriendshipConfirmed(true);
                relationRepository.addRelation(userRelation);
            } else {
                var userRelation = userRelationOptional.get();
                userRelation.setIsFriendshipConfirmed(true);
                relationRepository.updateRelation(userRelation);
            }
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        var userRelation = relationRepository.findRelation(userId, friendId);
        var friendRelation = relationRepository.findRelation(friendId, userId);
        userRelation.ifPresent(relationRepository::removeRelation);
        friendRelation.ifPresent(relationRepository::removeRelation);
    }


    public Collection<User> getFriends(Long userId) {
        var relations = relationRepository.getAllByUserId(userId);
        var confirmedFriends = getFriendsSet(relations);
        return userRepository.getByIds(confirmedFriends);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        var userRelations = relationRepository.getAllByUserId(userId);
        var otherPersonRelations = relationRepository.getAllByUserId(otherId);
        var userFriends = getFriendsSet(userRelations);
        var otherPersonFriends = getFriendsSet(otherPersonRelations);

        userFriends.retainAll(otherPersonFriends);
        return userFriends.stream().map(this::getById).collect(Collectors.toList());
    }

    private Set<Long> getFriendsSet(Collection<Relation> relations) {
        Set<Long> result = new HashSet<>();
        for (var relation : relations) {
            if (relation.getIsFriendshipConfirmed()) {
                result.add(relation.getFollowedUserId());
            }
        }

        return result;
    }

    public User create(User user) {
        return userRepository.create(user);
    }

    public User update(User user) {
        return userRepository.update(user);
    }

    public User getById(Long id) {
        var user = userRepository.getById(id);
        if (user.isEmpty()) throw new NotFoundException("User with id " + id + " not found");
        return user.get();
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }
}
