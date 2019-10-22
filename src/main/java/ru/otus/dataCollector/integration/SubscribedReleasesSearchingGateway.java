package ru.otus.dataCollector.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface SubscribedReleasesSearchingGateway {
    @Gateway(requestChannel = "searchSubscribedReleasesChannel")
    void searchSubscribedReleases();
}
