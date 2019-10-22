package ru.otus.dataCollector.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.PollableChannel;
import ru.otus.dataCollector.notifier.NotifierService;
import ru.otus.dataCollector.subscriptionSearch.SubscribedReleasesSearchingService;

@Configuration
@IntegrationComponentScan
public class Config {

    @Autowired
    private SubscribedReleasesSearchingService subscribedReleasesSearchingService;

    @Autowired
    private NotifierService notifierService;

    @Bean
    public PollableChannel searchSubscribedReleasesChannel() {
        return MessageChannels.queue("searchSubscribedReleasesChannel").get();
    }

    @Bean
    public PollableChannel notifyUsersChannel() {
        return MessageChannels.queue("notifyUsersChannel").get();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedRate(10).get();
    }

    @Bean
    public IntegrationFlow searchSubscribedReleases() {
        return f -> f.channel(searchSubscribedReleasesChannel())
                .handle(subscribedReleasesSearchingService, "performSearch");
    }

    @Bean
    public IntegrationFlow notifyUsers() {
        return f -> f.channel(notifyUsersChannel())
                .handle(notifierService, "notifySubscribers");
    }

}
