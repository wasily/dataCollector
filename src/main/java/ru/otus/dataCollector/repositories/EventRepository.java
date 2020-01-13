package ru.otus.dataCollector.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.otus.dataCollector.model.domain.Event;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    @Query(value = "{}", sort = "{\"eventTime\" : -1}")
    List<Event> findAllSorted();
}
