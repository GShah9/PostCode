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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Gautam on 27/02/2017.
 *
 * This includes solution for task 2 of Postcode import
 * link - https://gist.github.com/edhiley/5da612c93e31c7e60355#part-2---bulk-import
 *
 * And Task 3 for Performance engineering
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

    Function<String, List<String>> mapper = line -> Arrays.asList(line.split(SEPARATOR));

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

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public Map<Integer, String> readRecords() {
        BufferedReader br = null;

        try {
            br = new BufferedReader(source);
            setHeader(br.lines()
                    .findFirst()
                    .map(mapper)
                    .get());
            return br.lines()
                    .map(maptoPostCode)
                    //.filter(distinctByKey(pc -> pc.getPostcode())) //to prevent duplicate Postcode
                    .collect(Collectors.toMap(Postcode::getRowId, Postcode::getPostcode));
        } finally {
            try {
                br.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    public List<List<String>> readRecordsList() {
        try (BufferedReader reader = new BufferedReader(source)) {
            setHeader(reader.lines()
                    .findFirst()
                    .map(mapper)
                    .get());
            return reader.lines()
                    .map(mapper)
                    //.filter(distinctByKey(pc -> pc.get(1))) //to prevent duplicate Postcode
                    .sorted((f1, f2) -> Integer.compare(Integer.parseInt(f1.get(0)), Integer.parseInt(f2.get(0))))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String toCsvRow(Map<Integer, String> records) {
        return records.entrySet()
                .stream()
                .map(map -> map.toString().replaceAll("=", ","))
                .collect(Collectors.joining("\n"));
    }

    public static String toCsvRow(List<List<String>> records) {
        return records.stream()
                .map(list -> list.toString().replaceAll("\\s", ""))
                .map(listsa -> listsa.substring(1, listsa.length() - 1)) // to remove [] brackets
                .collect(Collectors.joining("\n"));
    }

    public static void postcodeValidity2CSV(List<List<String>> records) {
        List<List<String>> invalidList = new ArrayList<>();
        List<List<String>> validList = new ArrayList<>();

        records.stream().forEach(
                list -> {
                    if (list.get(1) == null || !PostCodeValidator.isPostCode(list.get(1))) {
                        invalidList.add(list);
                    } else if (list.get(1) != null && PostCodeValidator.isPostCode(list.get(1))) {
                        validList.add(list);
                    }
                }
        );

        System.out.println(invalidList.size() + " number of invalid postcodes found");
        System.out.println(validList.size() + " number of valid postcodes found");

        final long startWriteTime = Instant.now().toEpochMilli();

        writeCsvFile("failed_validation.csv", getHeaderString(), toCsvRow(invalidList));
        writeCsvFile("succeeded_validation.csv", getHeaderString(), toCsvRow(validList));

        final long stopWriteTime = Instant.now().toEpochMilli();

        System.out.println();
        System.out.println("Writing List to csv file took " + (stopWriteTime - startWriteTime) + " millisecond(s)");
        System.out.println();
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

        System.out.println(invalidMap.keySet().size() + " number of invalid postcodes found");
        System.out.println(validMap.keySet().size() + " number of valid postcodes found");

        final long startWriteTime = Instant.now().toEpochMilli();

        writeCsvFile("failed_validation.csv", getHeaderString(), toCsvRow(invalidMap));
        writeCsvFile("succeeded_validation.csv", getHeaderString(), toCsvRow(validMap));

        final long stopWriteTime = Instant.now().toEpochMilli();

        System.out.println();
        System.out.println("Writing Map to csv file took " + (stopWriteTime - startWriteTime) + " millisecond(s)");
        System.out.println();
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
        System.out.println();
        System.out.println("Please type in the <number> 1 or 2:");
        System.out.println("1) To process csv validation using Lists");
        System.out.println("2) To process csv validation using Maps");
        System.out.println();

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

        final long startReadRecordsTime;
        final long stopReadRecordsTime;
        final long startValidityTime;
        final long stopValidityTime;

        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextInt()) scan.next();
        int i = scan.nextInt();

        System.out.println("Please wait...");

        final long startTime = Instant.now().toEpochMilli();

        switch (i) {
            case 1:
                startReadRecordsTime = Instant.now().toEpochMilli();

                List<List<String>> li = bi.readRecordsList();

                stopReadRecordsTime = Instant.now().toEpochMilli();
                startValidityTime = Instant.now().toEpochMilli();

                postcodeValidity2CSV(li);

                stopValidityTime = Instant.now().toEpochMilli();
                break;
            case 2:
                startReadRecordsTime = Instant.now().toEpochMilli();

                Map<Integer, String> records = bi.readRecords();

                stopReadRecordsTime = Instant.now().toEpochMilli();
                startValidityTime = Instant.now().toEpochMilli();

                postcodeValidity2CSV(records);

                stopValidityTime = Instant.now().toEpochMilli();
                break;
            default:
                System.out.println("Had to choose 1 or 2! Using 2 as default..");
                startReadRecordsTime = Instant.now().toEpochMilli();

                records = bi.readRecords();

                stopReadRecordsTime = Instant.now().toEpochMilli();
                startValidityTime = Instant.now().toEpochMilli();

                postcodeValidity2CSV(records);

                stopValidityTime = Instant.now().toEpochMilli();
                break;
        }

        final long stopTime = Instant.now().toEpochMilli();

        System.out.println("Reading records from import_data.csv took " + (stopReadRecordsTime - startReadRecordsTime) + " millisecond(s)");
        System.out.println();
        System.out.println("Validating records took " + (stopValidityTime - startValidityTime) + " millisecond(s)");
        System.out.println();
        System.out.println("It took total time of " + (stopTime - startTime) + " millisecond(s) to complete the process");
        System.out.println();
    }
}
