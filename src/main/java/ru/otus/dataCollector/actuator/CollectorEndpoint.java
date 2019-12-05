package ru.otus.dataCollector.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import ru.otus.dataCollector.model.domain.Event;
import ru.otus.dataCollector.repositories.EventRepository;

import java.util.List;

@RequiredArgsConstructor
@Component
@Endpoint(id = "datacollector")
public class CollectorEndpoint {
    private final EventRepository eventRepository;

    @ReadOperation
    public List<Event> getAppUsersActivityStat() {
        return eventRepository.findAllSorted();
    }

}
