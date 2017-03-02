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
 * And Task 3 for Performance engineering
 *
 * This class deals with following tasks:
 * - Reading import_data.csv file which consists of columns 'row_id' and 'postcode'
 * - Temporarily storing data in a List or a Map (based on user's selection)
 * - Sorting the data on row_id column (ascending order)
 * - Validating the postcodes
 * - Saving in temporary storage for valid and invalid postcodes retaining their row_id and postcodes
 * - Writing the data in failed_validation.csv and succeeded_validation.csv files respectively in
 * root of project structure
 * - Log performance timings for user to identify performance bottlenecks like
 * File IOs or validation process using Maps v/s Lists
 */

public class BulkImport {
    // SEPARATOR is the character to split data from csv file
    private static final String SEPARATOR = ",";

    // source is going to be used by other classes to set object and used by readRecords() or readRecordsList() methods
    private final Reader source;

    // header is the first column header from import_data.csv file
    private static List<String> header;

    /*
     * Constructor class for adding source path of file
     * @param source is the Reader object
     */
    BulkImport(Reader source) {
        this.source = source;
    }

    /*
     * This method is used for setting header
     * @param header is an ArrayList of String
     */
    private void setHeader(List<String> header) {
        this.header = header;
    }

    /*
     * This method is used for getting stored header info, for writing in csv file
     * @return List<String> is going to have [row_id, postcode]
     */
    public List<String> getHeader() {
        return header;
    }

    /*
     * This method shall return header column for csv file
     * @return String version of header
     */
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

    /*
     * This lambda function separates values from a String and creates a List<String>
     * @param String of `row_id,postcode`
     * @return Function<String, List<String>> is the list of rows for lambda map function
     */
    Function<String, List<String>> mapper = line -> Arrays.asList(line.split(SEPARATOR));

    /*
     * This lambda function will create an object of Postcode class identifying row_id and postcode values
     * @param String will be split to it's respective data values
     * @return Function<String,Postcode> is Postcode object map for lambda map function
     */
    @SuppressWarnings("unchecked")
    public static Function<String, Postcode> maptoPostCode = (line) -> {
        String[] p = line.split(SEPARATOR);
        return new Postcode(Integer.parseInt(p[0]), p[1]);
    };

    /*
     * This lambda function is used as a filter to identify previously seen postcodes/row_ids
     * @param keyExtractor is the key which is supplied to this lambda function
     * @return boolean value if the key was seen before
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /*
     * This method shall read all the records and return a Map.
     * It uses BufferedReader to read lines from a csv file
     * filter is also mentioned for identifying previously seen postcodes. This takes more time and memory to process
     * @return Map<Integer,String> for dictionary Map of {row_id=postcode, row_id=postcode,...} format
     */
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

    /*
     * This method shall read all the records and return a List of List<String>.
     * It uses BufferedReader to read lines from a csv file
     * filter is also mentioned for identifying previously seen postcodes. This takes more time and memory to process
     * This method also sorts the data while reading from buffer
     * @return List<List<String>> for data structure List of [[row_id, postcode],[row_id, postcode],...] format
     */
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

    /*
     * This method takes in Map of records and flattens to a String format
     * @return String with '\n' newline characters in them, for buffer to move to next line while writing to a file
     */
    public static String toCsvRow(Map<Integer, String> records) {
        return records.entrySet()
                .stream()
                .map(map -> map.toString().replaceAll("=", ","))
                .collect(Collectors.joining("\n"));
    }

    /*
     * This method takes in List of records and flattens to a String format
     * @return String with '\n' newline characters in them, for buffer to move to next line while writing to a file
     */
    public static String toCsvRow(List<List<String>> records) {
        return records.stream()
                .map(list -> list.toString().replaceAll("\\s", ""))
                .map(listsa -> listsa.substring(1, listsa.length() - 1)) // to remove [] brackets
                .collect(Collectors.joining("\n"));
    }

    /*
     * This method shall iterate through List of records and validate against postcode regex
     * Two temporary Lists are created for valid/invalid data to prevent continuous File disk IOs
     * Data is written in csv files once the validation of all records is complete
     * Time records are calculated and logged to measure the performance of features in this method
     * @param records is List of records in following format [[row_id, postcode],[row_id, postcode],...]
     */
    public static void postcodeValidity2CSV(List<List<String>> records) {
        List<List<String>> invalidList = new ArrayList<>();
        List<List<String>> validList = new ArrayList<>();

        final long startValidityTime = Instant.now().toEpochMilli();
        records.stream().forEach(
                list -> {
                    if (list.get(1) == null || !PostCodeValidator.isPostCode(list.get(1))) {
                        invalidList.add(list);
                    } else if (list.get(1) != null && PostCodeValidator.isPostCode(list.get(1))) {
                        validList.add(list);
                    }
                }
        );
        final long stopValidityTime = Instant.now().toEpochMilli();

        System.out.println(invalidList.size() + " number of invalid postcodes found");
        System.out.println(validList.size() + " number of valid postcodes found");

        final long startWriteTime = Instant.now().toEpochMilli();

        writeCsvFile("failed_validation.csv", getHeaderString(), toCsvRow(invalidList));
        writeCsvFile("succeeded_validation.csv", getHeaderString(), toCsvRow(validList));

        final long stopWriteTime = Instant.now().toEpochMilli();

        System.out.println();
        System.out.println("Validating postcode List records took " + (stopValidityTime - startValidityTime) + " millisecond(s)");
        System.out.println();
        System.out.println("Writing List to csv file took " + (stopWriteTime - startWriteTime) + " millisecond(s)");
        System.out.println();
    }

    /*
     * This method shall iterate through Map of records and validate against postcode regex
     * Two temporary SortedMaps are created for valid/invalid data to prevent continuous File disk IOs
     * Data is sorted on row_id variable in ascending order
     * Data is written in csv files once the validation of all records is complete
     * Time records are calculated and logged to measure the performance of features in this method
     * @param records is Map of records
     */
    public static void postcodeValidity2CSV(Map<Integer, String> records) {
        SortedMap<Integer, String> invalidMap = new TreeMap<>();
        SortedMap<Integer, String> validMap = new TreeMap<>();

        Comparator<Map.Entry<Integer, String>> byKey = (entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey());

        final long startValidityTime = Instant.now().toEpochMilli();
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
        final long stopValidityTime = Instant.now().toEpochMilli();

        System.out.println(invalidMap.keySet().size() + " number of invalid postcodes found");
        System.out.println(validMap.keySet().size() + " number of valid postcodes found");

        final long startWriteTime = Instant.now().toEpochMilli();

        writeCsvFile("failed_validation.csv", getHeaderString(), toCsvRow(invalidMap));
        writeCsvFile("succeeded_validation.csv", getHeaderString(), toCsvRow(validMap));

        final long stopWriteTime = Instant.now().toEpochMilli();

        System.out.println();
        System.out.println("Validating postcode Map records took " + (stopValidityTime - startValidityTime) + " millisecond(s)");
        System.out.println();
        System.out.println("Writing Map to csv file took " + (stopWriteTime - startWriteTime) + " millisecond(s)");
        System.out.println();
    }

    /*
     * This method is for writing data in csv file.
     * @param fileNamePath is the file name to be supplied with it's path. Provide csv filename if the output needs to be in project root DIR
     * @param header is the header columns for csv file
     * @param content is the content to be added in the file
     */
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

    /*
     * This method is for running the program from commandline.
     * It will ask for two options and will process the csv file read, postcode validation and
     * writing data in valid/invalid postcode csv files in root DIR of the project
     * It is important to note that it is expecting import_data.csv in src/main/resources/ of this project
     */
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

                postcodeValidity2CSV(li);

                break;
            case 2:
                startReadRecordsTime = Instant.now().toEpochMilli();
                Map<Integer, String> records = bi.readRecords();
                stopReadRecordsTime = Instant.now().toEpochMilli();

                postcodeValidity2CSV(records);
                break;
            default:
                System.out.println("Had to choose 1 or 2! Using 2 as default..");

                startReadRecordsTime = Instant.now().toEpochMilli();
                records = bi.readRecords();
                stopReadRecordsTime = Instant.now().toEpochMilli();

                postcodeValidity2CSV(records);
                break;
        }

        final long stopTime = Instant.now().toEpochMilli();

        System.out.println("Reading records from import_data.csv took " + (stopReadRecordsTime - startReadRecordsTime) + " millisecond(s)");
        System.out.println();
        System.out.println("It took total time of " + (stopTime - startTime) + " millisecond(s) to complete the process");
        System.out.println();
    }
}
