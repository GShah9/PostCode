package com.postcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Gautam on 27/02/2017.
 *
 * This is the task 2 of Postcode import
 * link - https://gist.github.com/edhiley/5da612c93e31c7e60355#part-2---bulk-import
 */
public class BulkImport {
    private static final String SEPARATOR = ",";
    private final Reader source;
    private static List<String> header;

    BulkImport(Reader source) {
        this.source = source;
    }

    private void setHeader(List<String> header) {
        this.header = header;
    }

    public List<String> getHeader() {
        return header;
    }

    public static String getHeaderString() {
        StringBuilder headers = new StringBuilder();

        for (String head : header) {
            if (headers.length() != 0) { // prevents extra comma char
                headers.append(",");
            }
            headers.append(head);
        }
        return headers.toString();
    }

    @SuppressWarnings("unchecked")
    public static Function<String, Postcode> maptoPostCode = (line) -> {
        String[] p = line.split(SEPARATOR);
        return new Postcode(Integer.parseInt(p[0]), p[1]);
    };

    public Map<Integer, String> readRecords() {
        Map<Integer, String> postcodeMap = new HashMap<Integer, String>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(source);
            setHeader(br.lines()
                    .findFirst()
                    .map(line -> Arrays.asList(line.split(SEPARATOR)))
                    .get());
            postcodeMap = br.lines()
                    .map(maptoPostCode)
                    .collect(Collectors.toMap(Postcode::getRowId, Postcode::getPostcode));
        } finally {
            try {
                br.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
        return postcodeMap;
    }

    public static String toCsvRow(Map<Integer, String> records) {
        return records.entrySet()
                .stream()
                .map(map -> map.toString().replaceAll("=", ","))
                .collect(Collectors.joining("\n"));
    }

    public static Map<Integer, String> postcodeValidity(Map<Integer, String> records) {
        //Map<Integer, String> newMap = new HashMap<Integer, String>();
        SortedMap<Integer, String> invalidMap = new TreeMap<>();

        Comparator<Map.Entry<Integer, String>> byKey = (entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey());

        records.entrySet()
                .stream()
                .sorted(byKey)
                .forEach(
                        postMap -> {
                            if (postMap.getValue() == null || !PostCodeValidator.isPostCode(postMap.getValue())) {
                                invalidMap.put(postMap.getKey(), postMap.getValue());
                            }
                        }
                );
        return invalidMap;
    }

    public static void postcodeValidity2CSV(Map<Integer, String> records) {
        SortedMap<Integer, String> invalidMap = new TreeMap<>();
        SortedMap<Integer, String> validMap = new TreeMap<>();

        Comparator<Map.Entry<Integer, String>> byKey = (entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey());

        records.entrySet()
                .stream()
                .sorted(byKey)
                .forEach(
                        postMap -> {
                            if (postMap.getValue() == null || !PostCodeValidator.isPostCode(postMap.getValue())) {
                                invalidMap.put(postMap.getKey(), postMap.getValue());
                            } else if (postMap.getValue() != null && PostCodeValidator.isPostCode(postMap.getValue())) {
                                validMap.put(postMap.getKey(), postMap.getValue());
                            }
                        }
                );

        writeCsvFile("failed_validation.csv", getHeaderString(), toCsvRow(invalidMap));
        writeCsvFile("succeeded_validation.csv", getHeaderString(), toCsvRow(validMap));
    }

    public static void writeCsvFile(String fileNamePath, String header, String content) {
        Path path = Paths.get(fileNamePath);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(header);
            writer.newLine();
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final long startTime = Instant.now().toEpochMilli();

        BulkImport bi = null;
        try {
            File file = new File("src/main/resources/import_data.csv");
            Path path = Paths.get(file.getAbsolutePath());
            Reader reader = Files.newBufferedReader(
                    path, Charset.forName("UTF-8"));
            bi = new BulkImport(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Map<Integer, String> records = bi.readRecords();

        //writeCsvFile("failed_validation.csv", bi.getHeaderString(), toCsvRow(postcodeValidity(records)));

        postcodeValidity2CSV(records);

        final long stopTime = Instant.now().toEpochMilli();

        System.out.println("Took: " + (stopTime - startTime) + "ms to complete the process");
    }
}
