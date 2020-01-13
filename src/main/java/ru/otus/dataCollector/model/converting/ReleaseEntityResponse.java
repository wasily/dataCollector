package ru.otus.dataCollector.model.converting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleaseEntityResponse {
    @JsonProperty("result")
    HashMap<String, ReleaseEntity> result;
}
