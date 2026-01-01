package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

@Data
public class UserRelationDto {
    private Long followedUserId;
    private Boolean isFriendshipConfirmed;
}
