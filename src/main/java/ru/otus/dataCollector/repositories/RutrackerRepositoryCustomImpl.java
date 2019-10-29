package ru.otus.dataCollector.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.dataCollector.model.domain.ContentRelease;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class RutrackerRepositoryCustomImpl implements RutrackerRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<ContentRelease> findByTitleContainingByTime(String title, String contentType, LocalDateTime time) {
        return mongoTemplate.find(Query.query(Criteria.where("title").regex(title).and("contentType").is(contentType).and("regTime").gt(time)), ContentRelease.class);
    }

    @Override
    public Long countNewReleasesByCategory(String category, int periodInHours) {
        return  mongoTemplate.count(Query.query(Criteria.where("regTime").gt(LocalDateTime.now().minusHours(periodInHours)).and("contentType").is(category)),ContentRelease.class);
    }
}
