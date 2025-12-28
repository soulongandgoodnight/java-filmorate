package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class RatingMapper {
    public RatingDto mapToDto(Rating rating) {
        var result = new RatingDto();
        result.setId(rating.getId());
        result.setName(rating.getName());

        return result;
    }
}
