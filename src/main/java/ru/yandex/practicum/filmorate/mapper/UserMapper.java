package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserRelationDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public final class UserMapper {
    public User mapToUser(NewUserRequest dto) {
        var user = new User();
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        if (dto.mustUseLoginAsName()) {
            user.setName(dto.getLogin());
        } else {
            user.setName(dto.getName());
        }
        user.setBirthday(dto.getBirthday());
        user.setRelations(new HashMap<>());

        return user;
    }

    public User updateUserFields(User user, UpdateUserRequest dto) {
        if (dto.hasEmail()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.hasLogin()) {
            user.setLogin(dto.getLogin());
        }

        if (dto.hasBirthday()) {
            user.setBirthday(dto.getBirthday());
        }

        if (dto.mustUseLoginAsName()) {
            user.setName(dto.getLogin());
        } else {
            user.setName(dto.getName());
        }

        return user;
    }

    public UserDto mapToDto(User user) {
        var dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setBirthday(user.getBirthday());
        dto.setName(user.getName());
        var relations = user.getRelations().values().stream().map(r -> {
            var result = new UserRelationDto();
            result.setFollowedUserId(r.getFollowedUserId());
            result.setIsFriendshipConfirmed(r.getIsFriendshipConfirmed());
            return result;
        }).collect(Collectors.toCollection(ArrayList::new));
        dto.setRelations(relations);
        return dto;
    }
}