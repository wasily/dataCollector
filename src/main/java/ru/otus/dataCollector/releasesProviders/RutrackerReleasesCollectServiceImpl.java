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
import ru.otus.dataCollector.model.converting.ReleaseEntity;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RutrackerReleasesCollectServiceImpl implements ReleasesCollectService {
    private final static String FORUMS_URL = "http://api.rutracker.org/v1/static/cat_forum_tree";
    private final static String SUBFORUMS_URL = "http://api.rutracker.org/v1/static/pvc/f/";
    private final static String RELEASES_URL = "http://api.rutracker.org/v1/get_tor_topic_data?by=topic_id&val=";
    private static final String MOVIES_TOPIC_ID = "2";
    private static final String SERIES_TOPIC_ID = "18";
    private static final int RUTRACKER_REQUEST_LIMIT = 100;
    private static final String MOVIE_CONTENT_TYPE = "movie";
    private static final String SERIES_CONTENT_TYPE = "series";
    private final RutrackerRepository rutrackerRepository;
    private final EventRepository eventRepository;

    @Override
    public void uploadReleases() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        RestTemplate template = new RestTemplate(clientHttpRequestFactory);

        ResponseEntity<String> forumTreeResponse = template.getForEntity(FORUMS_URL, String.class);
        List<String> moviesTopics = extractTopics(forumTreeResponse, MOVIES_TOPIC_ID);
        List<String> seriesTopics = extractTopics(forumTreeResponse, SERIES_TOPIC_ID);

        LocalDateTime updateTime = LocalDateTime.now();
        long beforeCount = rutrackerRepository.count();
        processContent(template, moviesTopics, MOVIE_CONTENT_TYPE);
        processContent(template, seriesTopics, SERIES_CONTENT_TYPE);
        long afterCount = rutrackerRepository.count();
        eventRepository.save(new Event("Добавлены новые релизы", afterCount - beforeCount, updateTime));
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

    private void processContent(RestTemplate template, List<String> contentTopics, String contentType) {
        long releasesCount = rutrackerRepository.countByCategory(contentType);
        List<String> releasesIds = new ArrayList<>((int) Math.min(releasesCount + 100, Integer.MAX_VALUE));
        ObjectMapper mapper = new ObjectMapper();
        for (String category : contentTopics) {
            try {
                mapper.readTree(template.getForObject(SUBFORUMS_URL + category, String.class)).path("result").fieldNames().forEachRemaining(releasesIds::add);
            } catch (RestClientException | IOException e) {
            }
        }
        int chunksCount = (releasesIds.size() + RUTRACKER_REQUEST_LIMIT - 1) / RUTRACKER_REQUEST_LIMIT;
        IntStream.range(0, chunksCount)
                .parallel()
                .mapToObj(i -> combineIds(releasesIds, i))
                .forEach(ids -> requestReleases(template, ids).thenAccept(response ->
                        response.getResult().entrySet().stream().parallel()
                                .filter(entry -> entry.getValue() != null)
                                .forEach(entry -> saveRelease(entry.getKey(), entry.getValue(), contentType))));
    }

    private String combineIds(List<String> topicsIds, int chunkNumber) {
        return String.join(",", topicsIds.subList(chunkNumber * RUTRACKER_REQUEST_LIMIT,
                Math.min(RUTRACKER_REQUEST_LIMIT * (chunkNumber + 1), topicsIds.size())));
    }

    private CompletableFuture<ReleaseEntityResponse> requestReleases(RestTemplate template, String joinedTopicsIds) {
        return CompletableFuture.supplyAsync(() -> template.getForObject(RELEASES_URL + joinedTopicsIds, ReleaseEntityResponse.class));
    }

    private void saveRelease(String id, ReleaseEntity release, String contentType) {
        try {
            rutrackerRepository.save(new ContentRelease(id, contentType, release.getTitle(), release.getSize(),
                    release.getInfoHash(), LocalDateTime.ofEpochSecond(release.getRegTime(), 0, ZoneOffset.UTC)));
        } catch (DuplicateKeyException e) {
        }
    }
}
