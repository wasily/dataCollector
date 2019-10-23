package ru.otus.dataCollector.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;
import ru.otus.dataCollector.model.domain.SubscribedRelease;

@MessagingGateway
public interface NotificationGateway {
    @Gateway(requestChannel = "notifyUsersChannel")
    void notifyUser(@Payload SubscribedRelease subscribedRelease);
}
