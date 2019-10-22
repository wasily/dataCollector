package ru.otus.dataCollector.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.dataCollector.model.domain.SubscribedRelease;

public interface SubscribedReleaseRepository extends MongoRepository<SubscribedRelease, String> {
    void deleteByImdbId(String imdbId);
}
