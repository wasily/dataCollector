package ru.otus.dataCollector.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.PollableChannel;
import ru.otus.dataCollector.model.domain.ContentRelease;
import ru.otus.dataCollector.model.domain.SubscribedRelease;
import ru.otus.dataCollector.notifier.NotifierService;
import ru.otus.dataCollector.releasesProviders.ReleasesCollectService;
import ru.otus.dataCollector.repositories.RutrackerRepository;
import ru.otus.dataCollector.repositories.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@IntegrationComponentScan
@RequiredArgsConstructor
public class IntegrationConfiguration {
    private static final int POLLING_PERIOD = 1000;
    private final NotifierService notifierService;
    private final List<ReleasesCollectService> releasesCollectServices;
    private final SubscriptionRepository subscriptionRepository;
    private final RutrackerRepository rutrackerRepository;

    @Bean
    public PollableChannel releasesProcessingChannel() {
        return MessageChannels.queue("releasesProcessingChannel").get();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedRate(POLLING_PERIOD).get();
    }

    @Bean
    public IntegrationFlow processReleases() {
        return f -> f.channel(releasesProcessingChannel())
                .handle(m -> {
                    releasesCollectServices.forEach(ReleasesCollectService::uploadReleases);
                    subscriptionRepository.findAll().forEach(v -> {
                        List<ContentRelease> releases = rutrackerRepository.findByTitleContainingByTime(v.getContentTitle(), v.getContentType(), v.getLastUpdateTime());
                        if (releases.size() != 0) {
                            notifierService.notifySubscriber(new SubscribedRelease(v.getImdbId(), v.getContentType(), v.getUserEmail(), releases));
                        }
                        subscriptionRepository.updateSearchTime(v.getImdbId(), v.getUserEmail(), (LocalDateTime) m.getPayload());
                    });
                });
    }

}
