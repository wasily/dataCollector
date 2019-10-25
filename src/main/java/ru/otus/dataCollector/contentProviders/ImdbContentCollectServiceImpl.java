package ru.otus.dataCollector.contentProviders;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.otus.dataCollector.model.domain.Event;
import ru.otus.dataCollector.model.domain.Movie;
import ru.otus.dataCollector.model.domain.Series;
import ru.otus.dataCollector.repositories.EventRepository;
import ru.otus.dataCollector.repositories.MovieRepository;
import ru.otus.dataCollector.repositories.SeriesRepository;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImdbContentCollectServiceImpl implements ContentCollectService {
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final EventRepository eventRepository;
    private final static String CONTENT_LINK = "https://datasets.imdbws.com/title.basics.tsv.gz";
    private final static String FIELD_MISSING_FLAG = "N";
    private final static String LITERAL_FOR_TRUE_VALUE = "1";
    private final static String GENRES_SEPARATOR = ",";
    private final static char CONTENT_VALUES_SEPARATOR = '\t';
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
        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader(filename)).withSkipLines(1).
                    withCSVParser(new CSVParserBuilder().withSeparator(CONTENT_VALUES_SEPARATOR).build()).build();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length == 9 && nextLine[1].equals(MOVIE_TYPE)) {
                    try {
                        movieRepository.save(new Movie(nextLine[0], nextLine[2], nextLine[3], transformBoolean(nextLine[4]), parseStartYear(nextLine[5]),
                                Arrays.stream(nextLine[8].split(GENRES_SEPARATOR)).filter(genre -> !genre.equals(FIELD_MISSING_FLAG)).collect(Collectors.toList())));
                    } catch (DuplicateKeyException e) {
                    }
                } else if (nextLine.length == 9 && nextLine[1].equals(SERIES_TYPE)) {
                    try {
                        seriesRepository.save(new Series(nextLine[0], nextLine[2], nextLine[3], transformBoolean(nextLine[4]), parseStartYear(nextLine[5]),
                                Arrays.stream(nextLine[8].split(GENRES_SEPARATOR)).filter(genre -> !genre.equals(FIELD_MISSING_FLAG)).collect(Collectors.toList())));
                    } catch (DuplicateKeyException e) {
                    }
                }
            }
            new File(filename).delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean transformBoolean(String field) {
        return field.equals(FIELD_MISSING_FLAG) ? null : field.equals(LITERAL_FOR_TRUE_VALUE);
    }

    private Integer parseStartYear(String year) {
        if (year.equals(FIELD_MISSING_FLAG)) {
            return null;
        }
        try {
            return Integer.parseInt(year);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
