package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Validated
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<RatingDto> findAll() {
        return ratingService.findAll();
    }

    @GetMapping("/{id}")
    public RatingDto getById(Long id) {
        return ratingService.getById(id);
    }
}
