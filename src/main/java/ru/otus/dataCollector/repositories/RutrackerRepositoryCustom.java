package ru.otus.dataCollector.repositories;

import ru.otus.dataCollector.model.domain.ContentRelease;

import java.time.LocalDateTime;
import java.util.List;

public interface RutrackerRepositoryCustom {
    List<ContentRelease> findByTitleContainingByTime(String title, String contentType, LocalDateTime time);
    Long countNewReleasesByCategory(String category, int periodInHours);
}
