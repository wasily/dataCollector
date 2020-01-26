package ru.otus.dataCollector.repositories;

import java.time.LocalDateTime;

public interface SubscriptionRepositoryCustom {
    void updateSearchTime(String imdbId, String userEmail, LocalDateTime time);
}
