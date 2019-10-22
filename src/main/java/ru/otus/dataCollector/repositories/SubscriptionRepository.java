package ru.otus.dataCollector.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.dataCollector.model.domain.Subscription;

import java.util.List;

public interface SubscriptionRepository extends MongoRepository<Subscription, String>, SubscriptionRepositoryCustom {
    List<Subscription> findByUser(String user);
}
