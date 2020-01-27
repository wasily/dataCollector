package ru.otus.dataCollector.model.converting;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import ru.otus.dataCollector.model.domain.Movie;
import ru.otus.dataCollector.model.domain.Series;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ImdbContentParser {
    private final static String FIELD_MISSING_FLAG = "N";
    private final static String LITERAL_FOR_TRUE_VALUE = "1";
    private final static String GENRES_SEPARATOR = ",";
    private final static char CONTENT_VALUES_SEPARATOR = '\t';
    private final static String MOVIE_TYPE = "movie";
    private final static String SERIES_TYPE = "tvSeries";
    private final static int IMDBID_IDX = 0;
    private final static int MOVIE_TYPE_IDX = 1;
    private final static int PRIMARY_TITLE_IDX = 2;
    private final static int ORIGINAL_TITLE_IDX = 3;
    private final static int IS_ADULT_IDX = 4;
    private final static int START_YEAR_IDX = 5;
    private final static int GENRES_IDX = 8;
    private final static int VALUES_REQUIRED_COUNT = 9;
    private CSVReader reader;
    private String[] currentLine;

    public ImdbContentParser(String contentFilename) {
        try {
            this.reader = new CSVReaderBuilder(new FileReader(contentFilename)).withSkipLines(1).
                    withCSVParser(new CSVParserBuilder().withSeparator(CONTENT_VALUES_SEPARATOR).build()).build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean hasNextContentEntry() {
        try {
            while ((currentLine = reader.readNext()) != null) {
                if (currentLine.length == VALUES_REQUIRED_COUNT) {
                    if (currentLine[MOVIE_TYPE_IDX].equals(MOVIE_TYPE)) {
                        return true;
                    } else if (currentLine[MOVIE_TYPE_IDX].equals(SERIES_TYPE)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getContentType() {
        return currentLine == null ? null : currentLine[MOVIE_TYPE_IDX];
    }

    public Movie getMovie() {
        return currentLine == null ? null : new Movie(currentLine[IMDBID_IDX], currentLine[PRIMARY_TITLE_IDX], currentLine[ORIGINAL_TITLE_IDX],
                transformBoolean(currentLine[IS_ADULT_IDX]), parseStartYear(currentLine[START_YEAR_IDX]),
                Arrays.stream(currentLine[GENRES_IDX].split(GENRES_SEPARATOR)).filter(genre -> !genre.equals(FIELD_MISSING_FLAG)).collect(Collectors.toList()));
    }

    public Series getSeries() {
        return currentLine == null ? null : new Series(currentLine[IMDBID_IDX], currentLine[PRIMARY_TITLE_IDX], currentLine[ORIGINAL_TITLE_IDX],
                transformBoolean(currentLine[IS_ADULT_IDX]), parseStartYear(currentLine[START_YEAR_IDX]),
                Arrays.stream(currentLine[GENRES_IDX].split(GENRES_SEPARATOR)).filter(genre -> !genre.equals(FIELD_MISSING_FLAG)).collect(Collectors.toList()));
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
