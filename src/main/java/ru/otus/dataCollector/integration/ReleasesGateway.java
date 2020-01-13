package ru.otus.dataCollector.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

import java.time.LocalDateTime;

@MessagingGateway
public interface ReleasesGateway {
    @Gateway(requestChannel = "releasesProcessingChannel")
    void processReleases(@Payload LocalDateTime updateTime);
}
