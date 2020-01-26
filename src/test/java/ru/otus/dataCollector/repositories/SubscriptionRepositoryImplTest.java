package ru.otus.dataCollector.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.dataCollector.model.domain.Subscription;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@DisplayName("Тест провреки репозитария подписок")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SubscriptionRepositoryImplTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    @DisplayName("должен обновить дату последнего поиска релиза у подписки ({\"imdbId\" : 1, \"userEmail\" : 1}, { unique : true })")
    void shouldUpdateSubscriptionSearchTime() {
        String imdbId = "imdbId";
        String userEmail = "test@test.test";
        LocalDateTime oldTime = LocalDateTime.of(2020, 1, 30, 11, 25);
        LocalDateTime newTime = oldTime.plusHours(1);

        assertThat(subscriptionRepository.findAll()).hasSize(0);
        Subscription subscription = new Subscription(imdbId, "contentType", "title", userEmail, oldTime);
        subscriptionRepository.save(subscription);

        subscriptionRepository.updateSearchTime(null, userEmail, newTime);
        assertThat(subscriptionRepository.findAll().get(0)).isNotNull().isEqualToComparingFieldByField(subscription);

        subscriptionRepository.updateSearchTime(imdbId, null, newTime);
        assertThat(subscriptionRepository.findAll().get(0)).isNotNull().isEqualToComparingFieldByField(subscription);

        subscriptionRepository.updateSearchTime(imdbId, userEmail, null);
        assertThat(subscriptionRepository.findAll().get(0)).isNotNull().isEqualToComparingFieldByField(subscription);

        subscriptionRepository.updateSearchTime(imdbId, userEmail, newTime);
        assertThat(subscriptionRepository.findAll().get(0)).isNotNull()
                .isEqualToIgnoringGivenFields(subscription, "lastUpdateTime")
                .hasFieldOrPropertyWithValue("lastUpdateTime", newTime);
    }
}