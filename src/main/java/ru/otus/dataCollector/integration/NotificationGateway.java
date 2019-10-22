package ru.otus.dataCollector.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface NotificationGateway {
    @Gateway(requestChannel = "notifyUsersChannel")
    void notifyUsers();
}
