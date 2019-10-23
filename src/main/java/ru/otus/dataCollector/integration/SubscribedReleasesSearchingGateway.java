package ru.otus.dataCollector.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

import java.time.LocalDateTime;

@MessagingGateway
public interface SubscribedReleasesSearchingGateway {
    @Gateway(requestChannel = "searchSubscribedReleasesChannel")
    void searchSubscribedReleases(@Payload LocalDateTime updateTime);
}
