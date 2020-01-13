package ru.otus.dataCollector.releasesProviders;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.otus.dataCollector.integration.ReleasesGateway;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduledReleasesProcessing {
    private final ReleasesGateway releasesGateway;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void processReleases() {
        LocalDateTime updateTime = LocalDateTime.now();
        releasesGateway.processReleases(updateTime);
    }
}
