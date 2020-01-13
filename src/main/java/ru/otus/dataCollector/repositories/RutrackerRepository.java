package ru.otus.dataCollector.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.otus.dataCollector.model.domain.ContentRelease;

import java.util.List;

public interface RutrackerRepository extends MongoRepository<ContentRelease, String>, RutrackerRepositoryCustom {
    @Query("{ 'title' : {$regex:?0, $options:'i'}, 'contentType' : ?1 }")
    List<ContentRelease> findByTitleContaining(String title, String contentType);
}
