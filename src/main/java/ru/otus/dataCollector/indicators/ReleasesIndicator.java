package ru.otus.dataCollector.indicators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import ru.otus.dataCollector.repositories.RutrackerRepository;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReleasesIndicator extends AbstractHealthIndicator {
    private final RutrackerRepository rutrackerRepository;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Map<String, Long> details = new HashMap<>(2);
        details.put("количество имеющихся релизов фильмов", rutrackerRepository.countCategory("movie"));
        details.put("количество имеющихся релизов сериалов", rutrackerRepository.countCategory("series"));
        builder.up().withDetails(details);
    }
}
