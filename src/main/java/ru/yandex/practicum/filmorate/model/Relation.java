package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;

@Data
public class Relation {
    private Long userId;
    private Long friendId;
    private FriendshipStatus status;

    @Getter
    public enum FriendshipStatus {
        PENDING("Неподтверждённая"),
        CONFIRMED("Подтверждённая");

        private final String description;

        FriendshipStatus(String description) {
            this.description = description;
        }
    }
}

