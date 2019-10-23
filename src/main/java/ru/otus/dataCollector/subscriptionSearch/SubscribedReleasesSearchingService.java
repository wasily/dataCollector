package ru.otus.dataCollector.subscriptionSearch;

import java.time.LocalDateTime;

public interface SubscribedReleasesSearchingService {
    void performSearch(LocalDateTime updateTime);
}
