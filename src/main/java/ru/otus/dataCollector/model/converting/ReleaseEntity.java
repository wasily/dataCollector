package ru.otus.dataCollector.model.converting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseEntity {

    @JsonProperty(value = "topic_title")
    private String title;

    @JsonProperty(value = "size")
    private long size;

    @JsonProperty(value = "info_hash")
    private String infoHash;

    @JsonProperty(value = "reg_time")
    private long regTime;
}
