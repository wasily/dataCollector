package ru.otus.dataCollector.notifier;

import ru.otus.dataCollector.model.domain.SubscribedRelease;

public interface NotifierService {
    void notifySubscriber(SubscribedRelease subscribedRelease);
}
