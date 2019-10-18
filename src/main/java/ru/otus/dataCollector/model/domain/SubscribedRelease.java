package ru.otus.dataCollector.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "subscribed_releases")
public class SubscribedRelease {
    @Field(value = "imdbId")
    private String imdbId;

    @Field(value = "contentType")
    private String contentType;

    @Field(value = "contentReleases")
    private List<ContentRelease> contentReleases;
}
