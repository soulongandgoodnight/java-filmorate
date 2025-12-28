package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String login;
    private LocalDate birthday;
    private Collection<UserRelationDto> relations;
}

