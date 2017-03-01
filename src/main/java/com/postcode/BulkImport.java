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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private List<String> header;

    BulkImport(Reader source) {
        this.source = source;
    }

    private void setHeader(List<String> header) {
        this.header = header;
    }

    public List<String> getHeader() {
        return header;
    }

    public String getHeaderString() {
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
        Map<Integer, String> newMap = new HashMap<Integer, String>();
        records.entrySet()
                .stream()
                .forEach(
                        postMap -> {
                            if (postMap.getValue() != null && !PostCodeValidator.isPostCode(postMap.getValue())) {
                                newMap.put(postMap.getKey(), postMap.getValue());
                            } else {
                                // add your map for valid postcodes
                            }
                        }
                );
        return newMap;
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
        //System.out.println(toCsvRow(records));
        //writeCsvFile("testname.csv", bi.getHeaderString(), toCsvRow(records));
        writeCsvFile("failed_validation.csv", bi.getHeaderString(), toCsvRow(postcodeValidity(records)));
    }
}
