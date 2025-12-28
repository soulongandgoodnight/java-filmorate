package ru.yandex.practicum.filmorate.storage.genre;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
public class GenreRepositoryApplicationTests {
    private final GenreRepository genreRepository;

    public GenreRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.genreRepository = new GenreRepository(jdbc, new GenreRowMapper());
    }


    @Test
    public void testGetRatingById() {
        var ratingOptional = genreRepository.findById(1L);

        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    public void testFindAll() {
        var ratings = genreRepository.findAll();
        assertThat(ratings.size())
                .isEqualTo(6);
    }
}
