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
@Document(collection = "subscriptions")
public class Subscription {
    @Field(value = "imdbId")
    private String imdbId;

    @Field(value = "contentType")
    private String contentType;

    @Field(value = "contentTitle")
    private String contentTitle;

    @Field(value = "user")
    private String user;

    @Field(value = "lastUpdateTime")
    private LocalDateTime lastUpdateTime;
}
