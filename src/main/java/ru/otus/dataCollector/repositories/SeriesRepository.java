package ru.otus.dataCollector.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.otus.dataCollector.model.domain.Series;

import java.util.List;

public interface SeriesRepository extends MongoRepository<Series, String> {
    Series findByImdbId(String imdbId);
    @Query("{ 'primaryTitle' : {$regex:?0, $options:'i'}}")
    List<Series> findByPrimaryTitleContaining(String title);
}
