package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper mapper;

    public Collection<GenreDto> findAll() {
        var genres = genreRepository.findAll();
        return genres.stream().map(mapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public GenreDto getById(Long id) {
        var genre = genreRepository.findById(id);
        if (genre.isEmpty()) {
            throw new NotFoundException("Genre with id " + id + " not found");
        }

        return mapper.toDto(genre.get());
    }
}
