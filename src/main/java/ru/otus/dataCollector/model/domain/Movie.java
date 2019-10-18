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
@Document(collection = "movies")
public class Movie {
    @Field(value = "imdbId")
    private String imdbId;

    @Field(value = "primaryTitle")
    private String primaryTitle;

    @Field(value = "originalTitle")
    private String originalTitle;

    @Field(value = "isAdult")
    private boolean isAdult;

    @Field(value = "genres")
    private List<String> genres;

}