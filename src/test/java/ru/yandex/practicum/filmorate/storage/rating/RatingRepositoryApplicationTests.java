package ru.yandex.practicum.filmorate.storage.rating;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
public class RatingRepositoryApplicationTests {
    private final RatingRepository ratingRepository;

    public RatingRepositoryApplicationTests(@Autowired JdbcTemplate jdbc) {
        this.ratingRepository = new RatingRepository(jdbc, new RatingRowMapper());
    }

    @Test
    public void testGetRatingById() {
        var ratingOptional = ratingRepository.findById(1L);

        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "G"));
    }

    @Test
    public void testFindAll() {
        var ratings = ratingRepository.findAll();
        assertThat(ratings.size())
                .isEqualTo(5);
    }
}
