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
@Document(collection = "series")
public class Series {
    @Field(value = "imdbId")
    private String imdbId;

    @Field(value = "primaryTitle")
    private String primaryTitle;

    @Field(value = "originalTitle")
    private String originalTitle;

    @Field(value = "isAdult")
    private Boolean isAdult;

    @Field(value = "startYear")
    private Integer startYear;

    @Field(value = "genres")
    private List<String> genres;
}