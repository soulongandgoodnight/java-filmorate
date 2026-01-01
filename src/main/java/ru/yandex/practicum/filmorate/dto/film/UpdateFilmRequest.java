package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotNull
    private Long id;

    private String name;

    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;

    private Set<GenreDto> genres = new HashSet<>();

    private RatingDto mpa;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasDuration() {
        return !(duration == null);
    }

    public boolean hasGenres() {
        return !(genres == null);
    }

    public boolean hasRating() {
        return !(mpa == null || mpa.getId() == null);
    }
}
