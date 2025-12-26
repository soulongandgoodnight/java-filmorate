package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;

    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Электронная почта должна быть валидной (содержать @)")
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Электронная почта не может быть пустой")
    private String email;

    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @NotNull(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Дата рождения не может быть null")
    @Past(groups = Marker.OnCreate.class, message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Map<Long, Relation> relations = new HashMap<>();
}
