package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Marker;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotNull
    private Long id;

    @Email(message = "Электронная почта должна быть валидной (содержать @)")
    private String email;

    @Pattern(regexp = "^\\S*$", groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @Past(groups = Marker.OnCreate.class, message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public boolean hasEmail() {
        return email != null;
    }

    public boolean hasLogin() {
        return login != null;
    }

    public boolean hasBirthday() {
        return birthday != null;
    }

    public boolean hasName() {
        return name != null;
    }

    public boolean mustUseLoginAsName() {
        return !hasName() && hasLogin();
    }
}
