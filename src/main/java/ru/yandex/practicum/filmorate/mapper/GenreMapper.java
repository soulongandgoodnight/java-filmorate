package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class GenreMapper {
    public GenreDto toDto(Genre genre) {
        var result = new GenreDto();
        result.setId(genre.getId());
        result.setName(genre.getName());

        return result;
    }
}
