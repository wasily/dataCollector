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
@Document(collection = "events")
public class Event {
    @Field(value = "eventDescription")
    private String eventDescription;

    @Field(value = "metric")
    private Long metric;

    @Field(value = "eventTime")
    private LocalDateTime eventTime;
}
