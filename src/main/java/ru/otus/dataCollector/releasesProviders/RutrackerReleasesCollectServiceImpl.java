package ru.otus.dataCollector.releasesProviders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.otus.dataCollector.integration.SubscribedReleasesSearchingGateway;
import ru.otus.dataCollector.model.converting.ReleaseEntityResponse;
import ru.otus.dataCollector.model.domain.ContentRelease;
import ru.otus.dataCollector.model.domain.Event;
import ru.otus.dataCollector.repositories.EventRepository;
import ru.otus.dataCollector.repositories.RutrackerRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RutrackerReleasesCollectServiceImpl implements ReleasesCollectService {
    private final static String FORUMS_URL = "http://api.rutracker.org/v1/static/cat_forum_tree";
    private final static String SUBFORUMS_URL = "http://api.rutracker.org/v1/static/pvc/f/";
    private final static String RELEASES_URL = "http://api.rutracker.org/v1/get_tor_topic_data?by=topic_id&val=";
    private static final String MOVIE_CONTENT_TYPE = "movie";
    private static final String SERIES_CONTENT_TYPE = "series";
    private final RutrackerRepository rutrackerRepository;
    private final EventRepository eventRepository;
    private final SubscribedReleasesSearchingGateway subscribedReleasesSearchingGateway;

    @Override
    public void uploadReleases() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        RestTemplate template = new RestTemplate(clientHttpRequestFactory);

        ResponseEntity<String> forumTreeResponse = template.getForEntity(FORUMS_URL, String.class);
        List<String> moviesCategories = extractTopics(forumTreeResponse, "2");
        List<String> seriesCategories = extractTopics(forumTreeResponse, "18");

        LocalDateTime updateTime = LocalDateTime.now();
        long beforeCount = rutrackerRepository.count();
        upload(template, moviesCategories, MOVIE_CONTENT_TYPE);
        upload(template, seriesCategories, SERIES_CONTENT_TYPE);
        long afterCount = rutrackerRepository.count();
        eventRepository.save(new Event("Добавлены новые релизы", afterCount - beforeCount, updateTime));
        subscribedReleasesSearchingGateway.searchSubscribedReleases(updateTime);
    }

    private List<String> extractTopics(ResponseEntity<String> response, String forumId) {
        List<String> NotNeededSubforums = new ArrayList<>();
        NotNeededSubforums.add("214");
        NotNeededSubforums.add("511");
        NotNeededSubforums.add("756");
        NotNeededSubforums.add("124");
        NotNeededSubforums.add("917");
        NotNeededSubforums.add("911");
        NotNeededSubforums.add("2100");
        List<String> result = new LinkedList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode forum = mapper.readTree(response.getBody()).path("result").get("tree").get(forumId);
            forum.fieldNames().forEachRemaining(x -> {
                if (!NotNeededSubforums.contains(x)) {
                    forum.get(x).elements().forEachRemaining(s -> result.add(s.toString()));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void upload(RestTemplate template, List<String> contentCategories, String contentType) {
        for (String category : contentCategories) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<String> topicsList = new LinkedList<>();
                mapper.readTree(template.getForObject(SUBFORUMS_URL + category, String.class)).path("result").fields().forEachRemaining(x -> topicsList.add(x.getKey()));
                IntStream.range(0, (topicsList.size() + 100 - 1) / 100).mapToObj(i -> topicsList.subList(i * 100, Math.min(100 * (i + 1), topicsList.size()))).forEach(releaseId -> {
                    String joinedReleasesIds = releaseId.stream().collect(Collectors.joining(","));
                    ReleaseEntityResponse obj = template.getForObject(RELEASES_URL + joinedReleasesIds, ReleaseEntityResponse.class);
                    obj.getResult().forEach((id, content) -> {
                        try {
                            if (content != null) {
                                rutrackerRepository.save(new ContentRelease(id, contentType, content.getTitle(), content.getSize(),
                                        content.getInfoHash(), LocalDateTime.ofEpochSecond(content.getRegTime(), 0, ZoneOffset.UTC)));
                            }
                        } catch (DuplicateKeyException e) {
                        }
                    });
                });
            } catch (RestClientException | IOException e) {
                e.getLocalizedMessage();
            }
        }
    }
}
