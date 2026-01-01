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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private final UserMapper mapper;

    public void addFriend(Long userId, Long friendId) {
        var userOptional = userRepository.getById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        var friendOptional = userRepository.getById(friendId);
        if (friendOptional.isEmpty()) {
            throw new NotFoundException("Friend with id " + userId + " not found");
        }
        var userRelationOptional = relationRepository.findRelation(userId, friendId);

        if (userRelationOptional.isEmpty()) {
            var newRelation = new Relation();
            newRelation.setFollowingUserId(userId);
            newRelation.setFollowedUserId(friendId);
            relationRepository.addRelation(newRelation);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        var userOptional = userRepository.getById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        var friendOptional = userRepository.getById(friendId);
        if (friendOptional.isEmpty()) {
            throw new NotFoundException("Friend with id " + userId + " not found");
        }

        var userRelation = relationRepository.findRelation(userId, friendId);
        userRelation.ifPresent(relationRepository::removeRelation);
    }


    public Collection<UserDto> getFriends(Long userId) {
        var userOptional = userRepository.getById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        var relations = relationRepository.getAllByUserId(userId);
        var confirmedFriends = getFriendIdsSet(relations);
        var result = userRepository.getByIds(confirmedFriends);
        return result.stream().map(mapper::mapToDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<UserDto> getCommonFriends(Long userId, Long otherId) {
        var userRelations = relationRepository.getAllByUserId(userId);
        var otherPersonRelations = relationRepository.getAllByUserId(otherId);
        var userFriends = getFriendIdsSet(userRelations);
        var otherPersonFriends = getFriendIdsSet(otherPersonRelations);

        userFriends.retainAll(otherPersonFriends);
        return userFriends.stream().map(this::getById).collect(Collectors.toList());
    }

    private Set<Long> getFriendIdsSet(Collection<Relation> relations) {
        return relations.stream().map(Relation::getFollowedUserId).collect(Collectors.toSet());
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
