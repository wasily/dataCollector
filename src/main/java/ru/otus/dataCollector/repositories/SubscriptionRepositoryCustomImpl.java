package ru.otus.dataCollector.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.dataCollector.model.domain.Subscription;

@RequiredArgsConstructor
public class SubscriptionRepositoryCustomImpl implements SubscriptionRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public boolean deleteSubscriptionByImdbIdAndUser(String imdbId, String user) {
        mongoTemplate.remove(Query.query(Criteria.where("imdbId").is(imdbId).and("user").is(user)), Subscription.class);
        return true;
    }
}
