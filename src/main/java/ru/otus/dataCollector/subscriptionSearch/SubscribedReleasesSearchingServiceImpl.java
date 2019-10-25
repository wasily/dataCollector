package ru.otus.dataCollector.subscriptionSearch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.dataCollector.integration.NotificationGateway;
import ru.otus.dataCollector.model.domain.ContentRelease;
import ru.otus.dataCollector.model.domain.SubscribedRelease;
import ru.otus.dataCollector.model.domain.Subscription;
import ru.otus.dataCollector.repositories.RutrackerRepository;
import ru.otus.dataCollector.repositories.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscribedReleasesSearchingServiceImpl implements SubscribedReleasesSearchingService {
    private final SubscriptionRepository subscriptionRepository;
    private final RutrackerRepository rutrackerRepository;
    private final NotificationGateway notificationGateway;

    @Override
    public void performSearch(LocalDateTime updateTime) {
        List<Subscription> tmp = subscriptionRepository.findAll();
        tmp.forEach(v -> {
            List<ContentRelease> releases = rutrackerRepository.findByTitleContainingByTime(v.getContentTitle(), v.getContentType(), v.getLastUpdateTime());
            if (releases.size() != 0) {
                notificationGateway.notifyUser(new SubscribedRelease(v.getImdbId(), v.getContentType(), v.getUserEmail(), releases));
            }
            subscriptionRepository.updateSearchTime(v.getImdbId(), v.getUserEmail(), updateTime);
        });

    }

}
