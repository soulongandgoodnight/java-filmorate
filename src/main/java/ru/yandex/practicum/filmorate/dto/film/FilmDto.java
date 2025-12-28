package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Long> likes = new HashSet<>();
    private Set<Long> genreIds = new HashSet<>();
    private long ratingId;

}
