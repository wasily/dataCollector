package ru.otus.dataCollector.repositories;

import java.time.LocalDateTime;

public interface SubscriptionRepositoryCustom {
    boolean deleteSubscriptionByImdbIdAndUser(String imdbId, String userEmail);
    void updateSearchTime(String imdbId, String userEmail, LocalDateTime time);
}
