package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Relation {
    private Long followingUserId;
    private Long followedUserId;
    private Boolean isFriendshipConfirmed;
}

