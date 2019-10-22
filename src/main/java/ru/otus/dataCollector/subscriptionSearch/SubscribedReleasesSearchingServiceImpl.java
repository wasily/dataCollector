package ru.otus.dataCollector.subscriptionSearch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.dataCollector.integration.NotificationGateway;
import ru.otus.dataCollector.model.domain.ContentRelease;
import ru.otus.dataCollector.model.domain.SubscribedRelease;
import ru.otus.dataCollector.model.domain.Subscription;
import ru.otus.dataCollector.repositories.RutrackerRepository;
import ru.otus.dataCollector.repositories.SubscribedReleaseRepository;
import ru.otus.dataCollector.repositories.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribedReleasesSearchingServiceImpl implements SubscribedReleasesSearchingService {
    private final SubscriptionRepository subscriptionRepository;
    private final RutrackerRepository rutrackerRepository;
    private final SubscribedReleaseRepository subscribedReleaseRepository;
    private final NotificationGateway notificationGateway;

    @Override
    public void performSearch() {
        List<Subscription> tmp = subscriptionRepository.findAll();
        tmp.forEach(v -> {
            List<ContentRelease> crl = rutrackerRepository.findByTitleContaining(v.getContentTitle(), v.getContentType()).stream().
                    filter(z -> z.getRegTime().isAfter(v.getLastUpdateTime())).
                    collect(Collectors.toList());
            if (crl.size() != 0) {
                subscribedReleaseRepository.save(new SubscribedRelease(v.getImdbId(), v.getContentType(), v.getUserEmail(), crl));
            }
            subscriptionRepository.updateSearchTime(v.getImdbId(), v.getUserEmail(), LocalDateTime.now());
        });
        notificationGateway.notifyUsers();
    }

}
