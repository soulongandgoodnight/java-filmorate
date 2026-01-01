package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.storage.rating.RatingRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper mapper;

    public Collection<RatingDto> findAll() {
        var ratings = ratingRepository.findAll();
        return ratings.stream().map(mapper::mapToDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public RatingDto getById(Long id) {
        var rating = ratingRepository.findById(id);
        if (rating.isEmpty()) {
            throw new NotFoundException("Rating with id " + id + " not found");
        }

        return mapper.mapToDto(rating.get());
    }
}
