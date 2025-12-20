package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;

    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Описание не может быть длиннее 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Продолжительность должна быть положительным числом")
    private Integer duration;

    private Set<Long> likes = new HashSet<>();
}
