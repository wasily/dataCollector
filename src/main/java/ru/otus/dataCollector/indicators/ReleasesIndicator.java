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
    private final static int HOURS_AMOUNT_FOR_HEALTH_INDICATOR = 6;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Map<String, Long> details = new HashMap<>(2);
        details.put("количество имеющихся релизов фильмов, опубликованных за последние " + HOURS_AMOUNT_FOR_HEALTH_INDICATOR + " часов",
                rutrackerRepository.countNewReleasesByCategory("movie", HOURS_AMOUNT_FOR_HEALTH_INDICATOR));
        details.put("количество имеющихся релизов сериалов, опубликованных за последние " + HOURS_AMOUNT_FOR_HEALTH_INDICATOR + " часов",
                rutrackerRepository.countNewReleasesByCategory("series", HOURS_AMOUNT_FOR_HEALTH_INDICATOR));
        builder.up().withDetails(details);
    }
}
