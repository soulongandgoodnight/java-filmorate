package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class Genre {
    @NotNull(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private Long id;

    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;
}
