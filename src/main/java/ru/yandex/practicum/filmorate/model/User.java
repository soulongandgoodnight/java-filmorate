package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;

    @Email(groups = Marker.OnCreate.class, message = "Электронная почта должна быть валидной (содержать @)")
    @NotBlank(groups = Marker.OnCreate.class, message = "Электронная почта не может быть пустой")
    private String email;

    @NotBlank(groups = Marker.OnCreate.class, message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", groups = Marker.OnCreate.class, message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @Past(groups = Marker.OnCreate.class, message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
