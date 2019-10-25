package ru.otus.dataCollector.configuraton;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("data-collector")
@Setter
@Getter
public class ApplicationConfiguration {
    private String serverEmail;
}
