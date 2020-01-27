package ru.otus.dataCollector.contentProviders;

import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.otus.dataCollector.model.converting.ImdbContentParser;
import ru.otus.dataCollector.model.domain.Event;
import ru.otus.dataCollector.repositories.EventRepository;
import ru.otus.dataCollector.repositories.MovieRepository;
import ru.otus.dataCollector.repositories.SeriesRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImdbContentCollectServiceImpl implements ContentCollectService {
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final EventRepository eventRepository;
    private final static String CONTENT_LINK = "https://datasets.imdbws.com/title.basics.tsv.gz";
    private final static String MOVIE_TYPE = "movie";
    private final static String SERIES_TYPE = "tvSeries";

    @Override
    public void uploadContent() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String filename = tmpDir + "/content.gz";
        String contentFilename = tmpDir + "/content.tsv";
        LocalDateTime updateTime = LocalDateTime.now();
        long beforeCount = movieRepository.count() + seriesRepository.count();
        if (downloadFile(filename, CONTENT_LINK)) {
            if (decompressFile(filename, contentFilename)) {
                upload(contentFilename);
                long afterCount = movieRepository.count() + seriesRepository.count();
                eventRepository.save(new Event("Добавлены новые фильмы/сериалы", afterCount - beforeCount, updateTime));
            }
        }
    }

    private boolean downloadFile(String newFileName, String link) {
        URL url;
        try {
            url = new URL(link);
            try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(newFileName);
                 FileChannel fileChannel = fileOutputStream.getChannel()) {
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean decompressFile(String gzipFileName, String newFileName) {
        try (GzipCompressorInputStream in = new GzipCompressorInputStream(new FileInputStream(gzipFileName))) {
            IOUtils.copy(in, new FileOutputStream(newFileName));
            new File(gzipFileName).delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void upload(String filename) {
        ImdbContentParser parser = new ImdbContentParser(filename);
        while (parser.hasNextContentEntry()) {
            if (parser.getContentType().equals(MOVIE_TYPE)) {
                try {
                    movieRepository.save(parser.getMovie());
                } catch (DuplicateKeyException e) {
                }
            } else if (parser.getContentType().equals(SERIES_TYPE)) {
                try {
                    seriesRepository.save(parser.getSeries());
                } catch (DuplicateKeyException e) {
                }
            }
        }
    }
}
