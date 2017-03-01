package com.postcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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

    @SuppressWarnings("unchecked")
    public static Function<String, Postcode> maptoPostCode = (line) -> {
        String[] p = line.split(SEPARATOR);
        return new Postcode(Integer.parseInt(p[0]), p[1]);
    };

    public Map<Integer, String> readRecords(){
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
}
