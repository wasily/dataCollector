package ru.otus.dataCollector.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscribedRelease {
    private String imdbId;
    private String contentType;
    private String userEmail;
    private List<ContentRelease> contentReleases;
}
