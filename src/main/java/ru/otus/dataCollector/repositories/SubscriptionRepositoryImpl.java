package ru.otus.dataCollector.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.dataCollector.model.domain.Subscription;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public void updateSearchTime(String imdbId, String userEmail, LocalDateTime time) {
        if (imdbId == null || userEmail == null || time == null) {
            return;
        }
        mongoTemplate.updateMulti(Query.query(Criteria.where("imdbId").is(imdbId).and("userEmail").is(userEmail)), new Update().set("lastUpdateTime", time), Subscription.class);
    }
}
