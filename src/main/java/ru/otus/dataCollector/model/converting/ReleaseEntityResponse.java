package ru.otus.dataCollector.model.converting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleaseEntityResponse {
    @JsonProperty("result")
    LinkedHashMap<String, ReleaseEntity> result;
}
