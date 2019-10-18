package ru.otus.dataCollector.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "content_releases")
public class ContentRelease {
    @Field(value = "trackerId")
    private String trackerId;

    @Field(value = "contentType")
    private String contentType;

    @Field(value = "title")
    private String title;

    @Field(value = "size")
    private long size;

    @Field(value = "infoHash")
    private String infoHash;

    @Field(value = "regTime")
    private LocalDateTime regTime;
}
