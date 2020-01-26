package ru.otus.dataCollector.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.dataCollector.model.domain.ContentRelease;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@DisplayName("Тест провреки репозитария релизов Rutracker`а")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RutrackerRepositoryImplTest {
    private static final String MOVIE_CATEGORY = "movie";
    private static final String SERIES_CATEGORY = "series";

    @Autowired
    private RutrackerRepository rutrackerRepository;

    @Test
    @DisplayName("должен правильно вернуть релизы по части названия, по категории контента, за определенное время")
    void shouldReturnReleasesByTitleByCategoryByTime() {
        String title = "title";
        LocalDateTime releaseTime = LocalDateTime.of(2020, 1, 1, 12, 30, 20);
        LocalDateTime searchTime = releaseTime.minusHours(1);
        LocalDateTime wrongSearchTime = searchTime.minusHours(1);

        ContentRelease release1 = new ContentRelease("id1", MOVIE_CATEGORY, title, 0, "hash", releaseTime);
        ContentRelease release2 = new ContentRelease("id2", MOVIE_CATEGORY, title.toUpperCase(), 0, "hash", releaseTime);
        ContentRelease release3 = new ContentRelease("id3", MOVIE_CATEGORY, title + "test", 0, "hash", releaseTime);
        ContentRelease release4 = new ContentRelease("id4", MOVIE_CATEGORY, "test" + title, 0, "hash", releaseTime);
        ContentRelease release5 = new ContentRelease("id5", MOVIE_CATEGORY, "test" + title + "test", 0, "hash", releaseTime);
        rutrackerRepository.saveAll(List.of(release1, release2, release3, release4, release5));

        ContentRelease release6 = new ContentRelease("id6", MOVIE_CATEGORY, title, 0, "hash", wrongSearchTime);
        ContentRelease release7 = new ContentRelease("id7", MOVIE_CATEGORY, "test", 0, "hash", searchTime);
        ContentRelease release8 = new ContentRelease("id8", SERIES_CATEGORY, title, 0, "hash", searchTime);
        ContentRelease release9 = new ContentRelease("id9", SERIES_CATEGORY, title, 0, "hash", wrongSearchTime);
        rutrackerRepository.saveAll(List.of(release6, release7, release8, release9));

        assertEquals(0, rutrackerRepository.findByTitleContainingByTime(title, MOVIE_CATEGORY, null).size());

        List<ContentRelease> movieResult = rutrackerRepository.findByTitleContainingByTime(title, MOVIE_CATEGORY, searchTime);

        assertThat(movieResult).hasSize(5).usingRecursiveFieldByFieldElementComparator()
                .contains(release1, release2, release3, release4, release5)
                .doesNotContain(release6, release7, release8, release9);
    }

    @Test
    @DisplayName("должен правильно вернуть количество релизов по категории контента за определенное время")
    void shouldReturnCountOfNewReleasesByCategory() {
        int periodInHours = 3;
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime timeOutOfPeriod = currentTime.minusHours(periodInHours + 1);
        LocalDateTime timeOnBorderOfPeriod = currentTime.minusHours(periodInHours);
        LocalDateTime timeInPeriod = currentTime.minusHours(periodInHours - 1);
        assertTrue(periodInHours - 1 > 0);

        rutrackerRepository.save(new ContentRelease("id1", MOVIE_CATEGORY, "title", 0, "hash", currentTime));
        rutrackerRepository.save(new ContentRelease("id2", MOVIE_CATEGORY, "title", 0, "hash", timeInPeriod));

        rutrackerRepository.save(new ContentRelease("id3", MOVIE_CATEGORY, "title", 0, "hash", timeOnBorderOfPeriod));
        rutrackerRepository.save(new ContentRelease("id4", MOVIE_CATEGORY, "title", 0, "hash", timeOutOfPeriod));
        rutrackerRepository.save(new ContentRelease("id5", MOVIE_CATEGORY, "title", 0, "hash", timeOutOfPeriod));
        rutrackerRepository.save(new ContentRelease("id6", MOVIE_CATEGORY, "title", 0, "hash", timeOutOfPeriod));

        rutrackerRepository.save(new ContentRelease("id7", SERIES_CATEGORY, "none", 0, "hash", currentTime));
        rutrackerRepository.save(new ContentRelease("id8", SERIES_CATEGORY, "none", 0, "hash", timeInPeriod));
        rutrackerRepository.save(new ContentRelease("id9", SERIES_CATEGORY, "none", 0, "hash", timeOnBorderOfPeriod));

        assertEquals(2, rutrackerRepository.countNewReleasesByCategory(MOVIE_CATEGORY, periodInHours));
    }

    @Test
    @DisplayName("должен правильно вернуть количество релизов по категории контента")
    void shouldReturnCountOfReleasesByCategory() {
        rutrackerRepository.save(new ContentRelease("id1", MOVIE_CATEGORY, "title", 0, "hash", null));
        rutrackerRepository.save(new ContentRelease("id2", MOVIE_CATEGORY, "title", 0, "hash", null));
        rutrackerRepository.save(new ContentRelease("id3", MOVIE_CATEGORY, "title", 0, "hash", null));
        rutrackerRepository.save(new ContentRelease("id4", MOVIE_CATEGORY, "title", 0, "hash", null));
        assertEquals(4, rutrackerRepository.countByCategory(MOVIE_CATEGORY));

        rutrackerRepository.save(new ContentRelease("id5", SERIES_CATEGORY, "title", 0, "hash", null));
        rutrackerRepository.save(new ContentRelease("id6", SERIES_CATEGORY, "title", 0, "hash", null));
        rutrackerRepository.save(new ContentRelease("id7", SERIES_CATEGORY, "title", 0, "hash", null));
        assertEquals(3, rutrackerRepository.countByCategory(SERIES_CATEGORY));
    }
}