package ru.otus.dataCollector.releasesProviders;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledReleasesCollecting {
    private final List<ReleasesCollectService> releasesCollectService;

    @Scheduled(fixedRate = 1000*60*60*12)
    public void collectReleasesContent(){
        releasesCollectService.forEach(ReleasesCollectService::uploadReleases);
    }
}
