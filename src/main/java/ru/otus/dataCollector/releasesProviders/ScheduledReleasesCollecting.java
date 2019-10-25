package ru.otus.dataCollector.releasesProviders;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.otus.dataCollector.integration.SubscribedReleasesSearchingGateway;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledReleasesCollecting {
    private final List<ReleasesCollectService> releasesCollectService;
    private final SubscribedReleasesSearchingGateway subscribedReleasesSearchingGateway;

    @Scheduled(fixedRate = 1000*60*60*2)
    public void collectReleasesContent(){
        LocalDateTime updateTime = LocalDateTime.now();
        releasesCollectService.forEach(ReleasesCollectService::uploadReleases);
        subscribedReleasesSearchingGateway.searchSubscribedReleases(updateTime);
    }
}
