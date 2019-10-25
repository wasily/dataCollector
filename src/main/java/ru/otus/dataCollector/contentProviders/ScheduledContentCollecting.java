package ru.otus.dataCollector.contentProviders;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledContentCollecting {
    private final ContentCollectService contentCollectService;

    @Scheduled(fixedRate = 1000*60*60*24)
    public void collectContent(){
//        contentCollectService.uploadContent();
    }
}
