package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Collection<GenreDto> genres;
    private RatingDto mpa;

}
