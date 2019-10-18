package ru.otus.dataCollector.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.otus.dataCollector.model.domain.Movie;

import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, String> {
    Movie findByImdbId(String imdbId);
    @Query("{ 'primaryTitle' : {$regex:?0, $options:'i'}}")
    List<Movie> findByPrimaryTitleContaining(String title);
}
