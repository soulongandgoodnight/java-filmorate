package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.storage.relation.RelationRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private final UserMapper mapper;

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


    public Collection<UserDto> getFriends(Long userId) {
        var relations = relationRepository.getAllByUserId(userId);
        var confirmedFriends = getFriendsSet(relations);
        var result = userRepository.getByIds(confirmedFriends);
        return result.stream().map(mapper::mapToDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<UserDto> getCommonFriends(Long userId, Long otherId) {
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

    public UserDto create(NewUserRequest newUserRequest) {
        var user = mapper.mapToUser(newUserRequest);
        var result = userRepository.create(user);
        return mapper.mapToDto(result);
    }

    public UserDto update(UpdateUserRequest updateUserRequest) {
        var user = userRepository.getById(updateUserRequest.getId());
        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + updateUserRequest.getId() + " not found");
        }
        mapper.updateUserFields(user.get(), updateUserRequest);
        var result = userRepository.update(user.get());
        return mapper.mapToDto(result);
    }

    public UserDto getById(Long id) {
        var user = userRepository.getById(id);
        if (user.isEmpty()) throw new NotFoundException("User with id " + id + " not found");
        return mapper.mapToDto(user.get());
    }

    public Collection<UserDto> findAll() {
        var users = userRepository.findAll();
        return users.stream().map(mapper::mapToDto).collect(Collectors.toCollection(ArrayList::new));
    }
}
