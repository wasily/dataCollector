package ru.otus.dataCollector.indicators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import ru.otus.dataCollector.repositories.MovieRepository;
import ru.otus.dataCollector.repositories.SeriesRepository;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ContentIndicator extends AbstractHealthIndicator {
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Map<String, Long> details = new HashMap<>(2);
        details.put("количество известных фильмов", movieRepository.count());
        details.put("количество известных сериалов", seriesRepository.count());
        builder.up().withDetails(details);
    }
}
