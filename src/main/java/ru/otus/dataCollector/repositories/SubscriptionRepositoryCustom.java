package ru.otus.dataCollector.repositories;

public interface SubscriptionRepositoryCustom {
    boolean deleteSubscriptionByImdbIdAndUser(String imdbId, String user);
}
